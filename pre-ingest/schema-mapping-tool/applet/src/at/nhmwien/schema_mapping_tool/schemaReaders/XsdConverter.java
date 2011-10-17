/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import at.nhmwien.schema_mapping_tool.fileProcessors.DataRecord;


/**
 *
 * @author wkoller
 */
public class XsdConverter extends XmlConverter {
    HashMap<String,Element> complexTypesIndex = null;           // Index for all complexType defintions
    HashMap<String,Element> namedElementsIndex = null;          // Index for all elements which contain a name attribute
    HashMap<String,Element> namedGroupElementsIndex = null;     // Index for all named group elements
    ArrayList<String> typeStack = null;                         // Stack holding all previous types, used to prevent endless loops

    final private String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    /**
     * Initialize our basic variables and the class
     */
    public XsdConverter() {
        super();

        complexTypesIndex = new HashMap<String,Element>();
        namedElementsIndex = new HashMap<String,Element>();
        namedGroupElementsIndex = new HashMap<String,Element>();
        typeStack = new ArrayList<String>();
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
            // Index for our complex types
            complexTypesIndex.clear();
            namedElementsIndex.clear();
            namedGroupElementsIndex.clear();

            Document document = builder.parse(inputFile);
                     
            // Get all complex types
            NodeList nodes = document.getDocumentElement().getElementsByTagNameNS(XSD_NAMESPACE, "complexType" );
            for( int i = 0; i < nodes.getLength(); i++ ) {
                Element currElement = (Element)nodes.item(i);

                // Only named complexTypes can be referenced
                if( !currElement.hasAttribute( "name" ) ) continue;

                // Add node to our index
                complexTypesIndex.put(currElement.getAttribute("name"), currElement);
//                System.out.println("put complexType" + currElement.getAttribute("name"));
            }

            // Find all named elements
            nodes = document.getDocumentElement().getElementsByTagNameNS(XSD_NAMESPACE, "element");
            for( int i = 0; i < nodes.getLength(); i++ ) {
                Element currElement = (Element)nodes.item(i);

                // Check if this is a named element
                if( !currElement.hasAttribute("name")) continue;

                // Add node to our index
                namedElementsIndex.put(currElement.getAttribute("name"), currElement);
//                System.out.println("put namedElement" + currElement.getAttribute("name"));
            }

            // Find all named group elements
            nodes = document.getDocumentElement().getElementsByTagNameNS(XSD_NAMESPACE, "group");
            for( int i = 0; i < nodes.getLength(); i++ ) {
                Element currElement = (Element)nodes.item(i);

                // Check if this is a named element
                if( !currElement.hasAttribute("name")) continue;

                // Add node to our index
                namedGroupElementsIndex.put(currElement.getAttribute("name"), currElement);
//                System.out.println("put namedElement" + currElement.getAttribute("name"));
            }

            //fields = this.parseNodes(null, document.getDocumentElement().getElementsByTagNameNS(XSD_NAMESPACE, "element"));
            fields = this.parseNodes(null, document.getDocumentElement().getChildNodes());
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
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap<String, LinkedHashMap>();

        // Cycle through all nodes and check the element-nodes
        for( int i = 0; i < nodes.getLength(); i++ ) {
            String localName = nodes.item(i).getLocalName();
            //String nameSpace = currNode.getNamespaceURI();

            // Check if we have a group or element Node
            /*if( localName == null || ( !localName.equals( "element" ) && !localName.equals( "group" ) ) ) {
                //if( currNode.hasChildNodes() )
                continue;
            }*/

            if( nodes.item(i).getNodeType() != Node.ELEMENT_NODE ) continue;

            Element currNode = (Element) nodes.item(i);

            //if( !(("element".equals(localName) || "group".equals(localName) ) && XSD_NAMESPACE.equals(nameSpace)) ) continue;
            
            // Base variables
            String nodeName = null;
            String nodeType = null;

            if( currNode.hasAttribute( "name" ) ) nodeName = currNode.getAttribute("name");

            // Check if we have a type
            if( currNode.hasAttribute( "type" ) ) {
                nodeType = currNode.getAttribute( "type" );
                if( nodeName == null ) nodeName = nodeType;
            }
            // Else check if we have a reference
            else if( currNode.hasAttribute( "ref" ) ) {
                nodeType = currNode.getAttribute( "ref" );
                if( nodeName == null ) nodeName = nodeType;
            }
            // If we neither have a type, nor a ref, we might have a direct type declaration
            /*else if( currNode.hasAttribute( "name" ) && currNode.hasChildNodes() ) {
            }
            // No more ideas....
            else {
                System.out.println( "ERROR: Unknown node: " + currNode.toString() );
            }*/
            
            /*if( currNode.hasAttribute("name") && currNode.hasAttribute("type") ) {
                nodeName = currNode.getAttribute("name");
                nodeType = currNode.getAttribute("type");
            }
            // If not, check if we got a ref
            else if( currNode.hasAttribute("ref") ) {
                nodeName = currNode.getAttribute("ref");

                //System.out.println( "LocalName: " + localName + " / NodeName: " + nodeName );

                if( !localName.equals( "group" ) ) {
                    Element targetNode = namedElementsIndex.get(nodeName);

                    if( targetNode == null ) {
                        System.out.println( "ERROR: Target not found: " + nodeName );
                        continue;
                    }

                    if (targetNode.hasAttribute("type")) {
                        nodeType = targetNode.getAttribute("type");
                    } else {
                        String debugString = "";
                        NamedNodeMap nnm = targetNode.getAttributes();
                        for (int j = 0; j < nnm.getLength(); j++) {
                            debugString += "/ " + nnm.item(j).getNodeName() + ": " + nnm.item(j).getNodeValue() + " ";
                        }

                        System.out.println("ERROR: Unknown element type - report this as a bug: " + targetNode.toString() + debugString);
                    }
                }
                
                //if( !"group".equals(localName) ) nodeType = namedElementsIndex.get(nodeName).getAttribute("type");
            }
            else {
                String debugString = "";
                NamedNodeMap nnm = currNode.getAttributes();
                for( int j = 0; j < nnm.getLength(); j++ ) {
                    debugString += "/ " + nnm.item(j).getNodeName() + ": " + nnm.item(j).getNodeValue() + " ";
                }

                System.out.println( "ERROR: Unknown element type - report this as a bug: " + currNode.toString() + debugString );
                continue;
            }*/

            
            LinkedHashMap<String,LinkedHashMap> subFields = null;
            boolean bNoSubFields = false;

            // Construct fieldID
            String fieldID = nodeName;
            if( parentFieldID != null ) {
                if( fieldID != null ) {
                    fieldID = parentFieldID + DataRecord.getIDSeperator() + fieldID;
                }
                else {
                    fieldID = parentFieldID;
                }
            }

            //Check if we have a complex type or not
            if( complexTypesIndex.containsKey(nodeType) && !typeStack.contains(nodeType) ) {
                typeStack.add(nodeType);
//                subFields = this.parseNodes(fieldID, ((Element) complexTypesIndex.get(nodeType)).getElementsByTagNameNS(XSD_NAMESPACE, "*"));
                subFields = this.parseNodes(fieldID, ((Element) complexTypesIndex.get(nodeType)).getChildNodes());
                typeStack.remove(nodeType);
            }
            // If not, we might habe a named reference
            else if( namedElementsIndex.containsKey(nodeType) ) {
//                subFields = this.parseNodes( fieldID, ((Element) namedElementsIndex.get(nodeType)).getElementsByTagNameNS(XSD_NAMESPACE, "*"));
                subFields = this.parseNodes( fieldID, ((Element) namedElementsIndex.get(nodeType)).getChildNodes());
            }
            // Check if we have a group
            else if( localName.equals( "group" ) && namedGroupElementsIndex.containsKey(nodeName) ) {
//                subFields = this.parseNodes(fieldID, ((Element) namedGroupElementsIndex.get(nodeName)).getElementsByTagNameNS(XSD_NAMESPACE, "*"));
                subFields = this.parseNodes(fieldID, ((Element) namedGroupElementsIndex.get(nodeName)).getChildNodes());
            }
            // Check if we may have a direct complex-type definition
            //if( currNode.hasAttribute( "name" ) && currNode.hasChildNodes() ) {
            else if( currNode.hasAttribute( "name" ) ) {
                subFields = this.parseNodes( fieldID, currNode.getChildNodes() );

                // If we do not have subfields, don't add an entry at all
                //bNoSubFields = true;
            }
            // Anything else (like restrictions, etc.)
            else if( currNode.hasChildNodes() ) {
                //subFields = this.parseNodes( fieldID, currNode.getElementsByTagNameNS(XSD_NAMESPACE, "*") );
                subFields = this.parseNodes( fieldID, currNode.getChildNodes() );

                // As we do not have a "valid" mapping entry, we add them at the same-level, not as sub-fields
                if( subFields != null ) fields.putAll(subFields);

                bNoSubFields = true;
            }
            // Check if we have a reference to an external namespace
            else if( currNode.hasAttribute( "ref" ) && nodeType.indexOf( ':' ) != -1 ) {
                // No action required, because there can't be any sub-fields
                // TODO: Maybe add support for external namespaces here
            }
            else {
                bNoSubFields = true;
            }

            if( !bNoSubFields ) {
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
