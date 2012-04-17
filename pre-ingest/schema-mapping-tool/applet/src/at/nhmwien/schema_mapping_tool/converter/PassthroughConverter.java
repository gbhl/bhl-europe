/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.converter;

import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * @author wkoller
 */
public class PassthroughConverter {
    public static void passthrough( File inputFile, File outputFile, Charset inputEncoding, Charset outputEncoding ) throws Exception {
        InputStream in = new FileInputStream(inputFile);
        OutputStream out = new FileOutputStream(outputFile);
        
        InputStreamReader reader = new InputStreamReader(in, inputEncoding);
        OutputStreamWriter writer = new OutputStreamWriter(out, outputEncoding);
        
        // Read in char by char and write it to the new output
        int i = 0;
        while( (i = reader.read()) >= 0 ) {
            writer.write(i);
        }
        
        reader.close();
        writer.close();
    }
}
