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
package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.io.*;

import com.sun.xml.xsom.parser.XSOMParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import com.sun.xml.xsom.*;
import at.nhmwien.schema_mapping_tool.fileProcessors.DataRecord;

/**
 *
 * @author wkoller
 */
public class XsdXSOMConverter extends FileConverter {
    public class ParseErrorHandler implements ErrorHandler {
        public void error(SAXParseException exception) {
            System.out.println( "[SAXParseError] Line " + exception.getLineNumber() + "/"  + exception.getColumnNumber() + ": " + exception.getMessage() );
            System.out.println( "\t" + exception.getPublicId() + " / " + exception.getSystemId() + " / " + exception.toString() );
//            exception.printStackTrace();
        }

        public void fatalError(SAXParseException exception) {
            System.out.println( "[SAXParseFatal] Line " + exception.getLineNumber() + "/"  + exception.getColumnNumber() + ": " + exception.getMessage() );
            System.out.println( "\t" + exception.getPublicId() + " / " + exception.getSystemId() + " / " + exception.toString() );
//            exception.printStackTrace();
        }

        public void warning(SAXParseException exception) {
            System.out.println( "[SAXParseWarning] Line " + exception.getLineNumber() + "/"  + exception.getColumnNumber() + ": " + exception.getMessage() );
            System.out.println( "\t" + exception.getPublicId() + " / " + exception.getSystemId() + " / " + exception.toString() );
//            exception.printStackTrace();
        }
    }

    //private int callStackNumber = 0;
    ArrayList<String> typeStack = new ArrayList<String>();

    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap<String, LinkedHashMap>();

        try {
            XSOMParser parser = new XSOMParser();
            ParseErrorHandler parserError = new ParseErrorHandler();
            parser.setErrorHandler(parserError);

            parser.parse(inputFile);

/*            Set<SchemaDocument> docs = parser.getDocuments();
            Iterator<SchemaDocument> docIt = docs.iterator();

            while( docIt.hasNext() ) {
                SchemaDocument doc = docIt.next();
            }*/

            XSSchemaSet set = parser.getResult();

            LinkedHashMap<String,LinkedHashMap> elementField = null;
            Iterator<XSElementDecl> jtr = set.iterateElementDecls();
            while( jtr.hasNext() ) {
                XSElementDecl ele = jtr.next();

                elementField = this.parseElement(ele);

                // Check if we have an element
                if( elementField.entrySet().iterator().hasNext() ) {
                    Map.Entry<String,LinkedHashMap> elementFieldInfo = elementField.entrySet().iterator().next();
                    //if( elementFieldInfo.getKey().compareTo(( "element" )) != 0 ) continue;
                    
                    if( elementFieldInfo != null ) {
                        fields.put(elementFieldInfo.getKey(), elementFieldInfo.getValue() );
                    }
                }
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return fields;
    }

    public LinkedHashMap<String,LinkedHashMap> parseParticle( XSParticle particle ) {
        XSTerm complTerm = particle.getTerm();
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap<String, LinkedHashMap>();

        if( complTerm.isModelGroup() ) {
            XSModelGroup complModelGroup = complTerm.asModelGroup();
            XSParticle[] complChildren = complModelGroup.getChildren();

            LinkedHashMap<String,LinkedHashMap> elementField = null;
            for( int i = 0; i < complChildren.length; i++ ) {
                elementField = this.parseParticle( complChildren[i] );

                // Check if we have an element
                Iterator<Map.Entry<String, LinkedHashMap>> elementIt = elementField.entrySet().iterator();

                while( elementIt.hasNext() ) {
                    Map.Entry<String,LinkedHashMap> elementFieldInfo = elementIt.next();
                    fields.put(elementFieldInfo.getKey(), elementFieldInfo.getValue() );
                }

                /*if( elementField.entrySet().iterator().hasNext() ) {
                    Map.Entry<String,LinkedHashMap> elementFieldInfo = elementField.entrySet().iterator().next();
                }*/
            }
        }
        else if( complTerm.isModelGroupDecl() ) {
            XSModelGroupDecl complModelGroupDecl = complTerm.asModelGroupDecl();
            XSParticle[] complChildren = complModelGroupDecl.getModelGroup().getChildren();

            LinkedHashMap<String,LinkedHashMap> elementField = null;
            for( int i = 0; i < complChildren.length; i++ ) {
                //System.out.println( "parseParticle (modelgroupdecl): " + complChildren[i].toString() );
                elementField = this.parseParticle( complChildren[i] );

                // Check if we have an element
                if( elementField.entrySet().iterator().hasNext() ) {
                    Map.Entry<String,LinkedHashMap> elementFieldInfo = elementField.entrySet().iterator().next();
                    fields.put(elementFieldInfo.getKey(), elementFieldInfo.getValue() );
                }
            }
        }
        else if( complTerm.isElementDecl() ) {
            //System.out.println( "parseParticle (element): " + complTerm.toString() );
            fields = this.parseElement( complTerm.asElementDecl() );
        }

        return fields;
    }

    public LinkedHashMap<String,LinkedHashMap> parseElement( XSElementDecl element ) {
        //callStackNumber++;
        LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap<String, LinkedHashMap>();
        LinkedHashMap fieldInfo = new LinkedHashMap();
        
        if( typeStack.contains(element.getName()) ) {
            fieldInfo.put( "name", element.getName() );
            fieldInfo.put( "subfields", null );

            fields.put( element.getName(), fieldInfo);
            
            return fields;
        }
        typeStack.add( element.getName() );

        // Build element ID
        String elementID = new String();
        for( String id : typeStack ) {
            elementID = elementID.concat( id );
            elementID = elementID.concat( DataRecord.getIDSeperator() );
        }
        // Remove last seperator
        elementID = elementID.substring(0, elementID.lastIndexOf(DataRecord.getIDSeperator()));

        XSType eleType = element.getType();
        
        LinkedHashMap<String,LinkedHashMap> subFields = null;

        //System.out.println( "DEBUG (" + callStackNumber + "): Element '" + element.getName() + "' / '" + eleType.getName() + "'" );

        if( eleType.isComplexType() ) {
            XSComplexType complType = eleType.asComplexType();
            XSContentType complContent = complType.getContentType();
            XSParticle complParticle = complContent.asParticle();
            if( complParticle != null ) {
                //System.out.println( "parseElement: " + complParticle.toString() );
                subFields = this.parseParticle(complParticle);
            }

            // Check possible attributes
            Collection<? extends XSAttributeUse> cAttribs = complType.getAttributeUses();
            Iterator<? extends XSAttributeUse> i = cAttribs.iterator();

            // Check all attributes and add them to the list of sub-fields
            while( i.hasNext() ) {
                if( subFields == null ) subFields = new LinkedHashMap<String,LinkedHashMap>();

                XSAttributeDecl attributeDecl = i.next().getDecl();
                String typeName = complType.getName();

                //System.out.println( complType.getName() + " has attribute: " + attributeDecl.getName() );

                LinkedHashMap subFieldInfo = new LinkedHashMap();
                subFieldInfo.put( "name", "Attr: " + attributeDecl.getName() );
                subFieldInfo.put( "subfields", null );

                subFields.put( elementID + DataRecord.getIDSeperator() + ":attr:" + attributeDecl.getName(), subFieldInfo );
            }
        }

        //LinkedHashMap fieldInfo = new LinkedHashMap();
        fieldInfo.put( "name", element.getName() );
        fieldInfo.put( "subfields", subFields );

        //LinkedHashMap<String,LinkedHashMap> fields = new LinkedHashMap<String, LinkedHashMap>();
        //fields.put( element.getName(), fieldInfo);
        fields.put( elementID, fieldInfo);
        /*else if( eleType.isSimpleType() ) {
            XSSimpleType simplType = eleType.asSimpleType();

            if( simplType.isRestriction() ) {
                XSRestrictionSimpleType restSimplType = simplType.asRestriction();
            }
        }*/

        typeStack.remove( element.getName() );

        //callStackNumber--;

        return fields;
    }
}
