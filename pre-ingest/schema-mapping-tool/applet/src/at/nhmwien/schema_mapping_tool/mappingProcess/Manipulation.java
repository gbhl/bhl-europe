/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

/**
 *
 * @author wkoller
 */
public abstract class Manipulation {
    public abstract String applyManipulation( String content, Object parameter );
}
