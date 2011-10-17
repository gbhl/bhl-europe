/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import java.util.regex.Pattern;

/**
 * A data record is the basic information of a single entry in a data-file
 * So e.g. the following XML-Snippet:
 *
 * <tag1>info1</tag1>
 * <tag2>info2</tag2>
 *
 * Consists of two data-records:
 * - Record1: IDRecord = tag1; RecordContent = info1
 * - Record2: IDRecord = tag2; RecordContent = info2
 *
 * @author wkoller
 */
public class DataRecord implements Comparable<DataRecord> {
    protected String IDRecord;
    protected String RecordContent;

    private static String IDSeperator = "$";

    public static String getIDSeperator() {
        return DataRecord.IDSeperator;
    }

    public DataRecord() {
        IDRecord = null;
        RecordContent = null;
    }

    public void setIDRecord( String nIDRecord ) {
        this.IDRecord = nIDRecord;
    }

    public String getIDRecord() {
        return this.IDRecord;
    }

    public void setRecordContent( String nRecordContent ) {
        if( nRecordContent == null ) {
            nRecordContent = "";
        }

        this.RecordContent = nRecordContent;
    }

    public String getRecordContent() {
        return this.RecordContent;
    }

    // TODO: Check processing order
    public int compareTo( DataRecord otherRecord ) {
        String[] orIDcomponents = otherRecord.getIDRecord().split( Pattern.quote( DataRecord.getIDSeperator() ) );
        String[] myIDcomponents = this.IDRecord.split( Pattern.quote( DataRecord.getIDSeperator() ) );
        //String orID = otherRecord.getIDRecord().split( "\\$" )[0];
        //String myID = this.IDRecord.split( "\\$" )[0];
        String orID = orIDcomponents[0];
        String myID = myIDcomponents[0];

        int compareVal = myID.compareTo(orID);
        if( compareVal == 0 && orIDcomponents.length > 1 && myIDcomponents.length > 1 ) {
            // Compare the sub-ids
            return this.IDRecord.compareTo( otherRecord.getIDRecord() );
        }
        // Check if we compare against the main entry here
        /*else if( compareVal == 0 ) {
            if( myIDcomponents.length > 1 ) {
                return 1;
            }
            else {
                return -1;
            }
        }*/

        return compareVal;

        //return myID.compareTo(orID);

        //return this.IDRecord.compareTo( otherRecord.getIDRecord() );
    }
}
