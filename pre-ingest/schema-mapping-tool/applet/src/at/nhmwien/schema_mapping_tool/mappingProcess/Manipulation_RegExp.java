/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author wkoller
 */
public class Manipulation_RegExp extends Manipulation {
    public String applyManipulation( String content, Object parameter ) {
        HashMap<String,String> realParas = (HashMap<String,String>) parameter;

        String regExp = realParas.get("expression");
        String outputString = realParas.get("output");

        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(content);

        if( m.matches() ) {
            for( int i = 0; i <= m.groupCount(); i++ ) {
                outputString = outputString.replace( "$" + i, m.group(i) );
            }
            content = outputString;
        }

        return content;
    }
}
