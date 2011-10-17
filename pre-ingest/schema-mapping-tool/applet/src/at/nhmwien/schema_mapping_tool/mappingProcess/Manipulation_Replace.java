/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.*;

/**
 *
 * @author wkoller
 */
public class Manipulation_Replace extends Manipulation {
    public String applyManipulation( String content, Object parameter ) {
        HashMap<String,String> realParas = (HashMap<String,String>) parameter;

        String expression = realParas.get("expression");
        String outputString = realParas.get("output");

        content = content.replaceAll(expression, outputString);

        return content;
    }
}
