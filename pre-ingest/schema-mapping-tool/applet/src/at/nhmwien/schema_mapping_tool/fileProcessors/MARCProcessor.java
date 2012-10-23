/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import org.marc4j.*;
import org.marc4j.marc.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author wkoller
 */
public class MARCProcessor extends FileProcessor {
    protected MarcReader reader = null;
    protected MarcWriter writer = null;
    protected MarcFactory factory = null;
    protected Record currWriteRecord = null;

    /**
     * FileProcessor implementations
     */
    public void prepareFileRead() throws Exception {
        // Open the file using the marc4j reader
        try {
            InputStream input = new FileInputStream( this.operateFile );
            reader = new MarcStreamReader(input, this.fileEncoding );

            // Reset all writer-related variables
            writer = null;
            factory = null;
            currWriteRecord = null;
        }
        catch( Exception e ) {
            throw e;
            //e.printStackTrace();
        }
    }

    public void prepareFileWrite() throws Exception {
        // Open file for writing
        try {
            OutputStream output = new FileOutputStream( this.operateFile );
            // = new ByteArrayOutputStream();
            writer = new MarcStreamWriter( output, this.fileEncoding );
            factory = MarcFactory.newInstance();
            currWriteRecord = factory.newRecord();

            // Reset all reader variables
            reader = null;
        }
        catch( Exception e ) {
            throw e;
            //e.printStackTrace();
        }
    }

    /**
     * Add a data record to the marc-output
     * @param nRecord Record to add
     */
    public void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll ) {
        // Get the ID of the record
        String IDRecord = nRecord.getIDRecord();

        // Check if we have a leader
        if( IDRecord.startsWith("leader") ) {
            //System.out.println( "Adding Leader: " + IDRecord + " / " + nRecord.getRecordContent() );


            // Check if we have a direct leader mapping
            if( IDRecord.equals("leader") ) {
                Leader nLeader = factory.newLeader(nRecord.getRecordContent());
                currWriteRecord.setLeader(nLeader);
            }
            // .. if not, analyze the mapping and where it should be mapped
            else {
                String[] components = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
                String charPosInfo = components[1];
                int charPosStart = 0;
                int charPosEnd = 0;

                // Check if we have a char-position range
                if( charPosInfo.indexOf("-") > 0 ) {
                    String[] charPosInfoComponents = charPosInfo.split("-");
                    charPosStart = Integer.valueOf( charPosInfoComponents[0] );
                    charPosEnd = Integer.valueOf( charPosInfoComponents[1] );
                }
                // If not, we have a single char
                else {
                    charPosStart = Integer.valueOf( components[1] );
                    charPosEnd = charPosStart;
                }

                // Get the leader and modify it
                Leader nLeader = currWriteRecord.getLeader();
                char[] values;
                for( int charPos = charPosStart; charPos <= charPosEnd; charPos++ ) {
                    switch( charPos ) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            try { nLeader.setRecordLength(Integer.parseInt( nRecord.getRecordContent() )); } catch( Exception e ) { e.printStackTrace(); }
                            break;
                        case 5:
                            nLeader.setRecordStatus(nRecord.getRecordContent().charAt(charPos - charPosStart));
                            break;
                        case 6:
                            nLeader.setTypeOfRecord(nRecord.getRecordContent().charAt(charPos - charPosStart));
                            break;
                        case 7:
                        case 8:
                            values = nLeader.getImplDefined1();
                            values[charPos-7] = nRecord.getRecordContent().charAt(charPos - charPosStart);
                            nLeader.setImplDefined1(values);
                            break;
                        case 9:
                            nLeader.setCharCodingScheme(nRecord.getRecordContent().charAt(charPos - charPosStart));
                            break;
                        case 10:
                            try { nLeader.setIndicatorCount(Integer.parseInt( nRecord.getRecordContent() )); } catch( Exception e ) { e.printStackTrace(); }
                            break;
                        case 11:
                            try { nLeader.setSubfieldCodeLength(Integer.parseInt( nRecord.getRecordContent() )); } catch( Exception e ) { e.printStackTrace(); }
                            break;
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                            try { nLeader.setBaseAddressOfData(Integer.parseInt( nRecord.getRecordContent() )); } catch( Exception e ) { e.printStackTrace(); }
                            break;
                        case 17:
                        case 18:
                        case 19:
                            values = nLeader.getImplDefined2();
                            values[charPos-17] = nRecord.getRecordContent().charAt(charPos - charPosStart);
                            nLeader.setImplDefined2(values);
                            break;
                    }
                }

                // Set the leader in the record
                currWriteRecord.setLeader(nLeader);
            }
        }
        else {
            String[] irComponents = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
            int fieldCode = 0;
            try {
                fieldCode = Integer.parseInt( irComponents[0] );
            }
            catch( Exception e ) {
                e.printStackTrace();
                return;
            }

            // We have a control field
            if( fieldCode <= 9 ) {
                //System.out.println( "Adding Control Field: " + IDRecord + " / " + nRecord.getRecordContent() );

                ControlField nControlField = null;
                List controlFields = currWriteRecord.getVariableFields( String.format( "%03d", fieldCode) );
                if( !controlFields.isEmpty() ) {
                    nControlField = (ControlField) controlFields.get( controlFields.size() - 1 );
                }
                
                //ControlField nControlField = (ControlField) currWriteRecord.getVariableField( String.format( "%03d", fieldCode) );
                if( nControlField == null || bAddNew ) {
                    nControlField = factory.newControlField( String.format( "%03d", fieldCode), "" );
                    currWriteRecord.addVariableField(nControlField);

                    controlFields.add( nControlField );
                }

                // If we do not replace all, remove all fields except the current one
                if (!bReplaceAll) {
                    controlFields.clear();
                    controlFields.add(nControlField);
                }

                for( int i = 0; i < controlFields.size(); i++ ) {
                    nControlField = (ControlField) controlFields.get(i);
                    String currData = nControlField.getData();

                    // Check if we have char positions defined or not
                    if (irComponents.length > 1) {
                        int charPosStart = 0;
                        int charLength = 0;
                        int totalSpace = 0;
                        String charPosInfo = irComponents[1];

                        // Check if we have a char-position range
                        if (charPosInfo.indexOf("-") > 0) {
                            String[] charPosInfoComponents = charPosInfo.split("-");
                            charPosStart = Integer.valueOf(charPosInfoComponents[0]);
                            charLength = Integer.valueOf(charPosInfoComponents[1]) - charPosStart + 1;
                        } // If not, we have a single char
                        else {
                            charPosStart = Integer.valueOf(charPosInfo);
                            charLength = 1;
                        }
                        totalSpace = charPosStart + charLength;

                        // Extend the String if it isn't long enough for our operations
                        while( currData.length() < totalSpace ) currData += " ";
                        
                        /*if (currData.length() < totalSpace) {
                            
                            currData = currData.concat(new String(new char[totalSpace - currData.length()]));
                        }*/
                        // Check if record content string is long enough
                        String recordContent = nRecord.getRecordContent();
                        while( recordContent.length() < charLength ) recordContent += " ";
                        /*if (recordContent.length() < charLength) {
                            recordContent = recordContent.concat(new String(new char[charLength - recordContent.length()]));
                        }*/
                        //System.out.println( "Setting data: '" + currData + "' / recordContent: " + recordContent );

                        currData = currData.substring(0, charPosStart) + recordContent.substring(0, charLength ) + currData.substring(charPosStart + charLength);
                    } else {
                        currData = nRecord.getRecordContent();
                    }

                    nControlField.setData(currData);
                    //currWriteRecord.addVariableField(nControlField);
                }
            }
            // .. got a data field
            else {
                /*String debugString = "";
                for( int i = 0; i < nRecord.getRecordContent().length(); i++ ){
                    debugString = debugString + ";" + String.valueOf( (int) nRecord.getRecordContent().charAt(i) );
                }*/

                //System.out.println( "Adding Data Field: " + IDRecord + " / " + nRecord.getRecordContent() + " / " + debugString );

                // CHeck if a datafield with this code already exist, if yes get the last one from the stack
                DataField nDataField = null;
                List dataFields = currWriteRecord.getVariableFields( String.format( "%03d", fieldCode) );
                if( !dataFields.isEmpty() ) {
                    nDataField = (DataField) dataFields.get( dataFields.size() - 1 );
                }

                //DataField nDataField = (DataField) currWriteRecord.getVariableField( String.format( "%03d", fieldCode) );
                // If no data field is available yet, create a new one
                if( nDataField == null || bAddNew ) {
                    nDataField = factory.newDataField( String.format( "%03d", fieldCode), '0', '0');
                    currWriteRecord.addVariableField(nDataField);

                    dataFields.add( nDataField );
                }

                // Check if we have a sub-fields
                if( irComponents.length > 1 ) {
                    String subfieldCode = irComponents[1];

                    // If we do not replace all, remove all fields except the current one
                    if( !bReplaceAll ) {
                        dataFields.clear();
                        dataFields.add(nDataField);
                    }

                    // Cycle through all active fields and modify them
                    for( int i = 0; i < dataFields.size(); i++ ) {
                        nDataField = (DataField) dataFields.get(i);

                        // Check if we have an indicator mapping
                        if (subfieldCode.equals("fInd")) {
                            nDataField.setIndicator1(nRecord.getRecordContent().charAt(0));
                        } else if (subfieldCode.equals("sInd")) {
                            nDataField.setIndicator2(nRecord.getRecordContent().charAt(0));
                        } // .. if not, create a normal subfield entry
                        else {
                            Subfield nSubField = nDataField.getSubfield(subfieldCode.charAt(0));
                            // Check if the sub-field already exists, if not create a new one and add it
                            if (nSubField == null || bAddNew) {
                                nSubField = factory.newSubfield(subfieldCode.charAt(0), nRecord.getRecordContent());
                                nDataField.addSubfield(nSubField);
                            }

                            // Assign data
                            nSubField.setData(nRecord.getRecordContent());

                            // if yes, create a new master-field entry
                        /*else {
                            nDataField = factory.newDataField( String.format( "%03d", fieldCode ), '0', '0' );
                            currWriteRecord.addVariableField(nDataField);
                            nSubField = factory.newSubfield(subfieldCode.charAt(0), nRecord.getRecordContent());
                            nDataField.addSubfield(nSubField);
                            }*/
                            // copy master-entry
                        /*else {
                            DataField cDataField = factory.newDataField( String.format( "%03d", fieldCode ), '0', '0' );
                            currWriteRecord.addVariableField(cDataField);

                            // Copy all existing sub-fields
                            List subFields = nDataField.getSubfields();
                            for( int i = 0; i < subFields.size(); i++ ) {
                            Subfield cSubField = (Subfield) subFields.get(i);

                            // Overwrite old entry
                            if( cSubField.getCode() == subfieldCode.charAt(0) ) {
                            nSubField = factory.newSubfield( subfieldCode.charAt(0) , nRecord.getRecordContent() );
                            }
                            // Just add all others
                            else {
                            nSubField = factory.newSubfield( cSubField.getCode() , cSubField.getData() );
                            }
                            cDataField.addSubfield(nSubField);
                            }
                            }*/
                        }
                    }
                }
                // MARC4J doesn't support writing marc records without sub-fields
                else {
                    
                }

                //currWriteRecord.addVariableField(nDataField);
            }
        }
    }

    /**
     * Get a DataRecord for a given IDRecord in the current entry
     * @param IDRecord ID of record to return
     * @return Object of type DataRecord, with empty content if not found
     */
    public DataRecord getDataRecord( String IDRecord ) {
        // Create and initialize our return record
        DataRecord rRecord = new DataRecord();
        rRecord.setIDRecord(IDRecord);
        rRecord.setRecordContent( "" );

        // Check if we have a leader
        if( IDRecord.startsWith("leader") ) {
            // Check if we have a direct leader mapping
            if( IDRecord.equals("leader") ) {
                rRecord.setRecordContent( currWriteRecord.getLeader().marshal() );
            }
            // .. if not, analyze the mapping and where it should be mapped
            else {
                String[] components = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
                String charPosInfo = components[1];
                int charPosStart = 0;
                int charPosEnd = 0;

                // Check if we have a char-position range
                if( charPosInfo.indexOf("-") > 0 ) {
                    String[] charPosInfoComponents = charPosInfo.split("-");
                    charPosStart = Integer.valueOf( charPosInfoComponents[0] );
                    charPosEnd = Integer.valueOf( charPosInfoComponents[1] );
                }
                // If not, we have a single char
                else {
                    charPosStart = Integer.valueOf( components[1] );
                    charPosEnd = charPosStart;
                }

                // Get the leader and modify it
                Leader nLeader = currWriteRecord.getLeader();
                char[] values;
                for( int charPos = charPosStart; charPos <= charPosEnd; charPos++ ) {
                    switch( charPos ) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            rRecord.setRecordContent( String.valueOf( nLeader.getRecordLength() ) );
                            break;
                        case 5:
                            rRecord.setRecordContent( String.valueOf( nLeader.getRecordStatus() ) );
                            break;
                        case 6:
                            rRecord.setRecordContent( String.valueOf( nLeader.getTypeOfRecord() ) );
                            break;
                        case 7:
                        case 8:
                            rRecord.setRecordContent( String.valueOf( nLeader.getImplDefined1()[charPos -7] ) );
                            break;
                        case 9:
                            rRecord.setRecordContent( String.valueOf( nLeader.getCharCodingScheme() ) );
                            break;
                        case 10:
                            rRecord.setRecordContent( String.valueOf( nLeader.getIndicatorCount() ) );
                            break;
                        case 11:
                            rRecord.setRecordContent( String.valueOf( nLeader.getSubfieldCodeLength() ) );
                            break;
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                            rRecord.setRecordContent( String.valueOf( nLeader.getBaseAddressOfData() ) );
                            break;
                        case 17:
                        case 18:
                        case 19:
                            rRecord.setRecordContent( String.valueOf( nLeader.getImplDefined2() ) );
                            break;
                    }
                }
            }
        }
        else {
            String[] irComponents = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
            int fieldCode = 0;
            try {
                fieldCode = Integer.parseInt( irComponents[0] );
            }
            catch( Exception e ) {
                e.printStackTrace();
                return rRecord;
            }

            // We have a control field
            if( fieldCode <= 9 ) {
                ControlField nControlField = (ControlField) currWriteRecord.getVariableField( String.format( "%03d", fieldCode) );
                String currData = "";
                if( nControlField != null ) {
                    currData = nControlField.getData();
                }

                // Check if we have char positions defined or not
                if( irComponents.length > 1 ) {
                    int charPosStart = 0;
                    int charLength = 0;
                    String charPosInfo = irComponents[1];

                    // Check if we have a char-position range
                    if (charPosInfo.indexOf("-") > 0) {
                        String[] charPosInfoComponents = charPosInfo.split("-");
                        charPosStart = Integer.valueOf(charPosInfoComponents[0]);
                        charLength = Integer.valueOf(charPosInfoComponents[1]) - charPosStart + 1;
                    }
                    // If not, we have a single char
                    else {
                        charPosStart = Integer.valueOf(charPosInfo);
                        charLength = 1;
                    }

                    if( currData.length() > charPosStart ) {
                        if( currData.length() < ( charPosStart + charLength ) ) {
                            rRecord.setRecordContent( currData.substring(charPosStart) );
                        }
                        else {
                            rRecord.setRecordContent( currData.substring( charPosStart, charPosStart + charLength - 1 ));
                        }
                    }
                }
                else {
                    rRecord.setRecordContent( currData );
                }
            }
            // .. got a data field
            else {
                DataField nDataField = null;
                List dataFields = currWriteRecord.getVariableFields( String.format( "%03d", fieldCode) );
                if( !dataFields.isEmpty() ) {
                    nDataField = (DataField) dataFields.get( dataFields.size() - 1 );
                }

                if( nDataField != null ) {
                    // Check if we have a sub-fields
                    if( irComponents.length > 1 ) {
                        String subfieldCode = irComponents[1];

                        // Check if we have an indicator mapping
                        if( subfieldCode.equals("fInd") ) {
                            rRecord.setRecordContent( String.valueOf( nDataField.getIndicator1() ) );
                        }
                        else if( subfieldCode.equals("sInd") ) {
                            rRecord.setRecordContent( String.valueOf( nDataField.getIndicator2() ) );
                        }
                        // .. if not, create a normal subfield entry
                        else {
                            Subfield nSubField = nDataField.getSubfield( subfieldCode.charAt(0) );
                            // Check if the sub-field already exists, if not create a new one and add it
                            if( nSubField != null ) {
                                rRecord.setRecordContent( nSubField.getData() );
                            }
                        }
                    }
                    // MARC4J doesn't support marc records without sub-fields
                    else {
                        rRecord.setRecordContent( nDataField.toString() );
                    }
                }
            }
        }

        return rRecord;
    }

    /**
     * Called to inform the processor that it should read the next marc record
     */
    public void nextEntry() {
        // Check if we process an input file
        if( reader != null ) {
            Record currRecord = reader.next();

            //System.out.println( currRecord.toString() );

            // Create file record for leader
            Leader leader = currRecord.getLeader();
            DataRecord newRec = new DataRecord();
            newRec.setIDRecord("leader");
            newRec.setRecordContent(leader.marshal());
            recordsStack.add(newRec);

            // Get Control fields
            List fields = currRecord.getControlFields();
            Iterator it = fields.iterator();
            // Add all control fields to the stack
            while (it.hasNext()) {
                ControlField cf = (ControlField) it.next();
                newRec = new DataRecord();
                newRec.setIDRecord(cf.getTag());
                newRec.setRecordContent(cf.getData());
                recordsStack.add(newRec);
            }

            // Get all data fields
            fields = currRecord.getDataFields();

            it = fields.iterator();
            while (it.hasNext()) {
                DataField df = (DataField) it.next();

                // Remove the field code from the string version of the entry
                // Required because some fields may not contain ANY subfields at all, and those aren't really supported by MARC4J
                String dfContent = df.toString();
                Pattern p = Pattern.compile("^\\d+\\s+(.*)$");
                Matcher m = p.matcher(dfContent);
                if( m.matches() ) {
                    dfContent = m.group(1);
                }

                //System.out.println( "DataField Content: " + df.toString());
                // Add an entry for the whole datafield
                newRec = new DataRecord();
                newRec.setIDRecord(df.getTag());
                newRec.setRecordContent(dfContent);
                recordsStack.add(newRec);

                List subfields = df.getSubfields();
                Iterator subIt = subfields.iterator();
                while (subIt.hasNext()) {
                    Subfield subfield = (Subfield) subIt.next();
                    newRec = new DataRecord();
                    newRec.setIDRecord(df.getTag() + DataRecord.getIDSeperator() + subfield.getCode());
                    newRec.setRecordContent(subfield.getData());
                    recordsStack.add(newRec);
                }

                // Add the indicators to the stack
                newRec = new DataRecord();
                newRec.setIDRecord( df.getTag() + DataRecord.getIDSeperator() + "fInd" );
                newRec.setRecordContent( String.valueOf(df.getIndicator1()) );
                recordsStack.add(newRec);
                newRec = new DataRecord();
                newRec.setIDRecord( df.getTag() + DataRecord.getIDSeperator() + "sInd" );
                newRec.setRecordContent( String.valueOf(df.getIndicator2()) );
                recordsStack.add(newRec);
            }

            // Sort collection by fieldID (refer to the compareTo implementation in DataRecord class)
            Collections.sort( this.recordsStack );
        }
        else if( writer != null ) {
            //System.out.println( "Writing Next Record" );

            //System.out.println( "Writing Record with Leader: " + currWriteRecord.getLeader().marshal() );

            writer.write(currWriteRecord);
            currWriteRecord = factory.newRecord();
        }
    }

    @Override
    public void skipEntry() throws Exception {
        // just drop the current record & start with a fresh one
        if( writer != null ) {
            currWriteRecord = factory.newRecord();
        }
    }

    /**
     * Called to indicate that the current file processing is done
     */
    public void done() {
        if( writer != null ) {
            writer.close();
        }
    }

    /**
     * Iterator implementations
     */
    public boolean hasNext() {
        return (reader != null && (reader.hasNext() || super.hasNext()));
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "mrc", "mrk" };

        return arr;
    }
}
