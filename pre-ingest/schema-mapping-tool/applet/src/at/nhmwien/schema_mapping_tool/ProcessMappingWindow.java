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
package at.nhmwien.schema_mapping_tool;

import javax.swing.*;
import java.util.*;
import java.nio.charset.*;

import at.nhmwien.schema_mapping_tool.fileProcessors.*;
import at.nhmwien.schema_mapping_tool.mappingProcess.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author wkoller
 */
public class ProcessMappingWindow extends javax.swing.JFrame {
    MappingTask task;
    MappingProcess mp;
    DataSourceOptionsPanel inputOptionsPanel = null;
    DataSourceOptionsPanel outputOptionsPanel = null;
    private XMLConfiguration settings = new XMLConfiguration();

    // TODO: Improve mappingTask
    class MappingTask extends SwingWorker<Void, Void> {
        public Void doInBackground() {
            try {
                // Reset progress-Bar
                progressBar.setValue(0);
                progressBarLabel.setText("Creating File Processors");

                // Get the file-processors from the according options panels
                FileProcessor ifp = inputOptionsPanel.getProcessor();
                FileProcessor ofp = outputOptionsPanel.getProcessor();

                progressBar.setValue(10);
                progressBarLabel.setText("Preparing & Initializing File Processors");

                mp.setProcessor( ifp , ofp );

                mp.setInputFile( inputOptionsPanel.getFileName() , (Charset) ifEncodingComboBox.getSelectedItem() );
                mp.setOutputFile( outputOptionsPanel.getFileName() , (Charset) ofEncodingComboBox.getSelectedItem() );

    /*            if( ifp.getClass().getName().contains( "JDBCProcessor" ) ) {
    //                mp.setInputFile( jdbcURLTextField.getText() + dbnameTextField.getText() + "?user=" + usernameTextField.getText() + "&password=" + String.copyValueOf(passwordField.getPassword()) + "&" + tableField.getText() , (Charset) ifEncodingComboBox.getSelectedItem() );
                }
                else {
    //                mp.setInputFile( inputFileName.getText() , (Charset) ifEncodingComboBox.getSelectedItem() );
                }

                if( ofp.getClass().getName().contains( "JDBCProcessor" ) ) {
    //                mp.setOutputFile( jdbcURLTextField.getText() + dbnameTextField.getText() + "?user=" + usernameTextField.getText() + "&password=" + String.copyValueOf(passwordField.getPassword()) + "&" + tableField.getText() , (Charset) ifEncodingComboBox.getSelectedItem() );
                }
                else {
    //                mp.setOutputFile( outputFileName.getText() , (Charset) ofEncodingComboBox.getSelectedItem() );
                }*/

                /*try {
                    ifp.setFile(new File(inputFileName.getText()));
                    ifp.setEncoding( (Charset) ifEncodingComboBox.getSelectedItem());
                    ifp.prepareFileRead();

                    ofp.setFile(new File(outputFileName.getText()));
                    ofp.setEncoding( (Charset) ofEncodingComboBox.getSelectedItem());
                    ofp.prepareFileWrite();
                } catch (Exception e) {
                    e.printStackTrace();
                    //JOptionPane.showMessageDialog(this, "An error occured while trying to process the files:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }*/

                mp.setInputIDPrefix( inputIDPrefixTextField.getText() );
                mp.setCountThreshold( Integer.valueOf( countThresholdTextField.getText() ) );

                progressBar.setValue(15);
                progressBarLabel.setText("Converting Mappings-Hash & Preparing Processors");

                try {
                    mp.prepare();
                }
                catch( Exception e ) {
                    JOptionPane.showMessageDialog(null, "Error Preparing mapping process: " + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();

                    progressBarLabel.setText("Error - Error while preparing mapping process");
                    return null;
                }

                progressBarLabel.setText("Done - Starting with mapping process");
                // Re-structure the mappings set
                // Note we convert the mappings into an input to output HashMap here
                // because then we do not have to perform a search on each mapping
                /*HashMap<String, ArrayList<String>> tempMappings = new HashMap<String, ArrayList<String>>();
                while (MappingsHandler.self().hasNext()) {
    //            Map.Entry<String, ArrayList> entry = (Map.Entry<String, ArrayList>) mapsIt.next();
                    Map.Entry<String, HashMap<String,MappingRecord>> entry = MappingsHandler.self().next();
                    HashMap<String,MappingRecord> mappingsList = entry.getValue();
                    String outputFieldID = entry.getKey();

                    Iterator<String> mplIt = mappingsList.keySet().iterator();
                    while (mplIt.hasNext()) {
                        String inputFieldID = mplIt.next();
                        ArrayList<String> toMaps = null;

                        if (tempMappings.containsKey(inputFieldID)) {
                            toMaps = tempMappings.get(inputFieldID);
                        } else {
                            toMaps = new ArrayList<String>(0);
                        }
                        toMaps.add(outputFieldID);
                        tempMappings.put(inputFieldID, toMaps);
                    }
                }*/
                progressBar.setValue(25);

                //MappingsHandler.self().printMappings();

                while( mp.processMapping() ) {
                    if( Thread.interrupted() ) {
                        progressBarLabel.setText( "Aborted" );
                        break;
                    }

                    progressBarLabel.setText("Processing Entries - " + mp.getEntriesDone() + " Done.");
                }
            }
            catch( Exception e ) {
                JOptionPane.showMessageDialog( null, "Error Occured during processing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();

                progressBarLabel.setText( "Error Occured! Processed entries: " + mp.getEntriesDone() );
            }

            abortButton.setEnabled(false);
            processButton.setEnabled(true);

            /*boolean bDataMapped = false;
            int entriesDone = 0;
            HashMap<String,String> persistentMappings = new HashMap<String,String>();
            while (ifp.hasNext()) {
                // Check if thread got aborted
                if( Thread.interrupted() ) {
                    progressBarLabel.setText( "Aborted" );
                    progressBar.setValue(100);
                    return null;
                }

                DataRecord fr = ifp.next();

                if (fr == null) {
                    ifp.nextEntry();
                    if (bDataMapped) {
                        ofp.nextEntry();
                        bDataMapped = false;
                    }
                    fr = ifp.next();

                    entriesDone++;
                    progressBarLabel.setText("Processing Entries - " + entriesDone + " Done.");
                    persistentMappings = new HashMap<String,String>();
                }

                //System.out.println( "Processing Input: " + fr.getIDRecord() );

                //System.out.println( "InputID: " + fr.getIDRecord() + " Content: " + fr.getRecordContent() );

                // Check if the input ID is mapped
                if (tempMappings.containsKey(fr.getIDRecord())) {
                    ArrayList<String> targets = tempMappings.get(fr.getIDRecord());

                    Iterator<String> tIt = targets.iterator();
                    while (tIt.hasNext()) {
                        boolean bAddNew = false;
                        String currTarget = tIt.next();

                        //System.out.println( "InputID: " + fr.getIDRecord() + " mapped to: " + currTarget );
                        // Get Mapping record for our current mapping
                        MappingRecord cMR = MappingsHandler.self().getMapping(fr.getIDRecord(), currTarget);
                        // Get current datarecord
                        DataRecord cDR = ofp.getDataRecord(currTarget);
                        String recordContent = fr.getRecordContent();

                        // Manipulate record content
                        recordContent = ManipulationsHandler.self().manipulateValue(fr.getIDRecord(), currTarget, recordContent);

                        // Check if existing record is non-empty
                        if( !cDR.getRecordContent().isEmpty() ) {
                            // Now check the action
                            switch (cMR.existsAction) {
                                // Concatenate the contents
                                case CONCATENATE:
                                    recordContent = cDR.getRecordContent() + " " + recordContent;
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
                        if( cMR.persistentMapping ) {
                            persistentMappings.put( currTarget , recordContent );
                        }

                        // Set record content
                        DataRecord ofr = new DataRecord();
                        ofr.setRecordContent(recordContent);
                        ofr.setIDRecord(currTarget);
                        //fr.setRecordContent(recordContent);
                        // Update ID of record
                        //fr.setIDRecord(currTarget);

                        // Write the data record
                        ofp.addDataRecord(ofr, bAddNew, cMR.persistentMapping );

                        // Check if we added a new entry, if yes we have to re-run all persistent mappings
                        if( bAddNew ) {
                            Iterator<String> pmIt = persistentMappings.keySet().iterator();
                            while( pmIt.hasNext() ) {
                                String target = pmIt.next();

                                ofr.setRecordContent( persistentMappings.get( target ) );
                                ofr.setIDRecord(target);

                                ofp.addDataRecord(ofr, false, true);
                            }
                        }

                        bDataMapped = true;
                    }
                }
            }
            progressBar.setValue(90);

            // Finalize the mapping process
            ifp.done();
            ofp.done();*/

            mp.done();

            progressBar.setValue(100);

            return null;
        }
    }

    /** Creates new form ProcessMappingWindow */
    public ProcessMappingWindow() {
        initComponents();

        // Disable the abort button by default
        this.abortButton.setEnabled(false);

        // Add the available file types to the dropdown
        String[] fpTypes = ProcessorHandler.self().getProcessors();
        DefaultComboBoxModel iffCbModel = new DefaultComboBoxModel();
        DefaultComboBoxModel offCbModel = new DefaultComboBoxModel();
        for( int i = 0; i < fpTypes.length; i++ ) {
            iffCbModel.addElement(fpTypes[i]);
            offCbModel.addElement(fpTypes[i]);

            // Get all valid file-types for this handler and add a filenameextensionfilter for it
            /*String[] fpFileTypes = ProcessorHandler.self().getSupportedFilesForType(fpTypes[i]);
            if( fpFileTypes != null ) {
                String fpFTDescription = fpTypes[i] + " (*." + fpFileTypes[0];
                for( int j = 1; j < fpFileTypes.length; j++ ) {
                    fpFTDescription += ", *." + fpFileTypes[j];
                }
                fpFTDescription += ")";

                //FileNameExtensionFilter filter = new FileNameExtensionFilter( fpFTDescription, fpFileTypes );
                //fc.addChoosableFileFilter(filter);
            }*/
        }
        this.inputFileFormatComboBox.setModel(iffCbModel);
        this.outputFileFormatComboBox.setModel(offCbModel);

        // Add all available character encodings to the drop-down
        DefaultComboBoxModel ifECbModel = new DefaultComboBoxModel();
        DefaultComboBoxModel ofECbModel = new DefaultComboBoxModel();

        SortedMap<String,Charset> availChars = Charset.availableCharsets();
        Iterator<Map.Entry<String,Charset>> acIt = availChars.entrySet().iterator();
        while( acIt.hasNext() ) {
            Map.Entry<String,Charset> currEntry = acIt.next();

            ifECbModel.addElement(currEntry.getValue());
            ofECbModel.addElement(currEntry.getValue());
        }
        
        this.ifEncodingComboBox.setModel(ifECbModel);
        this.ofEncodingComboBox.setModel(ofECbModel);

        this.mp = new MappingProcess();

        // Create our initial settings windows
        inputFileFormatComboBoxActionPerformed( null );
        outputFileFormatComboBoxActionPerformed( null );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressBarLabel = new javax.swing.JLabel();
        abortButton = new javax.swing.JButton();
        processButton = new javax.swing.JButton();
        typeSelectTabbedPanel = new javax.swing.JTabbedPane();
        optionsPanel = new javax.swing.JPanel();
        inputFileFormatLabel = new javax.swing.JLabel();
        outputFileFormatLabel = new javax.swing.JLabel();
        inputFileFormatComboBox = new javax.swing.JComboBox();
        outputFileFormatComboBox = new javax.swing.JComboBox();
        ifEncodingLabel = new javax.swing.JLabel();
        ifEncodingComboBox = new javax.swing.JComboBox();
        ofEncodingLabel = new javax.swing.JLabel();
        ofEncodingComboBox = new javax.swing.JComboBox();
        cooButton = new javax.swing.JButton();
        cioButton = new javax.swing.JButton();
        inputIDPrefixLabel = new javax.swing.JLabel();
        inputIDPrefixTextField = new javax.swing.JTextField();
        countThresholdLabel = new javax.swing.JLabel();
        countThresholdTextField = new javax.swing.JFormattedTextField();
        menuToolBar = new javax.swing.JToolBar();
        loadSettingsButton = new javax.swing.JButton();
        saveSettingsButton = new javax.swing.JButton();

        setTitle("Process Mapping");
        setResizable(false);

        progressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mapping Progress", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N

        progressBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressBarLabel.setText("Progress");

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBarLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE))
                .addContainerGap())
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addComponent(progressBarLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        abortButton.setText("Abort");
        abortButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortButtonActionPerformed(evt);
            }
        });

        processButton.setText("Process");
        processButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processButtonActionPerformed(evt);
            }
        });

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Process Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N

        inputFileFormatLabel.setText("Input Format:");

        outputFileFormatLabel.setText("Output Format:");

        inputFileFormatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputFileFormatComboBoxActionPerformed(evt);
            }
        });

        outputFileFormatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFileFormatComboBoxActionPerformed(evt);
            }
        });

        ifEncodingLabel.setText("Encoding:");

        ofEncodingLabel.setText("Encoding:");

        cooButton.setText("Output Order");
        cooButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cooButtonActionPerformed(evt);
            }
        });

        cioButton.setText("Input Order");
        cioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cioButtonActionPerformed(evt);
            }
        });

        inputIDPrefixLabel.setText("Input ID Prefix:");

        countThresholdLabel.setText("New File every X entries:");

        countThresholdTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        countThresholdTextField.setText("0");

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(inputFileFormatLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(outputFileFormatLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionsPanelLayout.createSequentialGroup()
                                .addComponent(inputFileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ifEncodingLabel))
                            .addGroup(optionsPanelLayout.createSequentialGroup()
                                .addComponent(outputFileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ofEncodingLabel)))
                        .addGap(18, 18, 18)
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ofEncodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ifEncodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addComponent(cioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 434, Short.MAX_VALUE)
                        .addComponent(cooButton))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addComponent(inputIDPrefixLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputIDPrefixTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addComponent(countThresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(countThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputFileFormatLabel)
                    .addComponent(inputFileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ifEncodingLabel)
                    .addComponent(ifEncodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputFileFormatLabel)
                    .addComponent(outputFileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ofEncodingLabel)
                    .addComponent(ofEncodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(inputIDPrefixLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputIDPrefixTextField))
                .addGap(18, 18, 18)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(countThresholdLabel)
                    .addComponent(countThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cioButton)
                    .addComponent(cooButton))
                .addContainerGap())
        );

        menuToolBar.setRollover(true);

        loadSettingsButton.setText("Load");
        loadSettingsButton.setFocusable(false);
        loadSettingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadSettingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSettingsButtonActionPerformed(evt);
            }
        });
        menuToolBar.add(loadSettingsButton);

        saveSettingsButton.setText("Save");
        saveSettingsButton.setFocusable(false);
        saveSettingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveSettingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSettingsButtonActionPerformed(evt);
            }
        });
        menuToolBar.add(saveSettingsButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(typeSelectTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(abortButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 541, Short.MAX_VALUE)
                .addComponent(processButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(menuToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(typeSelectTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abortButton)
                    .addComponent(processButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void processButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processButtonActionPerformed
        task = new MappingTask();
        task.execute();

        this.processButton.setEnabled(false);
        this.abortButton.setEnabled(true);
    }//GEN-LAST:event_processButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed
        //this.setVisible(false);
        if( task != null ) task.cancel(true);

        task = null;

        this.processButton.setEnabled(true);
        this.abortButton.setEnabled(false);
    }//GEN-LAST:event_abortButtonActionPerformed

    private void inputFileFormatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputFileFormatComboBoxActionPerformed
        if( this.inputOptionsPanel != null ) this.typeSelectTabbedPanel.remove( this.inputOptionsPanel );

        // TODO: Replace check with something less static
        if( ((String) this.inputFileFormatComboBox.getSelectedItem()).contains( "JDBC" ) ) {
            this.inputOptionsPanel = new JDBCConnectPanel();
        }
        else {
            this.inputOptionsPanel = new FileSelectPanel();
        }
        this.inputOptionsPanel.setType(true);
        this.inputOptionsPanel.setSourceName((String) this.inputFileFormatComboBox.getSelectedItem());

        this.typeSelectTabbedPanel.insertTab( "Input", null, this.inputOptionsPanel, null, 0 );
        this.typeSelectTabbedPanel.setSelectedIndex( 0 );

        //this.inputTabPanel.add( this.inputOptionsPanel );
        //this.inputTabPanel.validate();
    }//GEN-LAST:event_inputFileFormatComboBoxActionPerformed

    private void outputFileFormatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputFileFormatComboBoxActionPerformed
        if( this.outputOptionsPanel != null ) this.typeSelectTabbedPanel.remove( this.outputOptionsPanel );

        // TODO: Replace check with something less static
        if( ((String) this.outputFileFormatComboBox.getSelectedItem()).contains( "JDBC" ) ) {
            this.outputOptionsPanel = new JDBCConnectPanel();
        }
        else {
            this.outputOptionsPanel = new FileSelectPanel();
        }
        this.outputOptionsPanel.setType(false);
        this.outputOptionsPanel.setSourceName((String) this.outputFileFormatComboBox.getSelectedItem());

        this.typeSelectTabbedPanel.insertTab( "Output", null, this.outputOptionsPanel, null, 1 );
        this.typeSelectTabbedPanel.setSelectedIndex( 1 );

        //this.outputTabPanel.add( this.outputOptionsPanel );
        //this.outputTabPanel.validate();
    }//GEN-LAST:event_outputFileFormatComboBoxActionPerformed

    private void cooButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cooButtonActionPerformed
        ChangeFieldOrderWindow.Self().setVisible(true, MainWindow.FIELD_OUTPUT);
    }//GEN-LAST:event_cooButtonActionPerformed

    private void cioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cioButtonActionPerformed
        ChangeFieldOrderWindow.Self().setVisible(true, MainWindow.FIELD_INPUT);
    }//GEN-LAST:event_cioButtonActionPerformed

    private void loadSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSettingsButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("SMT Processing Settings (*.sps)", "sps");
        fc.addChoosableFileFilter(fnef);

        // Let the user chose a settings file
        if( fc.showDialog(this, "Load Processing Settings") == JFileChooser.APPROVE_OPTION ) {
            try {
                this.settings = new XMLConfiguration(fc.getSelectedFile());

                // Update the interface to show all saved settings
                this.inputFileFormatComboBox.setSelectedItem(this.settings.getProperty("config.input-file-processor"));
                this.outputFileFormatComboBox.setSelectedItem(this.settings.getProperty("config.output-file-processor"));
                this.ifEncodingComboBox.setSelectedItem(Charset.forName( (String) this.settings.getProperty("config.input-file-encoding") ));
                this.ofEncodingComboBox.setSelectedItem(Charset.forName( (String) this.settings.getProperty("config.output-file-encoding") ));
                this.inputIDPrefixTextField.setText( (String) this.settings.getProperty("config.input-id-prefix" ) );
                this.countThresholdTextField.setText( (String) this.settings.getProperty("config.count-threshold") );
                MappingsHandler.Self().setInputOrder( (ArrayList<String>) this.settings.getProperty( "config.input-field-order") );
                MappingsHandler.Self().setOutputOrder( (ArrayList<String>) this.settings.getProperty( "config.output-field-order") );

                // Now load the processor specific settings
                FileProcessor fp = this.inputOptionsPanel.getProcessor();
                String options[] = fp.getAvailableOptions();
                for( String option : options ) {
                    if( this.settings.containsKey("config.inputProcessor.options." + option) ) fp.setOption(option, this.settings.getProperty("config.inputProcessor.options." + option) );
                }
                fp = this.outputOptionsPanel.getProcessor();
                options = fp.getAvailableOptions();
                for( String option : options ) {
                    if( this.settings.containsKey("config.outputProcessor.options." + option) ) fp.setOption(option, this.settings.getProperty("config.outputProcessor.options." + option) );
                }

                // Update options from processor for display
                this.inputOptionsPanel.loadOptions();
                this.outputOptionsPanel.loadOptions();

                /*Iterator it = this.settings.getKeys("config.inputProcessor.options");
                while( it.hasNext() ) {
                    System.out.println( it.next().toString() );
                }*/
                //this.settings.get
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_loadSettingsButtonActionPerformed

    /**
     * Save all settings to a given file
     * @param evt
     */
    private void saveSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSettingsButtonActionPerformed
        // Save all configuration properties
        this.settings.setProperty("config.input-file-processor", this.inputFileFormatComboBox.getSelectedItem());
        this.settings.setProperty("config.output-file-processor", this.outputFileFormatComboBox.getSelectedItem());
        this.settings.setProperty("config.input-file-encoding", this.ifEncodingComboBox.getSelectedItem().toString());
        this.settings.setProperty("config.output-file-encoding", this.ofEncodingComboBox.getSelectedItem().toString());
        this.settings.setProperty("config.input-id-prefix", this.inputIDPrefixTextField.getText());
        this.settings.setProperty("config.count-threshold", this.countThresholdTextField.getText());
        this.settings.setProperty("config.input-field-order", MappingsHandler.Self().getInputOrder());
        this.settings.setProperty("config.output-field-order", MappingsHandler.Self().getOutputOrder());
        // Save File-Processor specific options
        FileProcessor fp = this.inputOptionsPanel.getProcessor();
        String fpOptions[] = fp.getAvailableOptions();
        for( String fpOption : fpOptions ) {
           this.settings.setProperty( "config.inputProcessor.options." + fpOption, fp.getOption(fpOption));
        }
        fp = this.outputOptionsPanel.getProcessor();
        fpOptions = fp.getAvailableOptions();
        for( String fpOption : fpOptions ) {
           this.settings.setProperty( "config.outputProcessor.options." + fpOption, fp.getOption(fpOption));
        }

        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("SMT Processing Settings (*.sps)", "sps");
        fc.addChoosableFileFilter(fnef);

        // Let the user chose a settings file
        if( fc.showDialog(this, "Save Processing Settings") == JFileChooser.APPROVE_OPTION ) {
            try {
                this.settings.save(fc.getSelectedFile());
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_saveSettingsButtonActionPerformed

    /**
     * Load all processing settings from a given file
     * @param evt
     */
    /**
     * Save all settings to a given configuration file
     * @param evt
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton cioButton;
    private javax.swing.JButton cooButton;
    private javax.swing.JLabel countThresholdLabel;
    private javax.swing.JFormattedTextField countThresholdTextField;
    private javax.swing.JComboBox ifEncodingComboBox;
    private javax.swing.JLabel ifEncodingLabel;
    private javax.swing.JComboBox inputFileFormatComboBox;
    private javax.swing.JLabel inputFileFormatLabel;
    private javax.swing.JLabel inputIDPrefixLabel;
    private javax.swing.JTextField inputIDPrefixTextField;
    private javax.swing.JButton loadSettingsButton;
    private javax.swing.JToolBar menuToolBar;
    private javax.swing.JComboBox ofEncodingComboBox;
    private javax.swing.JLabel ofEncodingLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JComboBox outputFileFormatComboBox;
    private javax.swing.JLabel outputFileFormatLabel;
    private javax.swing.JButton processButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressBarLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JButton saveSettingsButton;
    private javax.swing.JTabbedPane typeSelectTabbedPanel;
    // End of variables declaration//GEN-END:variables

}
