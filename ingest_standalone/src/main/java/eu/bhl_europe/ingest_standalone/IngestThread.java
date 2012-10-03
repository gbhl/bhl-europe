/*
 */
package eu.bhl_europe.ingest_standalone;

import java.sql.Connection;
import java.sql.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * base thread for all ingest related processing
 * @author wkoller
 */
public abstract class IngestThread extends Thread {
    protected Logger m_logger = null;
    
    protected void InitLogger(Class p_class) {
        m_logger = LoggerFactory.getLogger(p_class);
    }
    
    /**
     * function for obtaining a connection to the mysql database
     * @return Connection to mysql database
     */
    protected Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    Ingest.m_settings.getProperty("database.connect") +
                    "?user=" + Ingest.m_settings.getProperty("database.username") +
                    "&password=" + Ingest.m_settings.getProperty("database.password")
                    );
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
