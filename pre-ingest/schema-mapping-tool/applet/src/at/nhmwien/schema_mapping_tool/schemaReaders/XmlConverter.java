/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import at.nhmwien.schema_mapping_tool.fileProcessors.DataRecord;

/**
 *
 * @author wkoller
 */
public class XmlConverter extends FileConverter{
    protected DocumentBuilder builder;

    /**
     * Constructor
     */
    public XmlConverter() {
        // Get a new instance of DocumentBuilder
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	dbf.setNamespaceAware(true);
            builder = dbf.newDocumentBuilder();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Parse a given file and return it's structure
     * @param inputFile File to parse
     * @return
     */
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap();

        // Try parsing
        try {
            Document document = builder.parse(inputFile);

            fields = this.parseNodes( null, document.getDocumentElement().getChildNodes());
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        
        return fields;
    }

    /**
     * Parse a list of nodes and convert them to our internal structure
     * @param parentFieldID ID of parent field, required to create a unique ID
     * @param nodes List of nodes to parse
     * @return
     */
    protected LinkedHashMap<String,LinkedHashMap> parseNodes( String parentFieldID, NodeList nodes ) {
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap();

        // Cycle through all nodes and check the element-nodes
        for( int i = 0; i < nodes.getLength(); i++ ) {
            Node currNode = nodes.item(i);

            // Check if our current node is an element-node
            if( currNode.getNodeType() == Node.ELEMENT_NODE ) {
                System.out.println( "DEBUG: currNode: " + currNode.getNodeName() );

                String nodeName = currNode.getNodeName();
                LinkedHashMap<String,LinkedHashMap> subFields = null;

                // Construct fieldID
                String fieldID = nodeName;
                if( parentFieldID != null ) {
                    fieldID = parentFieldID + DataRecord.getIDSeperator() + fieldID;
                }

                // If we have child-nodes, start recursive parsing
                if( currNode.hasChildNodes() ) {
                    subFields = this.parseNodes( fieldID, currNode.getChildNodes());
                }

                // Add info about this node into our structure
                LinkedHashMap fieldInfo = new LinkedHashMap();
                fieldInfo.put("name", nodeName);
                fieldInfo.put("subfields", subFields);

                // Finally add to our fields list
                fields.put( fieldID, fieldInfo);
            }
        }

        // If we found no element nodes, return null
        if( fields.isEmpty() ) fields = null;

        return fields;
    }
}
