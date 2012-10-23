/*
 * Process a delimited file
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import at.nhmwien.schema_mapping_tool.mappingProcess.MappingsHandler;

import java.io.*;
import java.util.*;

import com.csvreader.*;

/**
 *
 * @author wkoller
 */
public class DelFileProcessor extends FileProcessor {
    private CsvReader reader = null;
    private CsvWriter writer = null;

    private int currRow = 1;
    private ArrayList<String> fieldNames = null;

    public DelFileProcessor() {
        this.configuration.setProperty( "noHeader", 0 );
        this.configuration.setProperty( "delimiter", ";" );
    }

    public void prepareFileRead() throws Exception {
        this.reader = new CsvReader( new FileInputStream( this.operateFile ), this.configuration.getString("delimiter").charAt(0), this.fileEncoding );
        reader.readHeaders();

        this.fieldNames = new ArrayList<String>();
        String[] headers = reader.getHeaders();

        this.fieldNames.addAll( Arrays.asList(headers) );

        // Read header values for field names
        /*for( int i = 0; i < headers.length; i++ ) {
            this.fieldNames.add( headers[i] );
        }*/
    }

    public void prepareFileWrite() throws Exception {
        this.writer = new CsvWriter( new FileOutputStream( this.operateFile ), this.configuration.getString("delimiter").charAt(0), this.fileEncoding );
        this.fieldNames = MappingsHandler.Self().getOutputOrder();

        if( this.configuration.getInt("noHeader") == 0 ) {
            String[] fieldArray = new String[this.fieldNames.size()];
            for( int i = 0; i < fieldArray.length; i++ ) {
                fieldArray[i] = this.fieldNames.get(i);
            }
            this.writer.writeRecord( fieldArray );
        }

        /*this.writeBook = Workbook.createWorkbook(this.operateFile);
        this.writeSheet = this.writeBook.createSheet("SMT Export", 0);*/
    }

    public void nextEntry() throws Exception {
        if( this.reader != null ) {
            ArrayList<String> fieldOrder = MappingsHandler.Self().getInputOrder();

            for( int i = 0; i < fieldOrder.size(); i++ ) {
                DataRecord nRecord = new DataRecord();

                //this.fieldNames.indexOf(fieldOrder.get(i));

                nRecord.setIDRecord( fieldOrder.get(i) );
                nRecord.setRecordContent( this.reader.get(this.fieldNames.indexOf(fieldOrder.get(i))) );

                this.recordsStack.add(nRecord);
            }

            //Collections.sort(this.recordsStack);
        }
        else {
            String[] recordList = new String[this.fieldNames.size()];

            for( int i = 0; i < this.recordsStack.size(); i++ ) {
                DataRecord currRecord = this.recordsStack.get(i);

                int fieldIndex = this.fieldNames.indexOf(currRecord.getIDRecord());

                if( fieldIndex >= 0 ) {
                    recordList[fieldIndex] = currRecord.getRecordContent();
                }
            }

            this.writer.writeRecord( recordList );
            this.recordsStack.clear();
        }

        this.currRow++;
    }

    @Override
    public void skipEntry() throws Exception {
        this.recordsStack.clear();
    }

    public void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll ) {
        for( int i = 0; i < this.recordsStack.size(); i++ ) {
            DataRecord currRecord = this.recordsStack.get(i);

            if( currRecord.getIDRecord().equals( nRecord.getIDRecord() ) ) {
                this.recordsStack.remove(i);

                break;
            }
        }

        this.recordsStack.add(nRecord);
    }

    public DataRecord getDataRecord( String IDRecord ) {
        DataRecord nRecord = new DataRecord();
        nRecord.setIDRecord(IDRecord);
        nRecord.setRecordContent( "" );

        for( int i = 0; i < this.recordsStack.size(); i++ ) {
            DataRecord currRecord = this.recordsStack.get(i);

            if( currRecord.getIDRecord().equals( nRecord.getIDRecord() ) ) {
                nRecord.setRecordContent( currRecord.getRecordContent() );

                break;
            }
        }

        return nRecord;
    }

    public void done() {
        try {
            if( this.reader != null ) {
                this.reader.close();
                this.reader = null;
            }
            else {
                this.writer.flush();
                this.writer.close();
                this.writer = null;
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean hasNext() {
        try {
            if( this.recordsStack.size() > 0 || reader.readRecord() ) return true;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return false;
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "csv" };

        return arr;
    }
}
