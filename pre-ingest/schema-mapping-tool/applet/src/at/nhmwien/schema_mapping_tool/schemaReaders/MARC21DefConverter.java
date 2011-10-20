/*
 * This is a very specialized converter, designed to allow reading the marc21
 * standard from a copy&paste of the list found at
 * http://www.loc.gov/marc/bibliographic/ecbdlist.html
 */

package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import at.nhmwien.schema_mapping_tool.fileProcessors.DataRecord;

/**
 *
 * @author wkoller
 */
public class MARC21DefConverter extends FileConverter {
    /**
     * Parse the given file
     * @param inputFile File to parse
     * @return
     */
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap fields = new LinkedHashMap();

        // Read file line by line
        try {
            InputStreamReader fr = new InputStreamReader( inputFile );
            BufferedReader br = new BufferedReader( fr );

            String line = null;
            String[] lineParts = null;
            String fieldID = null;
            String fieldName = null;
            LinkedList<String> blockLines = new LinkedList<String>();
            HashMap lastFieldInfo = null;
            String lastFieldID = null;

            Pattern pTopLevel = Pattern.compile( "^\\d\\d\\d - .*" );
            Matcher m = null;

            while( (line = br.readLine()) != null ) {
                // Only lines starting with a number or a '$' sign are valid entries
                m = pTopLevel.matcher(line);

                // Check if we have a main entry...
                if( m.matches() || line.trim().equals( "LEADER" ) ) {
                    //System.out.println( "Matches: " + line );

                    // Seperate the name into its parts, and prepare for use
                    lineParts = line.split(" - ");
                    if( lineParts.length >= 2 ) {
                        fieldID = lineParts[0].trim();
                        fieldName = lineParts[1].trim();
                    }
                    else {
                        fieldID = line.trim().toLowerCase();
                        fieldName = line.trim();
                    }

                    // Skip obsolete entries
                    if( fieldName.indexOf( "[OBSOLETE]" ) != -1 ) continue;

                    // Prepare basic field information
                    // NOTE: We add subfields with "null", because if we have subfields, they get added later on
                    HashMap fieldInfo = new HashMap();
                    fieldInfo.put("name", fieldName);
                    fieldInfo.put("subfields", null);

                    // Check if we have a sub-block, if yes, add the sub-field info to the last entry
                    if( blockLines.size() > 0 && lastFieldInfo != null ) {
                        int numFieldID = 0;

                        try {
                            numFieldID = Integer.valueOf(lastFieldID);
                        }
                        catch( Exception e ) {
                            numFieldID = 11;
                        }

                        if( numFieldID < 10 || lastFieldID.equals("leader") ) {
                            lastFieldInfo.put( "subfields", this.parseCharPositions( lastFieldID, blockLines) );
                        }
                        else {
                            lastFieldInfo.put( "subfields", this.parseSubFields( lastFieldID, blockLines) );
                        }
                        blockLines.clear();
                    }

                    // Add field info
                    fields.put(fieldID, fieldInfo);
                    lastFieldInfo = fieldInfo;
                    lastFieldID = fieldID;
                }
                // .. otherwise just save the line for later sub-field searching
                else {
                    blockLines.add(line);
                }
            }

            // Check for last sub-fields
            if( blockLines.size() > 0 ) lastFieldInfo.put( "subfields", this.parseSubFields( fieldID, blockLines) );

            //fields = null;
        }
        catch( Exception e ) {
            //System.out.println( "Error while parsing MARC21 Definition File: " + e.getMessage() );
            e.printStackTrace();
        }

        return fields;
    }

    /**
     * Parse a block of sub-lines for sub-field entries
     * @param parentID ID of parent element, required to create a unique ID
     * @param lines String ArrayList of all sub-lines
     * @return
     */
    public LinkedHashMap<String,LinkedHashMap> parseSubFields( String parentID, LinkedList<String> lines ) {
        LinkedHashMap fields = new LinkedHashMap();

        // We always have two indicators
        HashMap indicator = new HashMap();
        indicator.put("name", "First Indicator");
        indicator.put("subfields", null);
        fields.put(parentID + "$fInd", indicator);
        // .. and now add the second indicator
        indicator = new HashMap();
        indicator.put("name", "Second Indicator");
        indicator.put("subfields", null);
        fields.put(parentID + "$sInd", indicator);

        // Prepare the pattern for sub-field matching
        Pattern pSubField = Pattern.compile( "\\s*\\$\\w - .*" );
        Matcher m = null;
        String[] lineParts = null;

        for( int i = 0; i < lines.size(); i++ ) {
            String currLine = lines.get(i);

            // Check if the current line starts with a $ sign followed by a word-character
            m = pSubField.matcher(currLine);
            if( m.matches() ) {
                //System.out.println( "Sub-Match: " + currLine );

                // Get the parts of the sub-Field entry (ID and Name)
                lineParts = currLine.split(" - ");

                // Skip obsolete entries
                if( lineParts[1].trim().indexOf( "[OBSOLETE]" ) != -1 ) continue;

                // Add sub-field info
                HashMap subField = new HashMap();
                subField.put("name", lineParts[1].trim());
                subField.put("subfields", null);

                // Add the sub-field to our list
                fields.put( parentID + DataRecord.getIDSeperator() + lineParts[0].trim().replaceAll( "\\$", "" ), subField);
            }
        }

        // Should return "null" if we have no sub-fields at all
        if( fields.size() <= 0 ) fields = null;

        return fields;
    }

    /**
     * Parse a block of sub-lines for character position entries (will be treated like sub-fields for the mapping process)
     * @param parentID ID of parent element, required to create a unique ID
     * @param lines String ArrayList of all sub-lines
     * @return
     */
    public LinkedHashMap<String,LinkedHashMap> parseCharPositions( String parentID, LinkedList<String> lines ) {
        LinkedHashMap fields = new LinkedHashMap();

        // Prepare the pattern for sub-field matching
        Pattern pSubField = Pattern.compile( "\\s*\\d\\d(-\\d\\d)? - .*" );
        Matcher m = null;
        String[] lineParts = null;

        for( int i = 0; i < lines.size(); i++ ) {
            String currLine = lines.get(i);

            // Check if the current line starts with a $ sign followed by a word-character
            m = pSubField.matcher(currLine);
            if( m.matches() ) {
                //System.out.println( "Sub-Match: " + currLine );

                // Get the parts of the sub-Field entry (ID and Name)
                lineParts = currLine.split(" - ");

                // Skip obsolete entries
                if( lineParts[1].trim().indexOf( "[OBSOLETE]" ) != -1 ) continue;

                // Add sub-field info
                HashMap subField = new HashMap();
                subField.put("name", lineParts[1].trim());
                subField.put("subfields", null);

                // Add the sub-field to our list
                fields.put( parentID + DataRecord.getIDSeperator() + lineParts[0].trim(), subField);
            }
        }

        // Should return "null" if we have no sub-fields at all
        if( fields.size() <= 0 ) fields = null;

        return fields;
    }
}
