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
public class ModsV34 extends Schema {
    protected String getResourceName() {
        return "resources/mods_3.4.xsd";
    }

    protected FileConverter getFileConverter() {
        //return new XsdConverter();
        return new XsdXSOMConverter();
    }
}
