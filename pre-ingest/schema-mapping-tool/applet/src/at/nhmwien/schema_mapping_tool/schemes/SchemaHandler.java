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

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author wkoller
 */
public class SchemaHandler {
    private static SchemaHandler mySelf = null;
    private HashMap<String,Schema> availableSchemes = null;

    public static SchemaHandler self() {
        if( SchemaHandler.mySelf == null ) {
            SchemaHandler.mySelf = new SchemaHandler();
        }

        return SchemaHandler.mySelf;
    }

    private SchemaHandler() {
        this.availableSchemes = new HashMap<String,Schema>();

        this.availableSchemes.put( "ese-v3.3", new EseV33() );
        this.availableSchemes.put( "ese-v3.2", new EseV32() );
        this.availableSchemes.put( "Marc 21", new Marc21() );
        this.availableSchemes.put( "Mods 3.4", new ModsV34() );
        this.availableSchemes.put( "OLEF 0.3", new OLEFV03() );
        this.availableSchemes.put( "ABCD 2.06", new AbcdV206() );
    }

    public String[] getAvailableSchemes() {
        return this.availableSchemes.keySet().toArray(new String[0]);
    }

    public LinkedHashMap<String,LinkedHashMap> getSchemaFields( String schemaName ) {
        return this.availableSchemes.get(schemaName).getSchemaContent();
    }
}
