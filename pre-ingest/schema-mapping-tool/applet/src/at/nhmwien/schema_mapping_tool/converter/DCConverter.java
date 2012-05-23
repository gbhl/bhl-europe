package at.nhmwien.schema_mapping_tool.converter;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author wkoller
 */
public class DCConverter {
    public static void convertToMODS( File inputFile, File outputFile ) throws Exception {
        InputStream xslFile = MARCXMLConverter.class.getResourceAsStream( "resources/simpleDC2MODS.xsl" );
        XSLTransformer.transform(inputFile, outputFile, xslFile);
    }
    
    public static void convertToOLEF( File inputFile, File outputFile ) throws Exception {
        File tempFile = File.createTempFile( "smt_conversion_dcOlef", ".tmp" );
        
        // Finally do the conversion
        DCConverter.convertToMODS(inputFile, tempFile);
        MODSConverter.convertToOLEF(tempFile, outputFile);
    }
    
}
