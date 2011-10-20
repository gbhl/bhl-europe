/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import java.io.*;
import org.marc4j.*;

/**
 *
 * @author wkoller
 */
public class MARCXMLProcessor extends MARCProcessor {
    /**
     * FileProcessor implementations
     */
    public void prepareFileRead() throws Exception {
        // Overwrite reader with XML reader
        try {
            super.prepareFileRead();

            InputStream input = new FileInputStream( this.operateFile );
            reader = new MarcXmlReader(input);
        }
        catch( Exception e ) {
            //e.printStackTrace();
            throw e;
        }
    }

    public void prepareFileWrite() throws Exception {
        // Open file for writing
        try {
            super.prepareFileWrite();

            writer.close();
            OutputStream output = new FileOutputStream( this.operateFile );
            writer = new MarcXmlWriter( output, this.fileEncoding, true );
        }
        catch( Exception e ) {
            //e.printStackTrace();
            throw e;
        }
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "xml" };

        return arr;
    }
}
