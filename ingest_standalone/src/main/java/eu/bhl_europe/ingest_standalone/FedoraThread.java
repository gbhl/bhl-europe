/*
 */
package eu.bhl_europe.ingest_standalone;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * ingest an item into fedora
 * @author wkoller
 */
public class FedoraThread extends IngestThread {
    private int m_queueId = 0;
    private String m_sipPath = null;
    private String m_guid = null;
    private File[] m_sipFiles = null;
    private FedoraClient m_fedoraClient = null;

    public FedoraThread(int p_queueId, String p_guid, String p_sipPath) {
        this.m_queueId = p_queueId;
        this.m_sipPath = p_sipPath;
        this.m_guid = p_guid;
        
        InitLogger(FedoraThread.class);
        
        // update ingest entry to be waiting
        setStatus("waiting");
    }
    
    /**
     * helper function for updating the status of the currently handled ingest item
     * @param p_status status to set for the ingest item
     */
    private void setStatus(String p_status) {
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
            m_logger.error(e.getMessage(), e);
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
    
    /**
     * set the item count to process
     * @param p_item_count 
     */
    private void setItemCount(int p_item_count) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // obtain connection to database
            conn = getConnection();
            // update queue entry status
            stmt = conn.prepareStatement("UPDATE `ingest_queue` SET `item_count` = ? WHERE `id` = ?");
            stmt.setInt(1, p_item_count);
            stmt.setInt(2, m_queueId);
            stmt.execute();
        }
        catch(Exception e) {
            m_logger.error(e.getMessage(),e);
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
    
    /**
     * Set the items which are processed
     * @param p_items_done 
     */
    private void setItemsDone(int p_items_done) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // obtain connection to database
            conn = getConnection();
            // update queue entry status
            stmt = conn.prepareStatement("UPDATE `ingest_queue` SET `items_done` = ? WHERE `id` = ?");
            stmt.setInt(1, p_items_done);
            stmt.setInt(2, m_queueId);
            stmt.execute();
        }
        catch(Exception e) {
            m_logger.error(e.getMessage(),e);
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
    
    private String getPIDFromFile( File p_file ) {
        int extIndex = p_file.getName().lastIndexOf('.');
        if( extIndex > 0 ) {
            String fileName = p_file.getName().substring(0, extIndex);
            
            fileName = fileName.replaceAll("_", "-");
            fileName = fileName.replaceFirst("bhle-", "bhle:");
            
            return fileName;
        }
        
        return null;
    }

    @Override
    public void run() {
        m_logger.info("Running [" + m_queueId + "/" + m_sipPath + "]");
        
        try {
            // update queue entry to be running
            setStatus("running");
            
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
            
            // set items to process
            setItemCount(m_sipFiles.length);
            int itemsDone = 0;

            // now process them all
            for( File sipFile : m_sipFiles ) {
                try {
                    String pid = getPIDFromFile(sipFile);
                    if( pid == null ) {
                        m_logger.error("Can't construct PID from filename, skipping [" + sipFile.getName() + "]");
                        continue;
                    }

                    // check if item already exists, if yes skip it
                    FindObjectsResponse findObjectsResponse = FedoraClient.findObjects().pid().query(URLEncoder.encode("pid=" + pid, "UTF-8")).execute(m_fedoraClient);
                    List<String> pids = findObjectsResponse.getPids();
                    if( !pids.isEmpty() ) {
                        m_logger.info("PID already found in Fedora, skipping [" + sipFile.getName() + "/" + pid + "]");
                        continue;
                    }

                    // finally execute the ingest
                    IngestResponse ingestResponse = FedoraClient.ingest().content(sipFile).format("info:fedora/fedora-system:METSFedoraExt-1.1").execute(m_fedoraClient);
                    m_logger.info("Item successfully ingested [" + sipFile + "] / [" + ingestResponse.getPid() + "]");
                    itemsDone++;

                    // update processed items
                    setItemsDone(itemsDone);
                }
                catch( Exception e ) {
                    m_logger.error("Error while ingesting item [" + m_sipPath + "] / [" + sipFile + "]");
                    m_logger.error(e.getMessage(),e);
                    
                    throw new Exception("Error during ingest");
                }
            }
            // once all files are ingested, call modifyObject on main entry to trigger access
            m_logger.debug("Activating object with GUID: " + m_guid);
            FedoraClient.modifyObject(m_guid).state("A").execute(m_fedoraClient);

            // update status
            setStatus("finished");
        }
        catch( Exception e ) {
            m_logger.error("Error while processing sip-Directory [" + m_sipPath + "]");
            m_logger.error(e.getMessage(),e);
            
            // update status
            setStatus("error");
        }
        
        // execute run method of super class
        super.run();
    }
}
