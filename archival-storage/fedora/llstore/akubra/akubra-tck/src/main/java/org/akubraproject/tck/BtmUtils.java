/* $HeadURL$
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

package org.akubraproject.tck;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.Reference;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.internal.XAResourceHolderState;
import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.AbstractXAResourceHolder;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAResourceHolder;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.common.XAStatefulHolder;

import com.google.common.collect.MapMaker;
import com.google.common.base.Function;

/**
 * Utilities for dealing with the Bitronix Transaction Manager.
 *
 * @author Ronald Tschalär
 */
public class BtmUtils {
  /**
   * Not meant to be instantiated.
   */
  private BtmUtils() {
  }

  static {
    System.setProperty("bitronix.tm.serverId", "akubra-tck-test");
    System.setProperty("bitronix.tm.journal", "null");
    System.setProperty("bitronix.tm.disableJmx", "true");
    System.setProperty("bitronix.tm.timer.gracefulShutdownInterval", "10");

    ResourceRegistrar.register(new SimpleXAResourceProducer());
  }

  /**
   * Get the transaction-manager instance.
   *
   * @return the transaction-manager
   */
  public static TransactionManager getTM() {
    return TransactionManagerServices.getTransactionManager();
  }

  private static class SimpleXAResourceProducer implements XAResourceProducer {
    private static final long serialVersionUID = 1L;
    private transient Map<XAResource, XAResourceHolder> xaresHolders = createXAResHoldersMap();

    private static final Map<XAResource, XAResourceHolder> createXAResHoldersMap() {
      return new MapMaker().weakKeys().weakValues().makeComputingMap(
        new Function<XAResource, XAResourceHolder>() {
          public XAResourceHolder apply(XAResource xares) {
            return createResHolder(xares);
          }
        }
      );
    }

    private Object readResolve() {
      xaresHolders = createXAResHoldersMap();
      return this;
    }

    public void init() {
    }

    public void close() {
    }

    public String getUniqueName() {
      return "Akubra-Simple-Resource-Producer";
    }

    public XAResourceHolderState startRecovery() {
      return createResHolder(new RecoveryXAResource()).getXAResourceHolderState();
    }

    public void endRecovery() {
    }

    public Reference getReference() {
      return null;
    }

    public XAStatefulHolder createPooledConnection(Object xaFactory, ResourceBean bean) {
      return null;
    }

    public XAResourceHolder findXAResourceHolder(final XAResource xaResource) {
      return xaresHolders.get(xaResource);
    }

    @SuppressWarnings("serial")
    private static XAResourceHolder createResHolder(XAResource xaResource) {
      ResourceBean rb = new ResourceBean() { };
      rb.setUniqueName(xaResource.getClass().getName() + System.identityHashCode(xaResource));
      rb.setApplyTransactionTimeout(true);

      XAResourceHolder resHolder = new SimpleXAResourceHolder(xaResource);
      resHolder.setXAResourceHolderState(new XAResourceHolderState(resHolder, rb));

      return resHolder;
    }

    private static class SimpleXAResourceHolder extends AbstractXAResourceHolder {
      private final XAResource xares;

      SimpleXAResourceHolder(XAResource xares) {
        this.xares = xares;
      }

      public void close() {
      }

      public Object getConnectionHandle() {
        return null;
      }

      public Date getLastReleaseDate() {
        return null;
      }

      @SuppressWarnings("unchecked")
      public List getXAResourceHolders() {
        return null;
      }

      public XAResource getXAResource() {
        return xares;
      }
    }

    private static class RecoveryXAResource implements XAResource {
      public void start(Xid xid, int flags) {
      }

      public void end(Xid xid, int flags) {
      }

      public int prepare(Xid xid) {
        return XA_OK;
      }

      public void commit(Xid xid, boolean onePhase) {
      }

      public void rollback(Xid xid) {
      }

      public Xid[] recover(int flag) {
        // recovery not supported (yet)
        return new Xid[0];
      }

      public void forget(Xid xid) {
      }

      public int getTransactionTimeout() {
        return 10;
      }

      public boolean setTransactionTimeout(int transactionTimeout) {
        return false;
      }

      public boolean isSameRM(XAResource xaResource) {
        return xaResource == this;
      }
    }
  }
}
