/*
 */
package eu.bhl_europe.ingest_standalone;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * ingest an item into fedora
 * @author wkoller
 */
public class FedoraThread extends IngestThread {
    private int m_queueId = 0;
    private String m_sipPath = null;
    private File[] m_sipFiles = null;
    private FedoraClient m_fedoraClient = null;

    public FedoraThread(int p_queueId, String p_sipPath) {
        this.m_queueId = p_queueId;
        this.m_sipPath = p_sipPath;
        
        InitLogger(FedoraThread.class);
    }
    
    /**
     * helper function for updating the status of the currently handled ingest item
     * @param p_status status to set for the ingest item
     */
    private void updateStatus(String p_status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // obtain connection to database
            conn = getConnection();
            // update queue entry status
            stmt = conn.prepareStatement("UPDATE `ingest_queue` SET `ingest_status` = ? WHERE `id` = ?");
            stmt.setString(1, p_status);
            stmt.setInt(2, m_queueId);
            stmt.execute();
        }
        catch(Exception e) {
            m_logger.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if( stmt != null ) {
                try {stmt.close();}catch(Exception e) {}
                stmt = null;
            }
            if( conn != null ) {
                try {conn.close();}catch(Exception e) {}
                conn = null;
            }
        }
        
    }

    @Override
    public void run() {
        m_logger.info("[FedoraThread] [" + m_queueId + "/" + m_sipPath + "]");
        
        try {
            // update queue entry to be running
            updateStatus("running");
            
            // prepare file name filter
            FilenameFilter sipFileFilter = new SipFileFilter();

            // list all files in sip-directory
            File sipDirectory = new File(m_sipPath);
            m_sipFiles = sipDirectory.listFiles(sipFileFilter);
            
            if( m_sipFiles == null ) {
                throw new Exception("Unable to read sip-directory.");
            }

            // connect to fedora
            FedoraCredentials fedoraCredentials = new FedoraCredentials(
                    Ingest.m_settings.getProperty("fedora.client.url"),
                    Ingest.m_settings.getProperty("fedora.client.username"),
                    Ingest.m_settings.getProperty("fedora.client.password")
            );
            m_fedoraClient = new FedoraClient(fedoraCredentials);

            // now process them all
            for( File sipFile : m_sipFiles ) {
                try {
                    IngestResponse ingestResponse = FedoraClient.ingest().content(sipFile).format("info:fedora/fedora-system:METSFedoraExt-1.1").execute(m_fedoraClient);
                    m_logger.info("Item successfully ingested [" + sipFile + "] / [" + ingestResponse.getPid() + "]");
                    
                }
                catch( Exception e ) {
                    m_logger.error("Error while ingesting item [" + m_sipPath + "] / [" + sipFile + "]");
                    m_logger.error(e.getMessage());
                    e.printStackTrace();
                    
                    throw new Exception("Error during ingest");
                }
            }

            // update status
            updateStatus("finished");
        }
        catch( Exception e ) {
            m_logger.error("Error while processing sip-Directory [" + m_sipPath + "]");
            m_logger.error(e.getMessage());
            e.printStackTrace();
            
            // update status
            updateStatus("error");
        }
        
        // execute run method of super class
        super.run();
    }
}
