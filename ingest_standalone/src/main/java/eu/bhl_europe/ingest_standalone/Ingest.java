package eu.bhl_europe.ingest_standalone;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class Ingest 
{
    public static Properties m_settings = new Properties();
    
    public static void main( String[] args )
    {
        Logger logger = LoggerFactory.getLogger(Ingest.class);
        
        // load properties
        try {
            m_settings.load(Ingest.class.getResourceAsStream("/ingest.properties"));
            
            // init done
            logger.info("Init done");

            // start main loop
            while(true) {
                try {
                    // start scheduler thread and wait for it to finish
                    SchedulerThread scheduler = new SchedulerThread();
                    scheduler.start();
                    scheduler.waitFor();
                }
                catch( Exception e ) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
