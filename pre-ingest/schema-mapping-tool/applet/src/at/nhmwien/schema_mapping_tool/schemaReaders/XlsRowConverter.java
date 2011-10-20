package at.nhmwien.schema_mapping_tool.schemaReaders;

import jxl.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author wkoller
 */

public class XlsRowConverter extends FileConverter {
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap<String,LinkedHashMap> fields = null;

        try {
            Workbook readBook = Workbook.getWorkbook(inputFile);
            Sheet readSheet = readBook.getSheet(0);

            fields = new LinkedHashMap();

            for( int i = 0; i < readSheet.getRows(); i++ ) {
                String cellName = readSheet.getCell(0, i).getContents();
                
                if( cellName.isEmpty() ) continue;

                // Add info about this node into our structure
                LinkedHashMap fieldInfo = new LinkedHashMap();
                fieldInfo.put( "name", cellName );
                fieldInfo.put( "subfields", null );

                fields.put( cellName, fieldInfo );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return fields;
    }
}
