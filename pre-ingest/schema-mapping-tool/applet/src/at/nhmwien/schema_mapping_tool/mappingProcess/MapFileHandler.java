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
package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author wkoller
 */
public class MapFileHandler {
    /**
     * Private constructor, as we want this to be a singleton
     */
    //private MapFileHandler() {}

    private static MapFileHandler mySelf = null;

    /**
     * Singleton access function
     * @return instance of MapFileHandler
     */
    public static MapFileHandler self() {
        if( MapFileHandler.mySelf == null ) {
            MapFileHandler.mySelf = new MapFileHandler();
        }

        return MapFileHandler.mySelf;
    }

    private HashMap<String,HashMap<String,MappingRecord>> mappings = null;
    private HashMap<String,ArrayList<ManipulationRecord>> manipulations = null;
    private LinkedHashMap<String,LinkedHashMap> inputFields = null;
    private LinkedHashMap<String,LinkedHashMap> outputFields = null;
    private HashMap<String,String> skipFilters = null;

    /**
     * Loads a given file into the instance, properties can be requested using the getter functions
     * @param fileName
     * @throws Exception
     */
    public void loadFile( File fileName ) throws Exception {
        FileInputStream fis = new FileInputStream( fileName );
        this.loadFile(fis);
    }
    
    public void loadFile( InputStream fis ) throws Exception {
        ObjectInputStream ois = new ObjectInputStream( fis );

        this.setMappings((HashMap<String, HashMap<String, MappingRecord>>) ois.readObject());
        this.setManipulations((HashMap<String, ArrayList<ManipulationRecord>>) ois.readObject());
        this.setInputFields((LinkedHashMap<String, LinkedHashMap>) ois.readObject());
        this.setOutputFields((LinkedHashMap<String, LinkedHashMap>) ois.readObject());
        
        // for backwards compatibility, this is optional
        try { this.setSkipFilters((HashMap<String, String>) ois.readObject()); }
        catch(Exception e) {}

        ois.close();
        fis.close();
    }

    /**
     * Overloaded function, behaves like loadFile above
     * @param fileName Name of file
     * @throws Exception
     */
    public void loadFile( String fileName ) throws Exception {
        this.loadFile( new File(fileName) );
    }

    /**
     * Save all mapping data to a given file
     * @param fileName
     * @throws Exception
     */
    public void saveFile( File p_file ) throws Exception {
        FileOutputStream fos = new FileOutputStream( p_file );
        ObjectOutputStream oos = new ObjectOutputStream( fos );

        oos.writeObject(this.getMappings());
        oos.writeObject(this.getManipulations());
        oos.writeObject(this.getInputFields());
        oos.writeObject(this.getOutputFields());
        oos.writeObject(this.getSkipFilters());

        oos.close();
        fos.close();
    }

    /**
     * Overloaded function for easier access
     * @param p_fileName
     * @throws Exception
     */
    public void saveFile( String p_fileName ) throws Exception {
        this.saveFile( new File(p_fileName) );
    }

    /**
     * @return the mappings
     */
    public HashMap<String, HashMap<String, MappingRecord>> getMappings() {
        return mappings;
    }

    /**
     * @param mappings the mappings to set
     */
    public void setMappings(HashMap<String, HashMap<String, MappingRecord>> mappings) {
        this.mappings = mappings;
    }

    /**
     * @return the manipulations
     */
    public HashMap<String, ArrayList<ManipulationRecord>> getManipulations() {
        return manipulations;
    }

    /**
     * @param manipulations the manipulations to set
     */
    public void setManipulations(HashMap<String, ArrayList<ManipulationRecord>> manipulations) {
        this.manipulations = manipulations;
    }

    /**
     * @return the inputFields
     */
    public LinkedHashMap<String, LinkedHashMap> getInputFields() {
        return inputFields;
    }

    /**
     * @param inputFields the inputFields to set
     */
    public void setInputFields(LinkedHashMap<String, LinkedHashMap> inputFields) {
        this.inputFields = inputFields;
    }

    /**
     * @return the outputFields
     */
    public LinkedHashMap<String, LinkedHashMap> getOutputFields() {
        return outputFields;
    }

    /**
     * @param outputFields the outputFields to set
     */
    public void setOutputFields(LinkedHashMap<String, LinkedHashMap> outputFields) {
        this.outputFields = outputFields;
    }

    public HashMap<String, String> getSkipFilters() {
        return skipFilters;
    }

    public void setSkipFilters(HashMap<String, String> skipFilters) {
        this.skipFilters = skipFilters;
    }
}
