/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.HashMap;

/**
 *
 * @author wkoller
 */
public class Manipulation_MathAdd extends Manipulation {
    public String applyManipulation( String content, Object parameter ) {
        HashMap<String,String> realParas = (HashMap<String,String>) parameter;

        String outputString = realParas.get("output");

        try {
            content = String.valueOf( Integer.valueOf(content) + Integer.valueOf(outputString) );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return content;
    }
}
