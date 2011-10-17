/*
   Copyright 2011 Museum of Natural History Vienna

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package at.nhmwien.schema_mapping_tool.fileProcessors;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.regex.Pattern;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import java.util.ArrayList;

/**
 *
 * @author wkoller
 */
public class XMLProcessor extends FileProcessor {
    private DocumentBuilder docBuild = null;
    private Document doc = null;
    private Document readDoc = null;
    private Element currWriteEl = null;
    private Element rootEl = null;

    private NodeList rootNodes = null;
    private int rootNodesIndex = 0;

    public XMLProcessor() {
        super();

        /*try {
            this.docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }*/
    }

    public void prepareFileRead() {
        try {
            this.docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.readDoc = this.docBuild.parse(this.operateFile);
            this.currWriteEl = null;
            this.rootEl = null;
            this.doc = null;

            this.rootNodes = this.readDoc.getChildNodes();
            this.rootNodesIndex = 0;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void prepareFileWrite() {
        try {
            this.docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.doc = this.docBuild.newDocument();
            this.currWriteEl = null;
            this.rootEl = null;
            this.readDoc = null;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void done() {
        if( this.doc != null ) {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
                transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
                transformer.setOutputProperty( OutputKeys.ENCODING, this.fileEncoding.displayName() );

                StreamResult sResult = new StreamResult( this.operateFile );
                DOMSource source = new DOMSource( this.doc );
                transformer.transform( source, sResult );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
        else if( this.readDoc != null ) {
               // Nothing to do here
        }
    }

    public void nextEntry() throws Exception {
        if( this.readDoc != null ) {
            Node currNode = this.rootNodes.item(this.rootNodesIndex);

            this.addNode(currNode);

            this.rootNodesIndex++;
        }
        else {
            this.currWriteEl = null;
        }
    }

    private void addNode( Node currNode ) {
        this.addNode(currNode, "");
    }

    private void addNode( Node currNode, String idPrefix ) {
        String nodeName = currNode.getNodeName();
        //String nodePrefix = currNode.getPrefix();
        // Remove any namespace prefixes
        nodeName = nodeName.replaceAll( ".*:", "" );
        
        // Update prefix
        if( !idPrefix.isEmpty() ) idPrefix += DataRecord.getIDSeperator();
        //idPrefix += currNode.getNodeName();
        idPrefix += nodeName;

        // Now create new record with this entry
        DataRecord newRec = new DataRecord();
        newRec.setIDRecord( idPrefix );
        newRec.setRecordContent( currNode.getTextContent() );

        // Add record to stack
        this.recordsStack.add(newRec);

        // Add all childs to the stack
        NodeList childNodes = currNode.getChildNodes();
        for( int i = 0; i < childNodes.getLength(); i++ ) {
            //if( childNodes.item(i).getNodeType() == Node.TEXT_NODE ) newRec.setRecordContent(childNodes.item(i).getNodeValue());

            if( childNodes.item(i).getNodeType() != Node.ELEMENT_NODE ) continue;
            
            this.addNode( childNodes.item(i), idPrefix );
        }
    }

     /**
     * Iterator implementations
     */
    public boolean hasNext() {
        return (this.readDoc != null && ( (this.rootNodesIndex < this.rootNodes.getLength()) || super.hasNext() ));
    }

    // TODO: ReplaceALL functionality
    public void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll ) {
        String idComponents[] = nRecord.getIDRecord().split( Pattern.quote( DataRecord.getIDSeperator() ) );

        String masterElementTag = idComponents[0];
        //System.arraycopy( idComponents, 1, idComponents, 0, idComponents.length - 1 );

        // Create top-level element if necessary
        if( this.rootEl == null ) {
            this.rootEl = this.doc.createElement(masterElementTag);
            doc.appendChild(this.rootEl);
        }

        // Check if we have sub-elements at all
        if( idComponents.length <= 1 ) {
            this.rootEl.setTextContent( nRecord.getRecordContent() );
        }
        // if yes, try to find our element
        else {
            //String elementTag = idComponents[1];
            //String pathTags[] = new String[idComponents.length - 2];
            String attributeName = null;
            int correction = 1;

            if( idComponents[idComponents.length -1].startsWith( ":attr:" ) ) {
                correction = 2;
                attributeName = idComponents[idComponents.length -1];

                // Extract attribute name
                attributeName = attributeName.substring( attributeName.indexOf( ":attr:" ) );
            }

            // Remove master element name (and attribute name) and copy new tag list to fresh array
            String pathTags[] = new String[idComponents.length - correction];
            System.arraycopy( idComponents, 1, pathTags, 0, pathTags.length );

            // Check if we have an attribute
            /*if( pathTags[pathTags.length - 1].startsWith( ":attr:" ) ) {
                String cleanPathTags[] = new String[pathTags.length - 1];
                attributeName = pathTags[pathTags.length - 1];

                // Remove :attr: flag
                attributeName = attributeName.substring( attributeName.indexOf( ":attr:" ) );

                System.arraycopy( pathTags, 0, cleanPathTags, 0, cleanPathTags.length );
                pathTags = cleanPathTags;
            }*/

            this.currWriteEl = this.getElementByPath( this.rootEl, pathTags );

            // Check if this is a fresh entry
            if( this.currWriteEl == null || bAddNew ) {
                this.currWriteEl = this.createElementByPath( this.rootEl, pathTags );
                //this.currWriteEl = this.doc.createElement( elementTag );
                //this.rootEl.appendChild( this.currWriteEl );
            }

            //Element myElement = null;
            // Check if we have a sub-element
            /*if( pathTags.length > 0 ) {
                myElement = this.getElementByPath( currWriteEl, pathTags );

                // If we do not have the element yet, create a new one
                if( myElement == null || bAddNew ) {
                    myElement = this.createElementByPath( this.currWriteEl, pathTags );
                }
            }
            // If not use current write element
            else {
                if( bAddNew ) {
                    this.currWriteEl = this.doc.createElement( elementTag );
                    this.rootEl.appendChild( this.currWriteEl );
                }

                myElement = this.currWriteEl;
            }*/

            // Assign content
            if( attributeName == null ) {
                this.currWriteEl.setTextContent( nRecord.getRecordContent() );
            }
            else {
                this.currWriteEl.setAttribute( attributeName, nRecord.getRecordContent() );
            }
        }
    }

    public DataRecord getDataRecord( String IDRecord ) {
        DataRecord returnRecord = new DataRecord();
        returnRecord.setIDRecord( IDRecord );
        returnRecord.setRecordContent( "" );
        
        String idComponents[] = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );

        // Check if we have an attribute flag, if yes we have to check the element
        int correction = 1;
        if( idComponents[idComponents.length - 1].startsWith( ":attr:" ) ) {
            correction = 2;
        }

        //String masterElementTag = idComponents[0];
        String pathRemains[] = new String[idComponents.length - correction];
        System.arraycopy( idComponents, 1, pathRemains, 0, pathRemains.length );

        Element myElement = this.getElementByPath( this.rootEl, pathRemains );
        if( myElement != null ) {
            returnRecord.setRecordContent( myElement.getTextContent() );
        }

        return returnRecord;
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "xml" };

        return arr;
    }

    /**
     * Find an element in the tree using a simple path
     * @param startElement Element to start searching from
     * @param path Path to search for
     * @return Element we are looking for, or null if not found
     */
    private Element getElementByPath( Element startElement, String path[] ) {
        if( startElement == null || path.length <= 0 ) return null;

        if( path.length == 1 ) {
            NodeList matchedElements = startElement.getElementsByTagName( path[0] );

            if( matchedElements.getLength() <= 0 ) return null;

            return (Element) startElement.getElementsByTagName( path[0] ).item( matchedElements.getLength() - 1 );

            //return (Element) startElement.getElementsByTagName( path[0] ).item(0);
        }
        else {
            NodeList matchedElements = startElement.getElementsByTagName( path[0] );

            if( matchedElements.getLength() <= 0 ) return null;

            String newPath[] = new String[path.length - 1];
            System.arraycopy( path, 1, newPath, 0, newPath.length );
            return this.getElementByPath( (Element) matchedElements.item( matchedElements.getLength() - 1 ), newPath );
        }
    }

    /**
     * Create a new element using a given path
     * @param startElement Element to start searching from
     * @param path Path to look for
     * @return Newly created element
     */
    private Element createElementByPath( Element startElement, String path[] ) {
        String singlePath[] = new String[1];
        singlePath[0] = path[0];

        // Search for our element, but only the next one
        Element myElement = this.getElementByPath( startElement, singlePath );
        // If we do not have the next element at all, we have to create all sub-elements
        if( myElement == null ) {
            // Set the starting point
            Element currElement = startElement;
            // Add all elements from the given path
            for( int i = 0; i < path.length; i++ ) {
                // Create a new element
                Element newElement = this.doc.createElement( path[i] );
                // Append the new element
                currElement.appendChild(newElement);
                currElement = newElement;
            }
            return currElement;
        }
        // If we have an element, go on
        else {
            // If this is the last element in the row, we have to create a parallel one
            if( path.length <= 1 ) {
                Element newElement = this.doc.createElement( path[0] );
                startElement.appendChild(newElement);

                return newElement;
            }
            // Else just continue with the search for the element & creation
            else {
                String newPath[] = new String[path.length - 1];
                System.arraycopy( path, 1, newPath, 0, newPath.length );
                return this.createElementByPath( myElement, newPath );
            }
        }
    }
}
