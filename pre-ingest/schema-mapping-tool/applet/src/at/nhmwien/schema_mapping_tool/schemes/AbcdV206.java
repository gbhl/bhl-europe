 package at.nhmwien.schema_mapping_tool.schemes;

import at.nhmwien.schema_mapping_tool.schemaReaders.FileConverter;
import at.nhmwien.schema_mapping_tool.schemaReaders.XsdXSOMConverter;

/**
 *
 * @author wkoller
 */


public class AbcdV206 extends Schema {
    protected String getResourceName() {
        return "resources/ABCD_2.06.XSD";
    }

    protected FileConverter getFileConverter() {
        return new XsdXSOMConverter();
    }
}
