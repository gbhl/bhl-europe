/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.*;

/**
 *
 * @author wkoller
 */
public class ManipulationsHandler {
    private HashMap<String,ArrayList<ManipulationRecord>> manipulations = null;
    private HashMap<String,Manipulation> availManipulations = null;

    private static ManipulationsHandler mySelf = null;

    /**
     * Get a reference to the global manipulation-handler
     * @return ManipulationsHandler
     */
    public static ManipulationsHandler self() {
        if( ManipulationsHandler.mySelf == null ) ManipulationsHandler.mySelf = new ManipulationsHandler();

        return ManipulationsHandler.mySelf;
    }

    /**
     * Construct the manipulation handler, prepare everything
     */
    private ManipulationsHandler() {
        this.manipulations = new HashMap<String,ArrayList<ManipulationRecord>>();
        this.availManipulations = new HashMap<String,Manipulation>();

        // Add instances of all available manipulations
        this.availManipulations.put("regExp", new Manipulation_RegExp());
        this.availManipulations.put("replace", new Manipulation_Replace());
        this.availManipulations.put("lowerCase", new Manipulation_LowerCase());
        this.availManipulations.put("upperCase", new Manipulation_UpperCase());
        this.availManipulations.put("strLen", new Manipulation_StrLen());
        this.availManipulations.put("mathAdd", new Manipulation_MathAdd());
    }

    /**
     * Add a new manipulation for a given mapping
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @param type Type of manipulation (e.g. regExp)
     * @param parameters Parameters for the manipulation
     * @return int -1 if manipulation-type is unknown, +1 if everything went well
     */
    public int addManipulation( String IDInput, String IDOutput, String type, Object parameters ) {
        // Check if this is a known manipulation
        if( !this.availManipulations.containsKey(type) ) return -1;

        ManipulationRecord nRecord = new ManipulationRecord();
        nRecord.type = type;
        nRecord.parameters = parameters;

        // Check if we already have a manipulation
        ArrayList<ManipulationRecord> mpList = null;
        if( this.manipulations.containsKey( IDInput + IDOutput ) ) {
            mpList = this.manipulations.get( IDInput + IDOutput );
        }
        else {
            mpList = new ArrayList<ManipulationRecord>(0);
        }

        mpList.add(nRecord);
        this.manipulations.put(IDInput + IDOutput, mpList);

        return 1;
    }

    /**
     * Get a list of manipulations for a given mapping
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @return
     */
    public ArrayList<ManipulationRecord> getManipulations( String IDInput, String IDOutput ) {
        return this.manipulations.get( IDInput + IDOutput );
    }

    /**
     * Set manipulations for a given mapped field-pair
     * NOTE: Any old (existing) manipulations for those fields will be overwritten
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @param nRecords ArrayList of Manipulation-Records
     */
    public void setManipulations( String IDInput, String IDOutput, ArrayList<ManipulationRecord> nRecords ) {
        this.manipulations.put( IDInput + IDOutput, nRecords );
    }

    /**
     * Return all available types of manipulations
     * @return String[] List of all registered manipulations
     */
    public String[] getManipulationTypes() {
        return this.availManipulations.keySet().toArray(new String[0]);
    }

    /**
     * Apply the manipulation to a given value
     * @param IDInput ID of input field
     * @param IDOutput ID of output field
     * @param content Content to apply the manipulaton to
     * @return String manipulated content
     */
    public String manipulateValue( String IDInput, String IDOutput, String content ) {
        // Apply global manipulation(s)
        //if( !IDInput.equals( "global" ) && !IDOutput.equals( "global" ) ) content = this.manipulateValue( "global", "global", content );
        // Apply any existing general manipulations (including global)
        if( !IDInput.equals( "*" ) && !IDOutput.equals( "*" ) ) {
            content = this.manipulateValue( "*", "*", content );
            content = this.manipulateValue( "*", IDOutput, content );
            content = this.manipulateValue( IDInput, "*", content );
        }

        ArrayList<ManipulationRecord> mpList = this.manipulations.get(IDInput + IDOutput);
        //ManipulationRecord mRecord = this.manipulations.get(IDInput + IDOutput);

        if( mpList != null ) {
            Iterator<ManipulationRecord> mpListIt = mpList.iterator();
            while( mpListIt.hasNext() ) {
                ManipulationRecord mRecord = mpListIt.next();
                Manipulation mp = this.availManipulations.get(mRecord.type);

                if( mp == null ) {
                    System.out.println( "Error - unable to find manipulation for type: " + mRecord.type );
                    continue;
                }

                content = mp.applyManipulation(content, mRecord.parameters);

                // Apply optional manipulations
                // Check if the string should be trimmed
                if( mRecord.options.containsKey( "trim" ) ) content = content.trim();
            }
        }
        return content;
    }

    /**
     * Remove all manipulations
     */
    public void clear() {
        this.manipulations.clear();
    }

    /**
     * Get the whole manipulations hash (used for saving)
     * @return HashMap of all manipulations
     */
    public HashMap<String,ArrayList<ManipulationRecord>> getManipulations() {
        return this.manipulations;
    }

    /**
     * Set all manipulations (used for loading)
     * @param nManipulations Manipulations Hash
     */
    public void setManipulations( HashMap<String,ArrayList<ManipulationRecord>> nManipulations ) {
        this.manipulations = nManipulations;

        // Remove any reference to "global" and replace with "*"
        ArrayList<ManipulationRecord> mpList = this.getManipulations( "global" , "global" );
        if( mpList != null ) this.setManipulations( "*" , "*" , mpList );

        this.manipulations.remove( "global" + "global" );
    }
}
