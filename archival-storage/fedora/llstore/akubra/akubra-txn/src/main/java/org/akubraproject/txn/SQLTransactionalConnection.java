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

package org.akubraproject.txn;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.XAConnection;
import javax.transaction.Transaction;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.PeekingIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;

/**
 * A basic superclass for sql-based transactional store connections. This just closes the sql
 * connection at the end and provides an Iterator implementation for implementing
 * {@link BlobStoreConnection#listBlobIds listBlobIds}.
 *
 * @author Ronald Tschalär
 */
public abstract class SQLTransactionalConnection extends AbstractTransactionalConnection {
  private static final Logger logger = LoggerFactory.getLogger(SQLTransactionalConnection.class);

  /** The xa connection being used */
  protected final XAConnection xaCon;
  /** The db connection being used */
  protected final Connection   con;

  /**
   * Create a new sql-based transactional connection.
   *
   * @param owner   the blob-store we belong to
   * @param bStore  the underlying blob-store to use
   * @param xaCon   the xa connection to use
   * @param con     the db connection to use
   * @param tx      the transaction we belong to
   * @param hints   the hints to pass to <code>openConnection<code> on <var>bStore</var>
   * @throws IOException if an error occurs initializing this connection
   */
  protected SQLTransactionalConnection(BlobStore owner, BlobStore bStore, XAConnection xaCon,
                                       Connection con, Transaction tx, Map<String, String> hints)
      throws IOException {
    super(owner, bStore, tx, hints);
    this.con   = con;
    this.xaCon = xaCon;
  }

  @Override
  public void afterCompletion(int status) {
    if (isCompleted)
      return;

    try {
      xaCon.close();
    } catch (SQLException sqle) {
      logger.error("Error closing db connection", sqle);
    } finally {
      super.afterCompletion(status);
    }
  }

  /**
   * A ResultSet based Iterator of blob-id's. Useful for {@link BlobStoreConnection#listBlobIds
   * listBlobIds}.
   *
   * @author Ronald Tschalär
   */
  protected static class RSBlobIdIterator extends AbstractIterator<URI>
        implements PeekingIterator<URI> {
    protected final ResultSet rs;
    private   final boolean   closeStmt;

    /**
     * Create a new iterator.
     *
     * @param rs        the underlying result-set to use; the result-set must either have at least
     *                  one column such that <code>rs.getString(1)</code> returns a URI, or you
     *                  must override {@link #getNextId}.
     * @param closeStmt whether to close the associated statement when closing the result-set at
     *                  the end of the iteration.
     * @throws SQLException if thrown while attempting to advance to the first row
     */
    public RSBlobIdIterator(ResultSet rs, boolean closeStmt) throws SQLException {
      this.rs        = rs;
      this.closeStmt = closeStmt;
    }

    @Override
    protected URI computeNext() {
      try {
        URI id = getNextId();
        if (id != null)
          return id;

        close();
        return endOfData();
      } catch (SQLException sqle) {
        throw new RuntimeException("error reading db results", sqle);
      }
    }

    /**
     * Get the next id.
     *
     * @return the next id, or null if there are none left
     */
    protected URI getNextId() throws SQLException {
      if (!rs.next())
        return null;

      return URI.create(rs.getString(1));
    }

    /**
     * Close the underlying result-set and statement (if <var>closeStmt</var> is true).
     */
    protected void close() {
      try {
        if (closeStmt)
          rs.getStatement().close();
        else
          rs.close();
      } catch (SQLException sqle) {
        logger.error("Error closing statement or result-set", sqle);
      }
    }
  }
}
