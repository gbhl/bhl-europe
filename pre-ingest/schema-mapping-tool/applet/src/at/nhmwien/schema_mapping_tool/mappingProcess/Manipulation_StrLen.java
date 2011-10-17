/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

/**
 *
 * @author wkoller
 */
public class Manipulation_StrLen extends Manipulation {
    public String applyManipulation( String content, Object parameter ) {
        return String.valueOf( content.length() );
    }
}
