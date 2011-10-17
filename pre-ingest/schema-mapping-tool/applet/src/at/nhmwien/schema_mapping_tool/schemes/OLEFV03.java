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
package at.nhmwien.schema_mapping_tool.schemes;

import at.nhmwien.schema_mapping_tool.mappingProcess.MapFileHandler;
import at.nhmwien.schema_mapping_tool.schemaReaders.FileConverter;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 *
 * @author wkoller
 */


public class OLEFV03 extends Schema {
    public LinkedHashMap<String,LinkedHashMap> getSchemaContent() {
        
        try {
            InputStream inputFile = getClass().getResourceAsStream( this.getResourceName() );

            MapFileHandler loader = new MapFileHandler();
            loader.loadFile( inputFile );
            
            return loader.getOutputFields();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    protected String getResourceName() {
        return "resources/OLEF_output.map";
    }

    protected FileConverter getFileConverter() {
        return null;
    }
}
