package at.nhmwien.schema_mapping_tool.converter;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author wkoller
 */
public class MARCXMLConverter {
    public static void convertToMODS( File inputFile, File outputFile ) throws Exception {
        // Now load the XSL from the internal resources
        InputStream xslFile = MARCXMLConverter.class.getResourceAsStream( "resources/MARC21slim2MODS3-4.xsl" );
        XSLTransformer.transform(inputFile, outputFile, xslFile);
    }

    public static void convertToOLEF( File inputFile, File outputFile ) throws Exception {
        File tempFile = File.createTempFile( "smt_conversion_marcxmlOlef", ".tmp" );

        // Finally do the conversion
        MARCXMLConverter.convertToMODS(inputFile, tempFile);
        MODSConverter.convertToOLEF(tempFile, outputFile);
    }
}
