/*
 * Base class for all File-Processors
 * Provides all required methods as abstract functions
 * NOTE: The FileProcessor can work both as input aswell as output processor
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import java.util.*;
import java.io.*;
import java.nio.charset.*;

import org.apache.commons.configuration.BaseConfiguration;

/**
 * Base class for all File-Processors
 * 
 * Basically all you have to do is override the abstract methods, and fill the recordsStack
 * in the nextEntry function - the rest is almost automatically handled by this base class.
 * You might want to override the hasNext() function if you have some special requirements.
 * See the MARCProcessor class for an example implementation.
 *
 * The call procedure for File-Reading is:
 * -> setFile
 * -> prepareFileRead
 * -> loop using hasNext, next and nextEntry until hasNext returns false
 * -> done

 * The call procedure for File-Writing is:
 * -> setFile
 * -> prepareFileWrite
 * -> loop addDataRecord and nextEntry until the processing is finished
 * -> done
 *
 * @author wkoller
 */
public abstract class FileProcessor implements Iterator<DataRecord> {
    
    public abstract void prepareFileRead() throws Exception;     // Called to tell the processor that we will use it for READING
    public abstract void prepareFileWrite() throws Exception;    // Called to tell the processor that we will use it for WRITING

    public abstract void nextEntry() throws Exception;   // Indicator function for the interface to start with a new record / read the next entry - do not forget to call "Collections.sort( this.recordsStack )"!!

    public abstract void done();        // Called to indicate that the processing is done (e.g. close files, etc.)

    public abstract void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll );  // Add a data record (if bAddNew is true, add a new entry instead of replacing an existing)
    public abstract DataRecord getDataRecord( String IDRecord );                // Returns the DataRecord for the given IDRecord (in the current Entry)

    public abstract String[] getSupportedFileExtensions();  // Return a String-Array with supported file extensions

    // Return all available options
    public String[] getAvailableOptions() {
        ArrayList<String> arrOpts = new ArrayList<String>();
        String arrOptsString[] = {};

        Iterator keyIt = this.configuration.getKeys();
        while( keyIt.hasNext() ) {
            arrOpts.add( (String) keyIt.next() );
        }

        return (String[]) arrOpts.toArray(arrOptsString);
    }

    // Called by the options pane to set an option for the given fileprocessor
    public void setOption( String key, Object value ) {
        this.configuration.setProperty(key, value);
    }

    public Object getOption( String key ) {
        return this.configuration.getProperty(key);
    }

    // Class variables
    protected File operateFile = null;
    protected ArrayList<DataRecord> recordsStack = new ArrayList<DataRecord>(0);
    protected Charset fileEncoding = null;
    protected BaseConfiguration configuration = new BaseConfiguration();
    
    /**
     * Initialize member variables
     */
    public FileProcessor() {
        //this.recordsStack = new ArrayList<DataRecord>(0);
    }

    // Set the file by name
    public void setFileName( String name ) {
        this.setFile( new File(name) );
    }

    // Set the file to operate on
    public void setFile( File file ) {
        this.operateFile = file;
    }

    // Set the encoding of the file
    public void setEncoding( Charset nEncoding ) {
        this.fileEncoding = nEncoding;
    }

    /**
     * Iterator implementations
     * NOTE: This function returns null if the next Entry should be read (using nextEntry() function)
     */
    public DataRecord next() {
        // Check if there are any records left on the stack
        if( recordsStack.isEmpty() ) return null;

        // Return top element
        return recordsStack.remove( 0 );
        //return recordsStack.remove(recordsStack.size() - 1);
    }

    // This is an optional operation from Iterator => we do not need it
    public void remove() { return; }

    // Returns true if the record stack is not empty
    public boolean hasNext() {
        return (!this.recordsStack.isEmpty());
    }

    // Overloaded addDataRecord function
    public void addDataRecord( DataRecord nRecord ) {
        addDataRecord( nRecord, false, false );
    }

    // Another overloaded version of addDataRecord
    public void addDataRecord( DataRecord nRecord, boolean bAddNew ) {
        addDataRecord( nRecord, bAddNew, false );
    }
}
