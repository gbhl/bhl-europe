/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.bhl_europe.ingest_standalone;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *
 * @author wkoller
 */
public class SchedulerThread extends IngestThread {
    private Semaphore m_semaphore = null;
    private static ExecutorService m_threadPool = Executors.newFixedThreadPool(2);

    public SchedulerThread() {
        // create (locked) semaphore
        m_semaphore = new Semaphore(0);
        
        InitLogger(SchedulerThread.class);
    }
    
    /**
     * used to synchronize main function with scheduler thread
     * @throws Exception 
     */
    public void waitFor() throws Exception {
        m_semaphore.acquire();
    }

    @Override
    public void run() {
        m_logger.info("Scheduler thread started");
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // connect to mysql database
            conn = getConnection();

            // fetch all content waiting to be processed
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `ingest_queue` WHERE `ingest_status` = 'queued'");
            
            while( rs.next() ) {
                int queueId = rs.getInt("id");
                String sipPath = rs.getString("sip_path");
                String guid = rs.getString("guid");
                
                m_logger.info("Executing queued item [" + queueId + "/" + sipPath + "]");
                
                FedoraThread fedoraThread = new FedoraThread(queueId, guid, sipPath);
                m_threadPool.submit(fedoraThread);
            }
            
            // wait for 1 minute before searching again
            sleep(60000);
        }
        catch( Exception e ) {
            m_logger.error(e.getMessage());
            e.printStackTrace();
        }
        // close database resources & clean up
        finally {
            if( rs != null ) {
                try {rs.close();}catch(Exception e) {}
                rs = null;
            }
            if( stmt != null ) {
                try {stmt.close();}catch(Exception e) {}
                stmt = null;
            }
            if( conn != null ) {
                try {conn.close();}catch(Exception e) {}
                conn = null;
            }
        }

        // let main loop continue
        m_semaphore.release();
    }
}
