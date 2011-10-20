/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.schemes;

import at.nhmwien.schema_mapping_tool.schemaReaders.*;

/**
 *
 * @author wkoller
 */
public class Marc21 extends Schema {
    protected String getResourceName() {
        return "resources/marc21_format.def";
    }

    protected FileConverter getFileConverter() {
        return new MARC21DefConverter();
    }
}
