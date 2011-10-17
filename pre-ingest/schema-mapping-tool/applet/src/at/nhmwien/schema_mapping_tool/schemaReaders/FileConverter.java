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

/*
 * Definition for a file converter
 * Defines all required functions
 */
package at.nhmwien.schema_mapping_tool.schemaReaders;

import java.util.*;
import java.io.*;

/**
 *
 * @author wkoller
 */
public abstract class FileConverter {
    // We provide two ways to call the parseFile function, because reading the defs from a jar requires a stream
    //public abstract LinkedHashMap<String,LinkedHashMap> parseFile( File inputFile );
    public abstract LinkedHashMap<String,LinkedHashMap> parseFile( InputStream inputFile );

    public LinkedHashMap<String,LinkedHashMap> parseFile( File inputFile ) {
        try {
            return this.parseFile(new FileInputStream( inputFile ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }
}
