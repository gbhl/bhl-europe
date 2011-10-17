/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;


/**
 *
 * @author wkoller
 */
public class XSLTransformer {
    public static void transform( File inputFile, File outputFile, File xslFile ) throws Exception {
        XSLTransformer.transform(inputFile, outputFile, new FileInputStream(xslFile));
    }

    public static void transform( File inputFile, File outputFile, InputStream xslFile ) throws Exception {
        // set up XSLT
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer(new StreamSource(xslFile));

        t.transform(new StreamSource(inputFile), new StreamResult(outputFile));
    }
}
