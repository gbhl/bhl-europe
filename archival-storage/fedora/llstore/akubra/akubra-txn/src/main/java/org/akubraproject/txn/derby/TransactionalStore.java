/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2009-2010 DuraSpace
 * http://duraspace.org
 *
 * In collaboration with Topaz Inc.
 * http://www.topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akubraproject.txn.derby;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.XAConnection;
import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.derby.tools.sysinfo;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.txn.AbstractTransactionalStore;

/**
 * A simple transactional store using Derby db for the transaction logging and id mappings. It
 * provides snapshot isolation with fail-fast semantics, meaning it will immediately throw a
 * {@link org.akubraproject.txn.ConcurrentBlobUpdateException ConcurrentBlobUpdateException}
 * if a transaction tries to modify (insert, delete, or overwrite) a blob which was modified by
 * another transaction since the start of the first transaction (even if the change by the other
 * transaction hasn't been committed yet). The assumption is that rollbacks are rare and that it is
 * better to be notified of a conflict immediately rather than wasting time uploading large amounts
 * of data that will just have to be deleted again.
 *
 * <p>In general a transaction must be considered failed and should be rolled back after any
 * exception occurred.
 *
 * <p>This store must be configured with exactly one underlying blob-store. It supports arbitrary
 * application-ids and maps them to the underlying blob-store's id's; it currently requires that
 * the underlying blob-store to be capable of generating ids.
 *
 * <p>Snapshot isolation is implemented using a MVCC design as follows. A name-map holds a list of
 * versioned id mappings which maps application-ids to underlying store-ids; in addition, each
 * mapping has two flags indicating whether the mapping has been deleted and whether it has been
 * committed. When a transaction starts it is given a read version number (these increase
 * monotonically); only committed map entries with a version less than this read version or
 * uncommitted entries with a version the same as the read version will be read; if there are
 * multiple such entries for a given app-id, then the one with the highest version is used. If the
 * transaction makes a change (adding, removing, replacing, etc), a new entry in recorded in the
 * map with the version set to the read-version and with the committed flag set to false. On commit
 * the transaction is assigned a write version number (which is higher than any previously issued
 * read version numbers) and which it then sets on all entries written as part of this transaction;
 * it also sets the committed flag to true on these entries.
 *
 * <p>Old entries (and the underlying blobs) are cleaned out as they become unreferenced, i.e. when
 * no active transaction could refer to them anymore. In order to speed up the discovery of such
 * entries, a separate deleted-list is kept into which an entry is made each time an entry in the
 * main map is marked as deleted and each time a blob is marked as deleted. This list is processed
 * at the end of every transaction and upon startup (on startup the list is completely cleared as
 * there are no active transactions).
 *
 * <p><em>A note on locking</em>: Derby, even in read-uncommitted mode, likes to acquire exclusive
 * locks on rows when doing inserts, deletes, and updates. This would be ok, except that it
 * sometimes attempts to lock rows it won't change. This can lead to deadlocks. The way around this
 * that I've found is to ensure Derby always uses an index when searching for the rows to update or
 * delete. This is accomplished by giving the optimizer explicit instructions via the
 * <var>DERBY-PROPERTIES</var> directive in the queries. Since this directive is only supported in
 * select statements, all updates and deletes are done via updatable queries (result-sets). This
 * actually performs about the same as a direct update or delete statement. See also the thread <a
 * href="http://mail-archives.apache.org/mod_mbox/db-derby-user/200903.mbox/%3c20090330092451.GD26813@innovation.ch%3e">disabling locking</a> (<a
 * href="http://mail-archives.apache.org/mod_mbox/db-derby-user/200904.mbox/%3c20090401001750.GB5281@innovation.ch%3e">continued</a>),
 * or at <a href="http://news.gmane.org/find-root.php?message_id=%3c20090330092451.GD26813%40innovation.ch%3e">gmane</a>.
 * Unfortunately, however, this does not seem to be sufficient: Derby may still lock other rows, as
 * documented in <a
 * href="http://db.apache.org/derby/docs/10.4/devguide/rdevconcepts8424.html">Scope of locks</a>
 * in Derbys's developer guide. When this happens, the wait for the lock will eventually time out
 * and an exception will be thrown. However, I have not enountered this issue so far. But a related
 * issue is present in 10.4 and earlier, namely <a
 * href="https://issues.apache.org/jira/browse/DERBY-2991">DERBY-2991</a>; testing with 10.5
 * indicates this issue has been resolved. For these reasons a flag is provided to restrict the
 * number of concurrent write-transactions to one, and the
 * {@link #TransactionalStore(URI, BlobStore, String) three-argument-constructor} will set this
 * single-writer flag to true for derby 10.4 and earlier.
 *
 * @author Ronald Tschalär
 */
public class TransactionalStore extends AbstractTransactionalStore {
  /** The SQL table used by this store to hold the name mappings */
  public static final String NAME_TABLE = "NAME_MAP";
  /** The SQL table used by this store to hold the list of deleted blobs */
  public static final String DEL_TABLE  = "DELETED_LIST";

  private static final Logger logger = LoggerFactory.getLogger(TransactionalStore.class);

  private final EmbeddedXADataSource dataSource;
  private final Set<Long>            activeTxns = new HashSet<Long>();
  private final Set<URI>             uriLocks = new HashSet<URI>();
  private final boolean              singleWriter;
  private       long                 nextVersion;
  private       long                 writeVersion = -1;
  private       long                 writeLockHolder = -1;
  private       boolean              purgeInProgress = false;
  private       int                  numPurgesDelayed = 0;
  private       boolean              started = false;

  /**
   * Create a new transactional store. The single-writer flag will be determined automatically
   * depending on the version of derby being used.
   *
   * @param id           the id of this store
   * @param wrappedStore the wrapped non-transactional store
   * @param dbDir        the directory to use to store the transaction information
   * @throws IOException if there was an error initializing the db
   */
  public TransactionalStore(URI id, BlobStore wrappedStore, String dbDir) throws IOException {
    this(id, wrappedStore, dbDir, needSingleWriter());
  }

  private static boolean needSingleWriter() {
    return sysinfo.getMajorVersion() < 10 ||
           sysinfo.getMajorVersion() == 10 && sysinfo.getMinorVersion() < 5;
  }

  /**
   * Create a new transactional store.
   *
   * @param id           the id of this store
   * @param wrappedStore the wrapped non-transactional store
   * @param dbDir        the directory to use to store the transaction information
   * @param singleWriter if true, serialize all writers to avoid all locking issues with
   *                     Derby; if false, some transactions may fail sometimes due to
   *                     locks timing out
   * @throws IOException if there was an error initializing the db
   */
  public TransactionalStore(URI id, BlobStore wrappedStore,
                            String dbDir, boolean singleWriter) throws IOException {
    super(id, wrappedStore);
    this.singleWriter = singleWriter;

    //TODO: redirect logging to logger
    //System.setProperty("derby.stream.error.logSeverityLevel", "50000");
    //System.setProperty("derby.stream.error.file", new File(base, "derby.log").toString());
    //System.setProperty("derby.language.logStatementText", "true");
    //System.setProperty("derby.stream.error.method", "java.sql.DriverManager.getLogStream");

    dataSource = new EmbeddedXADataSource();
    dataSource.setDatabaseName(dbDir);
    dataSource.setCreateDatabase("create");

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          dataSource.setShutdownDatabase("shutdown");
          dataSource.getXAConnection().getConnection();
        } catch (Exception e) {
          logger.warn("Error shutting down derby", e);
        }
      }
    });

    createTables();
    nextVersion = findYoungestVersion() + 1;
    logger.info("TransactionalStore started: dbDir='" + dbDir + "', version=" + nextVersion);
  }

  private void createTables() throws IOException {
    runInCon(new Action<Void>() {
      public Void run(Connection con) throws SQLException {
        // test if table exists
        ResultSet rs = con.getMetaData().getTables(null, null, NAME_TABLE, null);
        try {
          if (rs.next())
            return null;
        } finally {
          rs.close();
        }

        // nope, so create it
        logger.info("Creating tables and indexes for name-map");

        Statement stmt = con.createStatement();
        try {
          stmt.execute("CREATE TABLE " + NAME_TABLE +
                       " (appId VARCHAR(1000) NOT NULL, storeId VARCHAR(1000) NOT NULL, " +
                       "  version BIGINT NOT NULL, deleted SMALLINT, committed SMALLINT)");
          stmt.execute("CREATE INDEX " + NAME_TABLE + "_AIIDX ON " + NAME_TABLE + "(appId)");
          stmt.execute("CREATE INDEX " + NAME_TABLE + "_VIDX ON " + NAME_TABLE + "(version)");

          stmt.execute("CREATE TABLE " + DEL_TABLE + " (appId VARCHAR(1000) NOT NULL, " +
                       " storeId VARCHAR(1000), version BIGINT NOT NULL)");
          stmt.execute("CREATE INDEX " + DEL_TABLE + "_VIDX ON " + DEL_TABLE + "(version)");

          // ensure Derby never uses table-locks, only row-locks
          stmt.execute(
            "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.escalationThreshold', '" +
            Integer.MAX_VALUE + "')");

          // we should really never be waiting for a lock let alone deadlock, but just in case
          stmt.execute(
            "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.deadlockTimeout', '30')");
        } finally {
          stmt.close();
        }

        return null;
      }
    }, "Failed to create tables");
  }

  private long findYoungestVersion() throws IOException {
    return runInCon(new Action<Long>() {
      public Long run(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        try {
          stmt.setMaxRows(1);
          ResultSet rs =                                // NOPMD
              stmt.executeQuery("SELECT version FROM " + NAME_TABLE + " ORDER BY version DESC");
          return rs.next() ? rs.getLong(1) : -1L;
        } finally {
          stmt.close();
        }
      }
    }, "Failed to find youngest version");
  }

  /**
   * @throws IllegalStateException if no backing store has been set yet
   */
  @Override
  public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
      throws IllegalStateException, IOException {
    long version;
    synchronized (this) {

      if (!started) {
        started = true;
        purgeOldVersions(0);
      }

      while (writeVersion >= 0 && nextVersion == writeVersion) {
        if (logger.isDebugEnabled())
          logger.debug("Out of available versions - waiting for write-lock to be released");

        try {
          wait();
        } catch (InterruptedException ie) {
          throw new IOException("wait for write-lock interrupted", ie);
        }
      }

      version = nextVersion++;

      boolean isNew = activeTxns.add(version);
      assert isNew : "duplicate version " + version;
    }

    boolean ok = false;
    try {
      XAConnection xaCon;
      Connection   con;
      synchronized (dataSource) {
        xaCon = dataSource.getXAConnection();
        con   = xaCon.getConnection();
      }

      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

      tx.enlistResource(xaCon.getXAResource());

      BlobStoreConnection bsc =
          new TransactionalConnection(this, wrappedStore, xaCon, con, tx, hints, version);

      if (logger.isDebugEnabled())
        logger.debug("Opened connection, read-version=" + version);

      ok = true;
      return bsc;
    } catch (IOException ioe) {
      throw ioe;
    } catch (Exception e) {
      throw new IOException("Error connecting to db", e);
    } finally {
      if (!ok) {
        synchronized (this) {
          activeTxns.remove(version);
        }
      }
    }
  }

  boolean singleWriter() {
    return singleWriter;
  }

  /**
   * Acquire the write lock. This is a simple, re-entrant lock without a lock count. If the lock
   * is already held this will block until it is free.
   *
   * @param version the version acquiring the lock
   * @throws InterruptedException if waiting for the lock was interrupted
   */
  synchronized void acquireWriteLock(long version) throws InterruptedException {
    while (writeLockHolder >= 0 && writeLockHolder != version)
      wait();

    if (logger.isTraceEnabled())
      logger.trace("Transaction " + version + " acquired write lock");

    writeLockHolder = version;
  }

  /**
   * Release the write lock. This always completely releases lock no matter how often {@link
   * #acquireWriteLock} was invoked.
   *
   * @param version the version that acquired the lock
   * @throws IllegalStateException if the lock is not held by <var>version</var>
   */
  synchronized void releaseWriteLock(long version) {
    if (writeLockHolder != version)
      throw new IllegalStateException("Connection '" + version + "' is not the holder of the " +
                                      "write lock; '" + writeLockHolder + "' is");

    if (logger.isTraceEnabled())
      logger.trace("Transaction " + version + " released write lock");

    writeLockHolder = -1;
    notifyAll();
  }

  /**
   * Acquire a lock on the given URI. Each lock for each URI is a simple, non-reentrant lock and
   * each lock for each URI is independent of the others. If the lock is already held this will
   * block until it is free.
   *
   * @param uri the URI for which to acquire the lock
   * @throws InterruptedException if waiting for the lock was interrupted
   */
  void acquireUriLock(URI uri) throws InterruptedException {
    synchronized (uriLocks) {
      while (uriLocks.contains(uri))
        uriLocks.wait();

      uriLocks.add(uri);
    }
  }

  /**
   * Release the lock on the given URI.
   *
   * @param uri the URI for which to release the lock
   * @throws IllegalStateException if the lock was not held
   */
  void releaseUriLock(URI uri) throws IllegalStateException {
    synchronized (uriLocks) {
      if (!uriLocks.remove(uri))
        throw new IllegalStateException("Uri lock for <" + uri + "> was not held");

      uriLocks.notifyAll();
    }
  }

  /**
   * Prepare the transaction. This acquires the write-lock and hence must always be followed by
   * {@link #txnComplete} to release it.
   *
   * @param numMods the number of modifications made during this transaction; this is used
   *                to estimate how long the commit might take
   * @param version the transaction's read-version - used for logging
   * @return the write version
   * @throws InterruptedException if interrupted while waiting for the write-lock
   */
  synchronized long txnPrepare(int numMods, long version) throws InterruptedException {
    if (logger.isDebugEnabled())
      logger.debug("Preparing transaction " + version);

    acquireWriteLock(version);

    /* Leave a little space in the version number sequence so other transactions may start while
     * this one completes. The constant '1/100' is pulled out of thin air, and represents a guess
     * on the upper bound on how many transactions are likely to be started during the time it
     * takes this one to complete; if it is too large then we just have larger holes and the
     * transaction numbers jump more than necessary, which isn't tragic as long as the jumps are
     * not so large that we run into a real possibility of version number wrap-around; if it is too
     * small then that just means transactions may be needlessly held up waiting for this one to
     * complete. Also, we always leave a little extra room to account for the fact that there's a
     * semi-fixed overhead that a commit will take even if there are only a few changes.
     */
    writeVersion = Math.max(nextVersion + numMods / 100, 10);

    if (logger.isDebugEnabled())
      logger.debug("Prepared transaction " + version + ", write-version=" + writeVersion);

    return writeVersion;
  }

  /**
   * Signal that the transaction is complete. This must always be invoked.
   *
   * @param committed whether the transaction was committed or rolled back
   * @param version   the transaction's read-version
   */
  synchronized void txnComplete(boolean committed, long version) {
    if (logger.isDebugEnabled())
      logger.debug("Transaction " + version + " completed " +
                   (committed ? "(committed)" : "(rolled back)"));

    boolean wasActive = activeTxns.remove(version);
    assert wasActive : "completed unknown transaction " + version +
                       (committed ? "(committed)" : "(rolled back)");

    if (writeLockHolder != version)
      return;           // never prepared (e.g. r/o txn, or rollback)

    if (committed && writeVersion >= 0)
      nextVersion = writeVersion + 1;
    writeVersion = -1;

    releaseWriteLock(version);
  }

  /**
   * Purge all old versions that are not being used anymore.
   *
   * @param lastCompletedVersion the version of the recently completed transaction; if there are
   *                             other, older transactions still active then the purge can be
   *                             avoided, i.e. this is just for optimization.
   */
  void purgeOldVersions(long lastCompletedVersion) {
    final long minVers;
    synchronized (this) {
      minVers = activeTxns.isEmpty() ? nextVersion : Collections.min(activeTxns);
      if (minVers < lastCompletedVersion)
        return;           // we didn't release anything

      /* Derby has issues trying to run multiple purges in parallel (NPE's, waiting for
       * locks that should be held by anybody, and even deadlocks). Also, there isn't
       * that much point in running multiple purges simultaneously, as the next purge
       * will clean up stuff too.
       *
       * However, just short-circuiting here if a purge is already in progress can cause
       * the purging to fall seriously behind under load (in a sort of negative feedback
       * loop: the more it falls behind, the longer it takes to catch up, the more it
       * falls behind, ...). Hence we keep track of how many times we've skipped the
       * purge and after some threshhold we start blocking to let the purge catch up.
       */
      while (purgeInProgress) {
        if (numPurgesDelayed < 10) {
          numPurgesDelayed++;
          return;
        }

        try {
          wait();
        } catch (InterruptedException ie) {
          throw new RuntimeException("Interrupted waiting for purge lock", ie);
        }
      }

      purgeInProgress  = true;
      numPurgesDelayed = 0;
    }

    try {
      if (singleWriter)
        acquireWriteLock(lastCompletedVersion);

      runInCon(new Action<Void>() {
        public Void run(Connection con) throws SQLException {
          if (logger.isDebugEnabled())
            logger.debug("Purging deleted blobs older than revision " + minVers);

          // clean out stale mapping entries
          PreparedStatement findOld = con.prepareStatement(
              "SELECT appId, version FROM " + DEL_TABLE + " WHERE version < ?");
          findOld.setLong(1, minVers);
          ResultSet rs = findOld.executeQuery();        // NOPMD
          int cntM = 0;

          try {
            if (!rs.next())
              return null;

            PreparedStatement purge = con.prepareStatement(
                "SELECT version FROM " + NAME_TABLE + " -- DERBY-PROPERTIES index=NAME_MAP_AIIDX \n" +
                " WHERE appId = ? AND (version < ? OR version = ? AND deleted <> 0)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            do {
              purge.setString(1, rs.getString(1));
              purge.setLong(2, rs.getLong(2));
              purge.setLong(3, rs.getLong(2));

              ResultSet rs2 = purge.executeQuery();     // NOPMD
              try {
                while (rs2.next()) {
                  cntM++;
                  rs2.deleteRow();
                }
              } finally {
                rs2.close();
              }
            } while (rs.next());

            purge.close();
          } finally {
            try {
              rs.close();
            } finally {
              findOld.close();
            }
          }

          // remove unreferenced blobs
          findOld = con.prepareStatement(
              "SELECT storeId FROM " + DEL_TABLE + " WHERE version < ? AND storeId IS NOT NULL");
          findOld.setLong(1, minVers);
          rs = findOld.executeQuery();
          int cntB = 0;

          try {
            BlobStoreConnection bsc = wrappedStore.openConnection(null, null);
            try {
              while (rs.next()) {
                cntB++;
                String storeId = rs.getString(1);
                if (logger.isTraceEnabled())
                  logger.trace("Purging deleted blob '" + storeId + "'");

                try {
                  bsc.getBlob(URI.create(storeId), null).delete();
                } catch (IOException ioe) {
                  logger.warn("Error purging blob '" + storeId + "'", ioe);
                }
              }
            } finally {
              bsc.close();
            }
          } catch (IOException ioe) {
            logger.warn("Error opening connection to underlying store to purge old versions", ioe);
          } finally {
            try {
              rs.close();
            } finally {
              findOld.close();
            }
          }

          // purge processed entries from the delete table
          String sql = "SELECT version FROM " + DEL_TABLE +
                       " -- DERBY-PROPERTIES index=DELETED_LIST_VIDX \n WHERE version < ?";
          PreparedStatement purge =
              con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
          purge.setLong(1, minVers);
          rs = purge.executeQuery();
          int cntD = 0;

          try {
            while (rs.next()) {
              cntD++;
              rs.deleteRow();
            }
          } finally {
            try {
              rs.close();
            } finally {
              purge.close();
            }
          }

          // debug log the stats
          try {
            int cntL = 0;
            if (logger.isTraceEnabled()) {
              BlobStoreConnection bsc = wrappedStore.openConnection(null, null);
              for (Iterator<URI> iter = bsc.listBlobIds(null); iter.hasNext(); iter.next())
                cntL++;
              bsc.close();
            }

            if (logger.isDebugEnabled())
              logger.debug("purged: " + cntM + " mappings, " + cntB + " blobs, " + cntD +
                           " deletes" + (logger.isTraceEnabled() ? "; " + cntL + " blobs left" : ""));
          } catch (Exception e) {
            e.printStackTrace();
          }

          return null;
        }
      }, "Error purging old versions");
    } catch (Exception e) {
      logger.warn("Error purging old versions", e);
    } finally {
      try {
        if (singleWriter)
          releaseWriteLock(lastCompletedVersion);
      } finally {
        synchronized (this) {
          purgeInProgress = false;
          notifyAll();
        }
      }
    }
  }

  private <T> T runInCon(Action<T> action, String errMsg) throws IOException {
    try {
      XAConnection xaCon;
      Connection   con;
      synchronized (dataSource) {
        xaCon = dataSource.getXAConnection();
        con   = xaCon.getConnection();
      }

      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      con.setAutoCommit(false);
      boolean committed = false;

      try {
        T res = action.run(con);
        con.commit();
        committed = true;
        return res;
      } finally {
        if (!committed) {
          try {
            con.rollback();
          } catch (SQLException sqle) {
            logger.error("Error rolling back after failure", sqle);
          }
        }
        xaCon.close();
      }
    } catch (SQLException sqle) {
      throw new IOException(errMsg, sqle);
    }
  }

  private static interface Action<T> {
    public T run(Connection con) throws SQLException;
  }
}
