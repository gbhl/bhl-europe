/*
 * Main Class for alle FileProcessor
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import java.util.*;

/**
 * Handles the FileProcessor classes
 * If a new FileProcessor is added, please initiate it in the constructor here
 * @author wkoller
 */
public class ProcessorHandler {
    private static ProcessorHandler mySelf = null;

    private HashMap<String,FileProcessor> availProcessor = null;
    
    static public ProcessorHandler self() {
        if( ProcessorHandler.mySelf == null ) {
            ProcessorHandler.mySelf = new ProcessorHandler();
        }
        
        return ProcessorHandler.mySelf;
    }

    private ProcessorHandler() {
        this.availProcessor = new HashMap<String,FileProcessor>();

        this.availProcessor.put( "CDS/ISIS", new ISISProcessor() );
        this.availProcessor.put( "MARC21", new MARCProcessor() );
        this.availProcessor.put( "MarcXML", new MARCXMLProcessor() );
        this.availProcessor.put( "JDBC", new JDBCProcessor() );
        this.availProcessor.put( "XML", new XMLProcessor() );
        this.availProcessor.put( "Excel", new ExcelProcessor() );
        this.availProcessor.put( "Excel (XLSX)", new XlsxProcessor() );
        this.availProcessor.put( "DelFile", new DelFileProcessor() );
    }

    /**
     * Return all available types of processors
     * @return String[] List of all registered processors
     */
    public String[] getProcessors() {
        return this.availProcessor.keySet().toArray(new String[0]);
    }

    /**
     * Return the FileProcessor for a given type
     * @param type Type of FileProcessor
     * @return FileProcessor
     */
    public FileProcessor getProcessorForType( String type ) {
        // TODO: Replace with something more smart, so that we do not ALWAYS return a fresh copy, only if required
        try {
            return (this.availProcessor.get(type).getClass().newInstance());
        }
        catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the supported file extensions for a given type
     * @param type Type of FileProcessor
     * @return String[] List of all file extensions
     */
    public String[] getSupportedFilesForType( String type ) {
        if( !this.availProcessor.containsKey(type) ) return null;

        return this.availProcessor.get(type).getSupportedFileExtensions();
    }
}
