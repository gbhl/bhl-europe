package at.nhmwien.schema_mapping_tool.schemaReaders;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.io.*;

import at.nhmwien.schema_mapping_tool.fileProcessors.DataRecord;

/**
 *
 * @author wkoller
 */
public class FdtConverter extends FileConverter {
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap fields = new LinkedHashMap();

        // Read file line by line
        try {
            InputStreamReader fr = new InputStreamReader( inputFile );
            BufferedReader br = new BufferedReader( fr );

            String line = null;
            String fieldName = null;
            String fieldId = null;
            String subFieldsString = null;

            // Skip first 4 lines
            for( int i = 1; i <= 4; i++ ) br.readLine();

            while( (line = br.readLine()) != null ) {
                HashMap fieldInfo = null;
                HashMap subFieldsInfo = null;
                LinkedHashMap subFields = null;

                // Name of entry ranges from 0 to 30
                fieldName = line.substring( 0, 30 );
                // ID of entry starts at 50 and ends with a blank
                fieldId = String.format( "%03d", Integer.valueOf( line.substring( 50, line.indexOf( " ", 50) ) ) );
                // Subfields entries (if there are any) start at 30 and end at 50
                subFieldsString = line.substring(30,50).trim();
                // If we have subfields, generate entries in the mapping for it
                if( subFieldsString.length() > 0 ) {
                    subFields = new LinkedHashMap();
                    // Generate the mapping entries
                    for( int i = 0; i < subFieldsString.length(); i++ ) {
                        subFieldsInfo = new HashMap();
                        subFieldsInfo.put("name", fieldId + DataRecord.getIDSeperator() + subFieldsString.charAt(i));
                        subFieldsInfo.put("subfields", null);

                        subFields.put(fieldId + DataRecord.getIDSeperator() + subFieldsString.charAt(i), subFieldsInfo);
                    }
                }

                // Add field info
                fieldInfo = new HashMap();
                fieldInfo.put("name", fieldName);
                fieldInfo.put("subfields", subFields);

                // Add field-info to fields list
                fields.put(fieldId, fieldInfo);
            }
        }
        catch( Exception e ) {
            //System.out.println( "Error while parsing FDT File: " + e.getMessage() );
            e.printStackTrace();
        }

        return fields;
    }
}
