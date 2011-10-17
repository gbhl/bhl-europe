/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.converter;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author wkoller
 */
public class MODSConverter {
    public static void convertToOLEF( File inputFile, File outputFile ) throws Exception {
        // Now load the XSL from the internal resources
        InputStream xslFile = MODSConverter.class.getResourceAsStream( "resources/MODS2OLEF_v0.2.xsl" );
        XSLTransformer.transform(inputFile, outputFile, xslFile);
    }
}
