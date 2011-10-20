/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.*;
import java.util.Map.*;

/**
 * Handles the mappings
 * Note: You do not have to create an instance of this object, rather use the static self method
 * @author wkoller
 */
public class MappingsHandler implements Iterator<Entry<String,HashMap<String,MappingRecord>>> {
    //private HashMap<String,ArrayList<String>> fromMappings = null;  // Mappings in format: FROMID => TOIDs
    private HashMap<String,HashMap<String,MappingRecord>> outputMappings = null;    // Mappings in format: IDOutput => IDInput (multi)
    private Iterator<Entry<String,HashMap<String,MappingRecord>>> outMapIt = null;  // Internal iterator over mappings, required for own Iterator interface
    private int mappingCount = 0;                                       // Count the mappings
    private ArrayList<String> outputOrder = new ArrayList<String>();
    private ArrayList<String> inputOrder = new ArrayList<String>();

    private static MappingsHandler mySelf = null;   // Static Reference Pointer to own instance

    /**
     * Get a reference to the global Mappings Handler
     * @return MappingsHandler Reference to the global mappings-handler instance
     */
    public static MappingsHandler Self() {
        if( MappingsHandler.mySelf == null ) {
            MappingsHandler.mySelf = new MappingsHandler();
        }

        return MappingsHandler.mySelf;
    }

    /**
     * Constructor - can be private because we only reference it from a static function
     */
    private MappingsHandler() {
        //this.fromMappings = new HashMap<String,ArrayList<String>>();
        this.outputMappings = new HashMap<String,HashMap<String,MappingRecord>>();
    }

    /**
     * Add a mapping to the hash
     * @param String IDInput ID to map from
     * @param String IDOutput ID to map to
     */
    public void addMapping( String IDInput, String IDOutput ) {
        HashMap<String,MappingRecord> mappedIDs = null;

        // Check if the IDTo is already mapped
        if( this.outputMappings.containsKey(IDOutput) ) {
            mappedIDs = this.outputMappings.get(IDOutput);
        }
        else {
            mappedIDs = new HashMap<String,MappingRecord>();
        }

        // Check if this is a new mapping and add it if necessary
        // NOTE: We can ignore the warning, because we have an overwritten version of "equals" in MappingRecord
        if( !mappedIDs.keySet().contains(IDInput) ) {
            MappingRecord nRecord = new MappingRecord();
            nRecord.targetID = IDInput;
            mappedIDs.put(IDInput, nRecord);
            this.outputMappings.put(IDOutput, mappedIDs);

            // Add to our order-arrays if necessary
            if( !this.outputOrder.contains( IDOutput ) ) this.outputOrder.add( IDOutput );
            if( !this.inputOrder.contains( IDInput ) ) this.inputOrder.add( IDInput );

            this.mappingCount++;
        }
    }

    /**
     * Manipulate a mapping to set options etc.
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @param existsAction Action to take when the given mapping already exists in the current record
     * @deprecated Use setMapping instead
     * @return < 0 on error, 1 on success
     */
    public int manipulateMapping( String IDInput, String IDOutput, MappingRecord.existsActionType existsAction ) {
        HashMap<String,MappingRecord> mappedIDs = null;

        // Check if mapping exists at all
        if( this.outputMappings.containsKey( IDOutput ) ) {
            mappedIDs = this.outputMappings.get( IDOutput );

            // Check for IDInput in mapped fields
            if( mappedIDs.keySet().contains( IDInput ) ) {
                MappingRecord record = mappedIDs.get( IDInput );
                record.existsAction = existsAction;

                return 1;
            }
        }

        return -1;
    }

    /**
     * Return the MappingRecord for a given ID pair
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @return MappingRecord for both IDs, null if not found
     */
    public MappingRecord getMapping( String IDInput, String IDOutput ) {
        HashMap<String,MappingRecord> mappedIDs = null;

        // Check if mapping exists at all
        if( this.outputMappings.containsKey( IDOutput ) ) {
            mappedIDs = this.outputMappings.get( IDOutput );

            // Check for IDInput in mapped fields
            if( mappedIDs.keySet().contains( IDInput ) ) {
                return mappedIDs.get( IDInput );
            }
        }

        return null;
    }

    /**
     * Set the Mapping-Record for a given input / output pair
     * NOTE: Do not use this function to add mappings, because it won't work anyway
     * Setting the MappingRecord only works for existing mappings
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @param mr MappingRecord to set
     */
    public void setMapping( String IDInput, String IDOutput, MappingRecord mr ) {
         HashMap<String,MappingRecord> mappedIDs = null;

        // Check if mapping exists at all
        if( this.outputMappings.containsKey( IDOutput ) ) {
            mappedIDs = this.outputMappings.get( IDOutput );

            // Check for IDInput in mapped fields
            if( mappedIDs.keySet().contains( IDInput ) ) {
                mappedIDs.put( IDInput , mr );
            }
        }
    }

    /**
     * Remove a given mapping from the hash
     * @param String IDInput ID of the field to map from
     * @param String IDOutput ID of the field to map to
     * @return integer -1 if IDTo isn't found, -2 if IDFrom isn't found, +1 if everything went right
     */
    public int removeMapping( String IDInput, String IDOutput ) {
        // Check if we have the IDOutput at all
        if( this.outputMappings.containsKey(IDOutput) ) {
            HashMap<String,MappingRecord> mappedIDs = this.outputMappings.get(IDOutput);

            // Check if the IDInput is mapped to IDOutput
            if( mappedIDs.containsKey(IDInput) ) {
                mappedIDs.remove(IDInput);

                if( mappedIDs.size() <= 0 ) {
                    this.outputMappings.remove(IDOutput);
                    //this.outputOrder.remove( IDOutput );
                }
                //this.outputMappings.put(IDOutput, mappedIDs);
                this.mappingCount--;
                return 1;
            }
            // IDInput not mapped to IDOutput, return error code -2
            else {
                return -2;
            }
        }
        // IDOutput not found, return error code -1
        else {
            return -1;
        }
    }

    /**
     * Clear all mappings
     */
    public void clear() {
        this.outputMappings.clear();
        this.outputOrder.clear();
        this.inputOrder.clear();
        this.mappingCount = 0;
    }

    /**
     * Check if two fields are mapped
     * @param IDInput ID of Input Field
     * @param IDOutput ID of output Field
     * @return boolean true if mapped, false if not
     */
    public boolean isMapped( String IDInput, String IDOutput ) {
        if( this.outputMappings.containsKey(IDOutput) ) {
            HashMap<String,MappingRecord> mappedIDs = this.outputMappings.get(IDOutput);

            return mappedIDs.containsKey(IDInput);
        }
        else {
            return false;
        }
    }

    /**
     * Check if a given Input-field is mapped
     * @param IDInput ID of input field
     * @return true if mapped, false if not
     */
    public boolean isInputMapped( String IDInput ) {
        // Try to find the input ID
        Iterator<Map.Entry<String,HashMap<String,MappingRecord>>> mIt = this.outputMappings.entrySet().iterator();
        while( mIt.hasNext() ) {
            HashMap<String,MappingRecord> mappedIDs = mIt.next().getValue();

            if( mappedIDs.containsKey(IDInput) ) return true;
        }

        return false;
    }

    /**
     * Check if a given Output-field is mapped
     * @param IDOutput ID of output-field
     * @return boolean true if mapped, false if not
     */
    public boolean isOutputMapped( String IDOutput ) {
        return this.outputMappings.containsKey(IDOutput);
    }

    /**
     * Returns the mapping count (number of mappings done)
     * @return integer Mapping-Count
     */
    public int getMappingCount() {
        return this.mappingCount;
    }

    /**
     * Return a list of all mappings to a given output ID
     * @param IDOutput ID of output field
     * @return List of all input IDs which map to the given output ID, null if none
     */
    public ArrayList<String> getMappingsTo( String IDOutput ) {
        if( this.outputMappings.containsKey(IDOutput) ) {
            return new ArrayList( this.outputMappings.get(IDOutput).keySet() );
        }
        else {
            return null;
        }
    }

    public ArrayList<String> getMappingsFrom( String IDInput ) {
        ArrayList<String> mappedFrom = new ArrayList<String>();

        // Try to find the input ID
        Iterator<Map.Entry<String,HashMap<String,MappingRecord>>> mIt = this.outputMappings.entrySet().iterator();
        while( mIt.hasNext() ) {
            Map.Entry<String,HashMap<String,MappingRecord>> currEntry = mIt.next();
            HashMap<String,MappingRecord> mappedIDs = currEntry.getValue();

            if( mappedIDs.containsKey(IDInput) ) {
                mappedFrom.add(currEntry.getKey());
                continue;
            }
        }


        return mappedFrom;
    }

    public HashMap<String,HashMap<String,MappingRecord>> getMappings() {
        return this.outputMappings;
    }

    /**
     * Return the order of output fields
     * @return ArrayList of sorted output fields
     */
    public ArrayList<String> getOutputOrder() {
        return this.outputOrder;
    }

    /**
     * Set the output order
     * @param nOutputOrder ArrayList of ordered output fields
     */
    public void setOutputOrder( ArrayList<String> nOutputOrder ) {
        this.outputOrder = nOutputOrder;
    }

    /**
     * Get the input order
     * @return ArrayList of sorted input fields
     */
    public ArrayList<String> getInputOrder() {
        return inputOrder;
    }

    /**
     * Set the input order
     * @param inputOrder ArrayList of ordered input fields
     */
    public void setInputOrder(ArrayList<String> inputOrder) {
        this.inputOrder = inputOrder;
    }

   public void printMappings() {
        Iterator<Map.Entry<String,HashMap<String,MappingRecord>>> tmpIt = this.outputMappings.entrySet().iterator();

        while( tmpIt.hasNext() ) {
            Map.Entry<String,HashMap<String,MappingRecord>> currEntry = tmpIt.next();

            String IDOutput = currEntry.getKey();
            System.out.println( "Mapping(s) for ID '" + IDOutput + "'" );

            Iterator<Map.Entry<String,MappingRecord>> mfIt = currEntry.getValue().entrySet().iterator();

            while( mfIt.hasNext() ) {
                Map.Entry<String,MappingRecord> mfEntry = mfIt.next();

                String IDInput = mfEntry.getKey();
                MappingRecord mfRecord = mfEntry.getValue();

                System.out.println( "\t'" + IDInput + "': " + mfRecord.existsAction );
            }
        }
    }

    // Replace by serialize-able interface
    /*public void setMappings( HashMap<String,ArrayList<String>> nMappings ) {
        this.outputMappings = nMappings;
    }*/

    /**
     * Iterator Implementations
     */
    public boolean hasNext() {
        // If no iterator is set, receive a new one
        if( this.outMapIt == null ) {
            this.outMapIt = this.outputMappings.entrySet().iterator();
        }

        // If we do not have any entries left, remove the current iterator
        if( !this.outMapIt.hasNext() ) {
            this.outMapIt = null;
            
            return false;
        }

        return true;
    }

    public Entry<String,HashMap<String,MappingRecord>> next() {
        if( this.outMapIt == null ) return null;

        return this.outMapIt.next();
    }

    public void remove() { return; }
}
