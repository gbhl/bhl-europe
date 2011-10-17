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
public class EseV33 extends Schema {
    protected String getResourceName() {
        return "resources/ESE-V3.3.xsd";
    }

    protected FileConverter getFileConverter() {
        //return new XsdConverter();
        return new XsdXSOMConverter();
    }
}
