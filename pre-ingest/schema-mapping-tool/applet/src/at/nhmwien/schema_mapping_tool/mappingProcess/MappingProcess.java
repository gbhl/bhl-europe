/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.nhmwien.schema_mapping_tool.mappingProcess;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
//import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import at.nhmwien.schema_mapping_tool.fileProcessors.*;

/**
 *
 * @author wkoller
 */
public class MappingProcess {

    protected FileProcessor ifp = null;
    protected FileProcessor ofp = null;
    protected String inputFile = null;
    protected String outputFile = null;
    protected Charset inputCharset = null;
    protected Charset outputCharset = null;
    protected String inputIDPrefix = "";  // User specified ID offset, useful it you want to map down a tree

    protected HashMap<String, ArrayList<String>> tempMappings = null;
    protected boolean bDataMapped = false;
    protected HashMap<String, String> persistentMappings = null;

    protected int entriesDone = 0;
    protected int countThreshold = 0;

    public MappingProcess() {
    }

    public void setInputIDPrefix( String p_inputIDPrefix ) {
        this.inputIDPrefix = p_inputIDPrefix;
    }

    public void setCountThreshold( int p_countThreshold ) {
        this.countThreshold = p_countThreshold;
    }

    public void setProcessor(FileProcessor nIfp, FileProcessor nOfp) {
        this.ifp = nIfp;
        this.ofp = nOfp;
    }

    public void setInputFile(String nInputFile, Charset charSet) {
        this.inputFile = nInputFile;
        this.inputCharset = charSet;
    }

    public void setOutputFile(String nOutputFile, Charset charSet) {
        this.outputFile = nOutputFile;
        this.outputCharset = charSet;
    }

    public int getEntriesDone() {
        return this.entriesDone;
    }

    public void prepare() throws Exception {
        // Re-structure the mappings set
        // Note we convert the mappings into an input to output HashMap here
        // because then we do not have to perform a search on each mapping
        tempMappings = new HashMap<String, ArrayList<String>>();
        while (MappingsHandler.Self().hasNext()) {
            Map.Entry<String, HashMap<String, MappingRecord>> entry = MappingsHandler.Self().next();
            HashMap<String, MappingRecord> mappingsList = entry.getValue();
            String outputFieldID = entry.getKey();

            Iterator<String> mplIt = mappingsList.keySet().iterator();
            while (mplIt.hasNext()) {
                String inputFieldID = mplIt.next();
                ArrayList<String> toMaps = null;

                // Prefix the IDRecord with the inputIDOffsetPrefix
                /*if( !this.inputIDPrefix.isEmpty() ) {
                    inputFieldID = this.inputIDPrefix + DataRecord.getIDSeperator() + inputFieldID;
                    //fr.setIDRecord( this.inputIDPrefix + DataRecord.getIDSeperator() + fr.getIDRecord() );
                }*/

                if (tempMappings.containsKey(inputFieldID)) {
                    toMaps = tempMappings.get(inputFieldID);
                } else {
                    toMaps = new ArrayList<String>(0);
                }
                toMaps.add(outputFieldID);
                tempMappings.put(inputFieldID, toMaps);
            }
        }

        ifp.setFileName(this.inputFile);
        ifp.setEncoding(this.inputCharset);
        ifp.prepareFileRead();

        ofp.setFileName(this.outputFile);
        ofp.setEncoding(this.outputCharset);
        ofp.prepareFileWrite();

        this.bDataMapped = false;
        this.persistentMappings = new HashMap<String, String>();
        this.entriesDone = 0;
    }

    /**
     * execute a mapping step
     * @return false if error occured / finished, true on success
     * @throws Exception 
     */
    public boolean processMapping() throws Exception {
        if (this.ifp == null || this.ofp == null || this.inputFile == null || this.outputFile == null || this.outputCharset == null || this.inputCharset == null || this.persistentMappings == null || this.tempMappings == null ) {
            System.err.println("[MappingProcess] Set input and output settings before calling processMapping()");

            return false;
        }

        if (ifp.hasNext()) {
            DataRecord fr = ifp.next();

            if (fr == null) {
                ifp.nextEntry();
                if (bDataMapped) {
                    ofp.nextEntry();
                    bDataMapped = false;
                }
                fr = ifp.next();

                //entriesDone++;
                this.entriesDone++;

                // Check if we need to start a new file
                if( !JDBCProcessor.class.isInstance(ofp) & this.countThreshold > 0 && this.entriesDone % this.countThreshold == 0 ) {
                    ofp.done();

                    // Construct new output file name
                    int dotLocation = this.outputFile.lastIndexOf( "." );
                    if( dotLocation < 0 ) dotLocation = this.outputFile.length() - 1;
                    // Read extension + basename
                    String basename = this.outputFile.substring(0, dotLocation);
                    String extension = this.outputFile.substring(dotLocation);

                    //ofp.setFileName(this.outputFile + "_" + (int)(this.entriesDone / 100) );
                    ofp.setFileName(basename + "_" + (int)(this.entriesDone / this.countThreshold) + extension );
                    ofp.setEncoding(this.outputCharset);
                    ofp.prepareFileWrite();
                }

                persistentMappings = new HashMap<String, String>();
            }
            
            // check if we match a skip filter
            if( MappingsHandler.Self().matchSkipFilter(fr.getIDRecord(), fr.getRecordContent()) ) {
                ofp.skipEntry();    // discard anything mapped so far in this entry
                ifp.nextEntry();    // continue with next entry
                
                //System.out.println("[INFO] Skipping entry due to applying skip-filter.");
                
                return true;
            }
            
            // Prefix the IDRecord with the inputIDOffsetPrefix
            /*if( !this.inputIDPrefix.isEmpty() ) {
                fr.setIDRecord( this.inputIDPrefix + DataRecord.getIDSeperator() + fr.getIDRecord() );
            }*/

            //System.out.println( "Processing Input: " + fr.getIDRecord() );
            //System.out.println( "InputID: " + fr.getIDRecord() + " Content: " + fr.getRecordContent() );
            // Check if the record starts with the inputID prefix
            if( fr.getIDRecord().startsWith( this.inputIDPrefix ) ) {
                fr.setIDRecord( fr.getIDRecord().replaceFirst( Pattern.quote( this.inputIDPrefix ), "") );
            }

            // Check if the input ID is mapped
            if (tempMappings.containsKey(fr.getIDRecord())) {
                ArrayList<String> targets = tempMappings.get(fr.getIDRecord());

                Iterator<String> tIt = targets.iterator();
                while (tIt.hasNext()) {
                    boolean bAddNew = false;
                    String currTarget = tIt.next();

                    //System.out.println( "InputID: " + fr.getIDRecord() + " mapped to: " + currTarget );
                    // Get Mapping record for our current mapping
                    MappingRecord cMR = MappingsHandler.Self().getMapping(fr.getIDRecord(), currTarget);
                    // Get current datarecord
                    DataRecord cDR = ofp.getDataRecord(currTarget);
                    String recordContent = fr.getRecordContent();

                    // Manipulate record content
                    recordContent = ManipulationsHandler.self().manipulateValue(fr.getIDRecord(), currTarget, recordContent);

                    // Check if existing record is non-empty
                    if (!cDR.getRecordContent().isEmpty()) {
                        // Now check the action
                        switch (cMR.existsAction) {
                            // Concatenate the contents
                            case CONCATENATE:
                                if( !recordContent.isEmpty() ) {
                                    recordContent = cDR.getRecordContent() + " " + recordContent;
                                }
                                else {
                                    recordContent = cDR.getRecordContent();
                                }
                                break;
                            case PREPEND:
                                if( !recordContent.isEmpty() ) {
                                    recordContent = recordContent + " " + cDR.getRecordContent();
                                }
                                else {
                                    recordContent = cDR.getRecordContent();
                                }
                                break;
                            // TODO: Implement COPY action
                            case COPY:
                                break;
                            // Add a new entry
                            case NEW:
                                bAddNew = true;
                                break;
                            // Keep old content
                            case KEEP:
                                recordContent = cDR.getRecordContent();
                                break;
                            // Only replace content if the new value is not empty
                            // Which means: keep old content if value is empty
                            case REPLACEIFNOTEMPTY:
                                if (recordContent.isEmpty()) {
                                    recordContent = cDR.getRecordContent();
                                }
                                break;
                            // No additional action required
                            default:
                                break;
                        }
                    }

                    // Check if this is a persistant mapping
                    if (cMR.persistentMapping) {
                        persistentMappings.put(currTarget, recordContent);
                    }

                    // Set record content
                    DataRecord ofr = new DataRecord();
                    ofr.setRecordContent(recordContent);
                    ofr.setIDRecord(currTarget);
                    //fr.setRecordContent(recordContent);
                    // Update ID of record
                    //fr.setIDRecord(currTarget);

                    // Write the data record
                    ofp.addDataRecord(ofr, bAddNew, cMR.persistentMapping);

                    // Check if we added a new entry, if yes we have to re-run all persistent mappings
                    if (bAddNew) {
                        Iterator<String> pmIt = persistentMappings.keySet().iterator();
                        while (pmIt.hasNext()) {
                            String target = pmIt.next();

                            ofr.setRecordContent(persistentMappings.get(target));
                            ofr.setIDRecord(target);

                            ofp.addDataRecord(ofr, false, true);
                        }
                    }

                    bDataMapped = true;
                }
            }

            return true;
        }

        // Write data as a last step
        if( this.bDataMapped ) ofp.nextEntry();

        return false;
    }

    public void done() {
        // Finalize the mapping process
        ifp.done();
        ofp.done();
    }
}
