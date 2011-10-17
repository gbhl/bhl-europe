/*
 * Read schema information from a delimited file
 */

package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.io.*;
//import java.nio.charset.*;

import com.csvreader.*;

/**
 *
 * @author wkoller
 */
public class DelFileConverter extends FileConverter {
    public LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile ) {
        LinkedHashMap<String,LinkedHashMap> fields = null;

        try {
            //CsvReader reader = new CsvReader(inputFile, '|', Charset.defaultCharset() );
            CsvReader reader = new CsvReader( new InputStreamReader( inputFile, "UTF-8" ) );
            reader.setDelimiter( ';' );
            reader.readHeaders();

            String[] headers = reader.getHeaders();

            fields = new LinkedHashMap();

            for( int i = 0; i < headers.length; i++ ) {
                String cellName = headers[i];

                LinkedHashMap fieldInfo = new LinkedHashMap();
                fieldInfo.put( "name" , cellName );
                fieldInfo.put( "subfields" , null );

                fields.put( cellName, fieldInfo );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return fields;


/*        try {
            Workbook readBook = Workbook.getWorkbook(inputFile);
            Sheet readSheet = readBook.getSheet(0);

            fields = new LinkedHashMap();

            for( int i = 0; i < readSheet.getColumns(); i++ ) {
                String cellName = readSheet.getCell(i, 0).getContents();

                // Add info about this node into our structure
                LinkedHashMap fieldInfo = new LinkedHashMap();
                fieldInfo.put( "name", cellName );
                fieldInfo.put( "subfields", null );

                fields.put( cellName, fieldInfo );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }*/

//        return fields;
    }
}
