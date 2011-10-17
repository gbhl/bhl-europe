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

import at.nhmwien.schema_mapping_tool.mappingProcess.MappingsHandler;

import jxl.*;
import jxl.write.*;

import java.util.*;

/**
 *
 * @author wkoller
 */
public class ExcelProcessor extends FileProcessor {
    private Workbook readBook = null;
    private Sheet readSheet = null;

    private WritableWorkbook writeBook = null;
    private WritableSheet writeSheet = null;

    private int currRow = 1;
    private ArrayList<String> fieldNames = null;

    public void prepareFileRead() throws Exception {
        this.fieldNames = new ArrayList<String>();

        this.readBook = Workbook.getWorkbook(this.operateFile);
        this.readSheet = this.readBook.getSheet(0);

        // Read header values for field names
        for( int i = 0; i < this.readSheet.getColumns(); i++ ) {
            this.fieldNames.add( this.readSheet.getCell( i, 0 ).getContents() );
        }

        //this.currRow = 1;
    }

    public void prepareFileWrite() throws Exception {
        this.fieldNames = MappingsHandler.Self().getOutputOrder();

        this.writeBook = Workbook.createWorkbook(this.operateFile);
        this.writeSheet = this.writeBook.createSheet("SMT Export", 0);

        for( int i = 0; i < fieldNames.size(); i++ ) {
            Label headerCell = new Label( i, 0, fieldNames.get(i) );
            this.writeSheet.addCell(headerCell);
        }
    }

    public void nextEntry() throws Exception {
        if( this.readSheet != null ) {
            ArrayList<String> fieldOrder = MappingsHandler.Self().getInputOrder();

            for( int i = 0; i < fieldOrder.size(); i++ ) {
                DataRecord nRecord = new DataRecord();
                nRecord.setIDRecord( fieldOrder.get(i) );
                nRecord.setRecordContent( this.readSheet.getCell( this.fieldNames.indexOf(fieldOrder.get(i)), this.currRow ).getContents() );

                this.recordsStack.add(nRecord);
            }
            //Collections.sort(this.recordsStack);
        }
        else {
            for( int i = 0; i < this.recordsStack.size(); i++ ) {
                DataRecord currRecord = this.recordsStack.get(i);

                int fieldIndex = this.fieldNames.indexOf(currRecord.getIDRecord());

                if( fieldIndex < 0 ) {
                    this.fieldNames.add(currRecord.getIDRecord());
                    fieldIndex = this.fieldNames.size() - 1;

                    // Add header row
                    Label headerCell = new Label( fieldIndex, 0, currRecord.getIDRecord() );
                    this.writeSheet.addCell(headerCell);
                }

                Label dataCell = new Label( fieldIndex, this.currRow, currRecord.getRecordContent() );
                this.writeSheet.addCell(dataCell);
            }
            this.recordsStack.clear();
        }
        
        this.currRow++;
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
            if( this.readBook != null ) {
                this.readBook.close();
                this.readSheet = null;
                this.readBook = null;
            }
            else {
                this.writeBook.write();
                this.writeBook.close();
                this.writeSheet = null;
                this.writeBook = null;
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean hasNext() {
        if( this.recordsStack.size() > 0 || this.currRow < this.readSheet.getRows() ) return true;

        return false;
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "xls" };

        return arr;
    }
}
