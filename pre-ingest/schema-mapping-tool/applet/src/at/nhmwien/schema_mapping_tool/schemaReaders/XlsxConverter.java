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

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author wkoller
 */


public class XlsxConverter extends FileConverter {
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap<String,LinkedHashMap> fields = null;
        
        try {
            
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
            XSSFSheet sheet = workbook.getSheetAt( 0 );
            XSSFRow row = sheet.getRow(0);
            
            Iterator<Cell> cellIt = row.cellIterator();
            
            fields = new LinkedHashMap();
            
            while( cellIt.hasNext() ) {
                Cell currCell = cellIt.next();
                
                // Ignore any non-string cells (because they are not suitable as headers)
                if( currCell.getCellType() != Cell.CELL_TYPE_STRING ) continue;

                LinkedHashMap fieldInfo = new LinkedHashMap();
                fieldInfo.put( "name", currCell.getStringCellValue() );
                fieldInfo.put( "subfields", null );

                fields.put( currCell.getStringCellValue(), fieldInfo );
            }
            
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        
        return fields;
    }
}
