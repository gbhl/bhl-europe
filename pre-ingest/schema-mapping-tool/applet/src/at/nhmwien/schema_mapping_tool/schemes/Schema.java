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

import java.util.*;
import java.io.*;
import at.nhmwien.schema_mapping_tool.schemaReaders.*;

/**
 *
 * @author wkoller
 */
public abstract class Schema {
    public LinkedHashMap<String,LinkedHashMap> getSchemaContent() {
        try {
            InputStream inputFile = getClass().getResourceAsStream( this.getResourceName() );

            FileConverter conv = this.getFileConverter();
            return conv.parseFile(inputFile);

/*            File tmpFile = File.createTempFile( "currSchema", null );
            FileWriter writer = new FileWriter( tmpFile );

            writer.write(schemaContent);
            writer.close();*/
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract FileConverter getFileConverter();
    protected abstract String getResourceName();
}
