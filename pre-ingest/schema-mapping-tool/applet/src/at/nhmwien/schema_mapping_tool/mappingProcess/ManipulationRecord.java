/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;
import java.io.*;
import java.util.*;

/**
 *
 * @author wkoller
 */
public class ManipulationRecord implements Serializable {
    public String type = null;                      // Type of manipulation record
    public Object parameters = null;                // Parameters for the manipulation (type dependend)
    public HashMap<String,String> options = null;   // Options for this manipulation (valid for all types)

    public ManipulationRecord() {
        this.options = new HashMap<String,String>();
    }

    /**
     * Provided for interface classes, so they can display something useful
     * @return String this.type
     */
    public String toString() {
        return this.type;
    }
}
