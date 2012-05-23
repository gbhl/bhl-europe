package at.nhmwien.schema_mapping_tool.converter;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 *
 * @author wkoller
 */
public class MARC21Converter {
    public static void convertToMARCXML( File inputFile, File outputFile, Charset inputEncoding, Charset outputEncoding ) throws Exception {
        InputStream in = new FileInputStream(inputFile);
        OutputStream out = new FileOutputStream(outputFile);

        MarcReader reader = new MarcStreamReader(in, inputEncoding );
        MarcXmlWriter writer = new MarcXmlWriter(out, outputEncoding, true);

        while (reader.hasNext()) {
            Record record = reader.next();

            writer.write(record);
        }
        writer.close();
    }

    public static void convertToMODS( File inputFile, File outputFile, Charset inputEncoding, Charset outputEncoding ) throws Exception {
        File tempFile = File.createTempFile( "smt_conversion_marcMods", ".tmp" );

        // Do the conversion in two steps
        MARC21Converter.convertToMARCXML(inputFile, tempFile, inputEncoding, outputEncoding );
        MARCXMLConverter.convertToMODS(tempFile, outputFile);
    }

    public static void convertToOLEF( File inputFile, File outputFile, Charset inputEncoding, Charset outputEncoding ) throws Exception {
        File tempFile = File.createTempFile( "smt_conversion_marcOlef", ".tmp" );

        // Finally do the conversion
        MARC21Converter.convertToMODS(inputFile, tempFile, inputEncoding, outputEncoding);
        MODSConverter.convertToOLEF(tempFile, outputFile);
    }
}
