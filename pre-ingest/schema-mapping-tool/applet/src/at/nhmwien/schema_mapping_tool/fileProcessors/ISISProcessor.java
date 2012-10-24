/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import ISISJAVA.*;
import java.util.regex.*;
import java.util.*;

/**
 *
 * @author wkoller
 */
public class ISISProcessor extends FileProcessor {
    protected int appHandler = 0;
    protected int spaceHandler = 0;
    protected int returnCode = 0;
    protected IsisRecControl controlStruct = null;
    protected int currIndex = 1;
    protected HashMap<String,HashMap<Character,String>> writeBuffer = null;
    
    public void prepareFileRead() throws Exception {
        appHandler = ISISAPI.IsisAppNew();
        ISISAPI.IsisAppDebug(appHandler, Constants.SHOW_NEVER);
        spaceHandler = ISISAPI.IsisSpaNew(appHandler);
        returnCode = ISISAPI.IsisSpaMf(spaceHandler, this.operateFile.getAbsolutePath());

        controlStruct = new IsisRecControl();
        returnCode = ISISAPI.IsisRecControlMap( spaceHandler, controlStruct );

        //controlStruct.nxtmfn = 101;

        currIndex = 1;
        this.writeBuffer = null;
    }

    public void prepareFileWrite() throws Exception {
        //throw new Exception();
        appHandler = ISISAPI.IsisAppNew();
        ISISAPI.IsisAppDebug(appHandler, Constants.SHOW_NEVER);
        spaceHandler = ISISAPI.IsisSpaNew(appHandler);
        returnCode = ISISAPI.IsisSpaMf(spaceHandler, this.operateFile.getAbsolutePath() );
        ISISAPI.IsisSpaMfCreate(spaceHandler);

        this.writeBuffer = new HashMap<String,HashMap<Character,String>>();

        // Reset any reading indicators
        this.controlStruct = null;
        this.currIndex = 1;
    }

    public void nextEntry() {
        // Check if we are in read mode
        if( this.controlStruct != null ) {
            String[] nextRecord = new String[1];

            //System.out.println( "Reading Next Record: " + currIndex );

            returnCode = ErrorCodes.ERR_RECPHYSDEL;
            while( returnCode != ErrorCodes.ZERO ) {
                returnCode = ISISAPI.IsisRecRead(spaceHandler, 0, currIndex);

                if( returnCode != ErrorCodes.ZERO ) System.out.println( "Skipping Record " + currIndex );
                
                currIndex++;
            }
            returnCode = ISISAPI.IsisRecDump(spaceHandler, 0, nextRecord, Constants.MAXMFRL);

            // Looks complicated, but we have to do it that way, because in Java we have no chance to convert a String object from a given encoding
            // That's because the ISISAPI fills the string, and in java we can only convert encodings while filling the string object
            byte[] rawData = new byte[nextRecord[0].length()] ;
            for( int i = 0; i < nextRecord[0].length(); i++ ) {
                rawData[i] = (byte) nextRecord[0].charAt(i);
            }

            String recordContent = "";
            try {
                recordContent =  new String( rawData, this.fileEncoding );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
            
            // Add the MFN-Number to the stack
            // TODO Replace IDRecord once custom field adding is supported
            DataRecord nRecord = new DataRecord();
            nRecord.setIDRecord( "001" );
            nRecord.setRecordContent( String.valueOf(currIndex - 1) );
            this.recordsStack.add(nRecord);

            //this.charDecoder.decode( ByteBuffer.wrap( nextRecord[0] ) );

            //String recordContent = new String( nextRecord[0].getBytes( this.fileEncoding ), this.fileEncoding );

            Pattern p = Pattern.compile("<(\\d+)>(.*)</\\1>");
            Matcher m = p.matcher(recordContent);

            // temporary stack used for sorting
            TreeMap<String,ArrayList<ArrayList<DataRecord>>> currRecordsStack = new TreeMap<String,ArrayList<ArrayList<DataRecord>>>();
            
            // parse & add all fields
            while (m.find()) {
                ArrayList<DataRecord> currFieldRecords = new ArrayList<DataRecord>();
                //System.out.println( "Found: " + m.group(1) + " / Content: " + m.group(2) + " / " + (int) m.group(2).charAt(3) );

                /*if( m.group(1).equals( "260$a" ) ) {
                    System.out.println( "DEBUG: " + m.group(1) + " Char: '" + m.group(1).charAt(3) + "'" );
                }*/
                // Convert to 3 digit code (required)
                String fieldCode = String.format( "%03d", Integer.valueOf( m.group(1) ) );
                //String fieldCode = m.group(1);

                // Add an entry for the whole record
                nRecord = new DataRecord();
                nRecord.setIDRecord( fieldCode );
                nRecord.setRecordContent( m.group(2) );

                //this.recordsStack.add(nRecord);
                currFieldRecords.add(nRecord);

                // Find the sub-fields and add them to the stack
                String[] subFields = m.group(2).split("\\^");
                if (subFields.length > 1) {
                    for (int i = 1; i < subFields.length; i++) {
                        if( subFields[i].length() <= 0 ) continue;
                        nRecord = new DataRecord();
                        nRecord.setIDRecord(fieldCode + DataRecord.getIDSeperator() + subFields[i].charAt(0));
                        nRecord.setRecordContent(subFields[i].substring(1));

                        //this.recordsStack.add(nRecord);
                        currFieldRecords.add(nRecord);
                    }
                }
                Collections.sort(currFieldRecords);
                
                // fetch fitting entry from temporary stack
                ArrayList<ArrayList<DataRecord>> currRecordsStackEntry = currRecordsStack.get(fieldCode);
                if( currRecordsStackEntry == null ) {
                    currRecordsStackEntry = new ArrayList<ArrayList<DataRecord>>();
                }
                // add the new (sorted) record to the stack
                currRecordsStackEntry.add(currFieldRecords);
                currRecordsStack.put(fieldCode, currRecordsStackEntry);
            }
            
            // add the temporary stack to the internal recordsStack
            // we use this as the entries are now correctly sorted
            Iterator<Map.Entry<String,ArrayList<ArrayList<DataRecord>>>> crs_It = currRecordsStack.entrySet().iterator();
            while( crs_It.hasNext() ) {
                Map.Entry<String,ArrayList<ArrayList<DataRecord>>> currEntry = crs_It.next();
                Iterator<ArrayList<DataRecord>> ce_It = currEntry.getValue().iterator();
                while(ce_It.hasNext()) {
                    Iterator<DataRecord> dr_it = ce_It.next().iterator();
                    while( dr_it.hasNext() ) {
                        this.recordsStack.add(dr_it.next());
                    }
                }
            }

            // Sort collection by fieldID (refer to the compareTo implementation in DataRecord class)
            //Collections.sort( this.recordsStack );
        }
        // Check if we are in write mode
        else if( this.writeBuffer != null ) {
            String recordString = "";
            Iterator<Map.Entry<String, HashMap<Character,String>>> wbIt = this.writeBuffer.entrySet().iterator();
            while( wbIt.hasNext() ) {
                Map.Entry<String, HashMap<Character,String>> currEntry = wbIt.next();
                String fieldCode = currEntry.getKey();
                HashMap<Character,String> subfields = currEntry.getValue();

                String fieldString = "<" + fieldCode + ">";

                Iterator<Map.Entry<Character,String>> sfIt = subfields.entrySet().iterator();
                while( sfIt.hasNext() ) {
                    Map.Entry<Character,String> currSfEntry = sfIt.next();
                    Character subfieldCode = currSfEntry.getKey();
                    String content = currSfEntry.getValue();

                    if( subfieldCode.equals( ' ' ) ) {
                        fieldString += content;
                        break;
                    }
                    else {
                        fieldString += "^" + subfieldCode + content;
                    }
                }

                fieldString += "</" + fieldCode + ">\n";
                recordString += fieldString;
            }

            returnCode = ISISAPI.IsisRecNew(spaceHandler, 0);
            ISISAPI.IsisRecUpdate(spaceHandler, 0, recordString);
            ISISAPI.IsisRecWrite(spaceHandler, 0);

            this.writeBuffer = new HashMap<String, HashMap<Character,String>>();
        }
    }

    @Override
    public void skipEntry() throws Exception {
        this.writeBuffer = new HashMap<String, HashMap<Character,String>>();
    }
    
    public void done() {
        ISISAPI.IsisSpaDelete(spaceHandler);
        ISISAPI.IsisAppDelete(appHandler);
    }

    // TODO: Implement replace / addNew / replaceAll functionality
    public void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll ) {
        //ISISAPI.IsisRecFieldUpdate(spaceHandler, 0, "a" );
        String[] irComponents = nRecord.getIDRecord().split( Pattern.quote( DataRecord.getIDSeperator() ) );
        String fieldCode = irComponents[0];

        char subfieldCode = ' ';
        if( irComponents.length > 1 ) {
            subfieldCode = irComponents[1].charAt(0);
        }

        HashMap<Character, String> subfields = null;

        if (this.writeBuffer.containsKey(fieldCode)) {
            subfields = this.writeBuffer.get(fieldCode);
        } else {
            subfields = new HashMap<Character, String>();
        }
        subfields.put(subfieldCode, nRecord.getRecordContent());

        this.writeBuffer.put(fieldCode, subfields);
    }

    /**
     * Return a data record for a given IDRecord in the current entry
     * @param IDRecord ID of record to return
     * @return DataRecord with empty content if nothing found
     */
    public DataRecord getDataRecord( String IDRecord ) {
        DataRecord rRecord = new DataRecord();
        rRecord.setIDRecord(IDRecord);
        rRecord.setRecordContent("");

        String[] irComponents = IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
        String fieldCode = irComponents[0];

        char subfieldCode = ' ';
        if( irComponents.length > 1 ) {
            subfieldCode = irComponents[1].charAt(0);
        }

        HashMap<Character, String> subfields = null;
        if (this.writeBuffer.containsKey(fieldCode)) {
            subfields = this.writeBuffer.get(fieldCode);

            if( subfields.containsKey( subfieldCode ) ) {
                rRecord.setRecordContent( subfields.get(subfieldCode) );
            }
        }
        
        return rRecord;
    }

    public boolean hasNext() {
        return (this.controlStruct != null && (this.currIndex < this.controlStruct.nxtmfn || super.hasNext() ) );
    }

    public String[] getSupportedFileExtensions() {
        String exts[] = { "fdt" };

        return exts;
    }
}
