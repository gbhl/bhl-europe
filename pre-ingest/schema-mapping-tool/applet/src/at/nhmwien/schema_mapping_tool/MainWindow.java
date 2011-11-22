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

import at.nhmwien.schema_mapping_tool.mappingProcess.*;
import at.nhmwien.schema_mapping_tool.schemes.SchemaHandler;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

import at.nhmwien.schema_mapping_tool.converter.MARC21Converter;
import at.nhmwien.schema_mapping_tool.converter.MARCXMLConverter;
import at.nhmwien.schema_mapping_tool.converter.MODSConverter;
import at.nhmwien.schema_mapping_tool.converter.REFNUMConverter;
import java.nio.charset.Charset;

// at.nhmwien.schema_mapping_tool.fileProcessors.*;

/**
 *
 * @author wkoller
 */
public class MainWindow extends javax.swing.JFrame implements ItemListener {
    // Define our constants
    public static final int FIELD_INPUT = 1;
    public static final int FIELD_OUTPUT = 2;

    // Class variable
    // Extra dialogs and windows
    JFileChooser fc;
    InputConversionWindow icw;
    ProcessMappingWindow pmw;
    ManipulationsWindow mw;
    JDBCWindow jdcbw;
    AddFieldWindow afw;

    FileNameExtensionFilter mapFilter = null;   // Filter for mapping files
    FileNameExtensionFilter iso2709Filter = null;   // Filter for MARC21 (ISO2709) files
    FileNameExtensionFilter xmlFilter = null;   // Filter for XML Files
    
    MappingFieldPanel selectedInputMFP, selectedOutputMFP;
    LinkedHashMap<String,LinkedHashMap> inputFields, outputFields;

    JPanel mappedFieldsContainer;
    FieldContainer inputFieldContainer, outputFieldContainer;

    File currMappingFile;     // Name of current mappings-file
    String defaultTitle;        // Default title of window

    int currGridY = 0;  // Current gridY for mapped fields
    
    public static MainWindow mySelf = null;

    // Singleton implementation
    public static MainWindow Self() {
        if( MainWindow.mySelf == null ) {
            MainWindow.mySelf = new MainWindow();
        }

        return MainWindow.mySelf;
    }

    /** Initializes the applet MainWindow
     */
    private MainWindow() {
        // Initialize custom components
        fc = new JFileChooser((String) null);
        icw = new InputConversionWindow();
        pmw = new ProcessMappingWindow();
        mw = new ManipulationsWindow();
        jdcbw = new JDBCWindow();
        afw = new AddFieldWindow();

        // Initialize file-filters
        String iso2709FileExts[] = { "mrc", "mrk" };
        this.mapFilter = new FileNameExtensionFilter( "SMT Mapping Files (*.map)", "map" );
        this.iso2709Filter = new FileNameExtensionFilter( "MARC21 Files (*.mrc, *.mrk)", iso2709FileExts );
        this.xmlFilter = new FileNameExtensionFilter( "XML Files (*.xml)", "xml" );

        selectedInputMFP = null;
        selectedOutputMFP = null;
        inputFields = new LinkedHashMap<String, LinkedHashMap>();
        outputFields = new LinkedHashMap<String, LinkedHashMap>();

        currMappingFile = null;

        // Initialize main components and setup layout
        initComponents();

        // Save the default title
        this.defaultTitle = this.getTitle();

        // Setup main layout containers (panels)
        mainPanel.setLayout(new GridBagLayout());
        mappedFieldsContainer = new JPanel(new GridBagLayout());
        inputFieldContainer = new FieldContainer();
        outputFieldContainer = new FieldContainer();

        // Setup layout positions
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        // Add mapped container to main panel
        mainPanel.add(mappedFieldsContainer, c);
        c.weightx = 0.0;
        c.gridx = 0;
        // Add input container to main panel
        mainPanel.add(inputFieldContainer, c);
        c.gridx = 2;
        // Add output container to main panel
        mainPanel.add(outputFieldContainer, c);

        // Add built-in schema entries to the menus
        String[] availSchemes = SchemaHandler.self().getAvailableSchemes();
        for( int i = 0; i < availSchemes.length; i++ ) {
            // Add entry for input formats
            JMenuItem nInputItem = new JMenuItem( availSchemes[i] );
            nInputItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openSchema( MainWindow.FIELD_INPUT, ((JMenuItem) evt.getSource()).getText() );
                }
            });
            this.inputFormatMenu.add(nInputItem);

            // Now the entry for the output format
            JMenuItem nOutputItem = new JMenuItem( availSchemes[i] );
            nOutputItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openSchema( MainWindow.FIELD_OUTPUT, ((JMenuItem) evt.getSource()).getText() );
                }
            });
            this.outputFormatMenu.add(nOutputItem);
        }

    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rightClickPopupMenu = new javax.swing.JPopupMenu();
        addCustomFieldMenuItem = new javax.swing.JMenuItem();
        mfpRCPopupMenu = new javax.swing.JPopupMenu();
        generalManipulationMenuItem = new javax.swing.JMenuItem();
        scrollPane = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        newMappingButton = new javax.swing.JButton();
        loadMappingButton = new javax.swing.JButton();
        saveMappingButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        doMapButton = new javax.swing.JButton();
        doUnmapButton = new javax.swing.JButton();
        manipulationsButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        processMappingButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        formatFilesMenu = new javax.swing.JMenu();
        inputFormatMenu = new javax.swing.JMenu();
        openInputMenuItem = new javax.swing.JMenuItem();
        inputJDBCMenutem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        outputFormatMenu = new javax.swing.JMenu();
        openOutputMenuItem = new javax.swing.JMenuItem();
        outputJDBCMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        saveAsMappingMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        manipulationMenu = new javax.swing.JMenu();
        globalVMMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        marcToMarcXMLMenuItem = new javax.swing.JMenuItem();
        marcToModsMenuItem = new javax.swing.JMenuItem();
        marcToOlefMenuItem = new javax.swing.JMenuItem();
        marcxmlToModsMenuItem = new javax.swing.JMenuItem();
        marcxmlToOlefMenuItem = new javax.swing.JMenuItem();
        modsToOlefMenuItem = new javax.swing.JMenuItem();
        refnumToOlefMenuItem = new javax.swing.JMenuItem();

        addCustomFieldMenuItem.setText("Add custom field");
        addCustomFieldMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCustomFieldMenuItemActionPerformed(evt);
            }
        });
        rightClickPopupMenu.add(addCustomFieldMenuItem);

        generalManipulationMenuItem.setText("General Manipulation");
        generalManipulationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generalManipulationMenuItemActionPerformed(evt);
            }
        });
        mfpRCPopupMenu.add(generalManipulationMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schema Mapping Tool");

        mainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainPanelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1215, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 573, Short.MAX_VALUE)
        );

        scrollPane.setViewportView(mainPanel);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        newMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/page_white.png"))); // NOI18N
        newMappingButton.setText("New Mapping");
        newMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMappingButtonActionPerformed(evt);
            }
        });
        toolBar.add(newMappingButton);

        loadMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/folder_explore.png"))); // NOI18N
        loadMappingButton.setText("Load Mapping");
        loadMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMappingButtonActionPerformed(evt);
            }
        });
        toolBar.add(loadMappingButton);

        saveMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/page_save.png"))); // NOI18N
        saveMappingButton.setText("Save Mapping");
        saveMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMappingButtonActionPerformed(evt);
            }
        });
        toolBar.add(saveMappingButton);
        toolBar.add(jSeparator2);

        doMapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/connect.png"))); // NOI18N
        doMapButton.setText("Map");
        doMapButton.setEnabled(false);
        doMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doMapButtonActionPerformed(evt);
            }
        });
        toolBar.add(doMapButton);

        doUnmapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/disconnect.png"))); // NOI18N
        doUnmapButton.setText("Un-Map");
        doUnmapButton.setEnabled(false);
        doUnmapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doUnmapButtonActionPerformed(evt);
            }
        });
        toolBar.add(doUnmapButton);

        manipulationsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/textfield_rename.png"))); // NOI18N
        manipulationsButton.setText("Value Manipulation");
        manipulationsButton.setEnabled(false);
        manipulationsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manipulationsButtonActionPerformed(evt);
            }
        });
        toolBar.add(manipulationsButton);
        toolBar.add(jSeparator3);

        processMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/cog.png"))); // NOI18N
        processMappingButton.setText("Process Mapping");
        processMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processMappingButtonActionPerformed(evt);
            }
        });
        toolBar.add(processMappingButton);

        fileMenu.setText("File");

        formatFilesMenu.setText("Format Files");

        inputFormatMenu.setText("Input");

        openInputMenuItem.setText("Open...");
        openInputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openInputMenuItemActionPerformed(evt);
            }
        });
        inputFormatMenu.add(openInputMenuItem);

        inputJDBCMenutem.setText("JDBC (ODBC) ...");
        inputJDBCMenutem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputJDBCMenutemActionPerformed(evt);
            }
        });
        inputFormatMenu.add(inputJDBCMenutem);
        inputFormatMenu.add(jSeparator4);

        formatFilesMenu.add(inputFormatMenu);

        outputFormatMenu.setText("Output");

        openOutputMenuItem.setText("Open...");
        openOutputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openOutputMenuItemActionPerformed(evt);
            }
        });
        outputFormatMenu.add(openOutputMenuItem);

        outputJDBCMenuItem.setText("JDBC (ODBC) ...");
        outputJDBCMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputJDBCMenuItemActionPerformed(evt);
            }
        });
        outputFormatMenu.add(outputJDBCMenuItem);
        outputFormatMenu.add(jSeparator5);

        formatFilesMenu.add(outputFormatMenu);

        fileMenu.add(formatFilesMenu);
        fileMenu.add(jSeparator1);

        saveAsMappingMenuItem.setText("Save Mapping As...");
        saveAsMappingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMappingMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMappingMenuItem);
        fileMenu.add(jSeparator6);

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        manipulationMenu.setText("Manipulation");

        globalVMMenuItem.setText("Global Value Manipulation");
        globalVMMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalVMMenuItemActionPerformed(evt);
            }
        });
        manipulationMenu.add(globalVMMenuItem);

        menuBar.add(manipulationMenu);

        toolsMenu.setText("Tools");

        jMenu1.setText("Convert");

        marcToMarcXMLMenuItem.setText("MARC21 to MARCXML");
        marcToMarcXMLMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcToMarcXMLMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(marcToMarcXMLMenuItem);

        marcToModsMenuItem.setText("MARC21 to MODS");
        marcToModsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcToModsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(marcToModsMenuItem);

        marcToOlefMenuItem.setText("MARC21 to OLEF");
        marcToOlefMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcToOlefMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(marcToOlefMenuItem);

        marcxmlToModsMenuItem.setText("MARCXML to MODS");
        marcxmlToModsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcxmlToModsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(marcxmlToModsMenuItem);

        marcxmlToOlefMenuItem.setText("MARCXML to OLEF");
        marcxmlToOlefMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcxmlToOlefMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(marcxmlToOlefMenuItem);

        modsToOlefMenuItem.setText("MODS to OLEF");
        modsToOlefMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modsToOlefMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(modsToOlefMenuItem);

        refnumToOlefMenuItem.setText("refNum to OLEF");
        refnumToOlefMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refnumToOlefMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(refnumToOlefMenuItem);

        toolsMenu.add(jMenu1);

        menuBar.add(toolsMenu);

        setJMenuBar(menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(toolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    /**
     * Map two selected fields together
     * @param evt
     */
    private void doMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doMapButtonActionPerformed
        // Remove selected panels from the original position, if they aren't mapped yet
    /*    Container comp = null;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.gridy = numMappings;
        // Both fields aren't mapped yet, just move them
        if( !selectedInputMFP.getMapped() && !selectedOutputMFP.getMapped() ) {
            // Remove input field
            comp = selectedInputMFP.getParent();
            comp.remove(selectedInputMFP);
            comp.validate();
            // Remove output field
            comp = selectedOutputMFP.getParent();
            comp.remove(selectedOutputMFP);
            comp.validate();

            // Now place the fields on the middle panel
            // Input Field...
            c.gridx = 0;
            c.anchor = GridBagConstraints.NORTHEAST;
            selectedInputMFP.setMapped(true);
            mappedFieldsContainer.add(selectedInputMFP,c);
            // ... Output Field
            c.gridx = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            selectedOutputMFP.setMapped(true);
            mappedFieldsContainer.add(selectedOutputMFP,c);

            // Count new mapping
            numMappings++;
        }
        // Both fields are mapped, we can't do much here - just inform the user
        else if( selectedInputMFP.getMapped() && selectedOutputMFP.getMapped() ) {
            JOptionPane.showMessageDialog(this, "Both fields are already mapped, so it's not possible to re-arrange them in order to provide visual feedback. However the mapping is done in the background and will be used when converting the data!" );
        }
        // One of the fields is already mapped, the other isn't, so move the one that isn't to the already mapped one
        else {
            MappingFieldPanel moveTo = null;
            MappingFieldPanel toMove = null;

            // Check which component we have to move
            if( selectedInputMFP.getMapped() ) {
                moveTo = selectedInputMFP;
                toMove = selectedOutputMFP;
                // Also prepare our constraints
                c.gridx = 1;
                c.anchor = GridBagConstraints.NORTHWEST;
            }
            else {
                moveTo = selectedOutputMFP;
                toMove = selectedInputMFP;
                // Also prepare our constraints
                c.gridx = 0;
                c.anchor = GridBagConstraints.NORTHEAST;
            }

            // Get constraints first and increase gridheight
            int gridypos = ((GridBagLayout) mappedFieldsContainer.getLayout()).getConstraints(moveTo).gridy;
            // Get basic layout & comps info
            GridBagLayout mfcLayout = (GridBagLayout) mappedFieldsContainer.getLayout();
            Component[] mfcComps = mappedFieldsContainer.getComponents();
            // Re-Position all the elements
            for( int i = 0; i < mfcComps.length; i++ ) {
                GridBagConstraints compC = mfcLayout.getConstraints(mfcComps[i]);
                // Check if we have the selected field
                if( mfcComps[i].equals(moveTo) ) {
                    compC.gridheight++;
                }
                // Else check if we have to move the element
                else if( compC.gridy >= gridypos ) {
                    compC.gridy++;
                }

                mfcLayout.setConstraints(mfcComps[i], compC);
            }

            // Finally move the "toMove" field to the "moveTo" field
            c.gridy = gridypos;
            comp = toMove.getParent();
            comp.remove(toMove);
            comp.validate();
            toMove.setMapped(true);
            mappedFieldsContainer.add(toMove,c);

            // Increase mapping count
            numMappings++;
        }*/

        // Add the mapping to the handler
        MappingsHandler.Self().addMapping(selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID());
        // Move the panels
        int status = doMapMove();
        if( status == 2 ) JOptionPane.showMessageDialog(this, "Both fields are already mapped, so it's not possible to re-arrange them in order to provide visual feedback. However the mapping is done in the background and will be used when converting the data!" );

        // Save the mapping into our mappings hash
        /*ArrayList<String> assignedFields = null;
        if( mappings.containsKey(selectedOutputMFP.getFieldID()) ) {
            assignedFields = mappings.get(selectedOutputMFP.getFieldID());
        }
        else {
            assignedFields = new ArrayList<String>(0);
            mappings.put(selectedOutputMFP.getFieldID(), assignedFields);
        }
        assignedFields.add(selectedInputMFP.getFieldID());*/

        // De-Select our fields (NOTE: This also sets both references to null, because of the ItemStateChanged Event)
        selectedInputMFP.setSelectionState(false);
        selectedOutputMFP.setSelectionState(false);

        // Re-Validate our containers (so that the interface is painted correctly)
        mappedFieldsContainer.validate();
        this.validate();
    }//GEN-LAST:event_doMapButtonActionPerformed

    /**
     * Called when the user wants to unmap two fields
     * @param evt
     */
    private void doUnmapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doUnmapButtonActionPerformed
        // First of all remove the mapping between the two fields
        //((ArrayList) mappings.get(selectedOutputMFP.getFieldID())).contains(selectedInputMFP.getFieldID())
        /*ArrayList mappingsList = mappings.get(selectedOutputMFP.getFieldID());
        mappingsList.remove(selectedInputMFP.getFieldID());*/

        MappingsHandler.Self().removeMapping(selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID());

        // Check if there are any mappings left, if not unmark field and move back
        if( !MappingsHandler.Self().isOutputMapped(selectedOutputMFP.getFieldID()) ) {
            selectedOutputMFP.setMapped(false);
            selectedOutputMFP.getParent().remove(selectedOutputMFP);
            selectedOutputMFP.getOrigParent().add(selectedOutputMFP, selectedOutputMFP.getOrigConstraints());
        }

        // Check if input field is mapped somewhere
        /*Iterator mapsIt = mappings.entrySet().iterator();
        boolean bInputStillMapped = false;
        while( mapsIt.hasNext() ) {
            mappingsList = ((Map.Entry<String,ArrayList>) mapsIt.next()).getValue();

            // If we find the field mapped, exit loop
            if( mappingsList.contains(selectedInputMFP.getFieldID())) {
                bInputStillMapped = true;
                break;
            }
        }*/

        // If no mappings are left for the input, unmark and move it back
        if( !MappingsHandler.Self().isInputMapped(selectedInputMFP.getFieldID()) ) {
            selectedInputMFP.setMapped(false);
            selectedInputMFP.getParent().remove(selectedInputMFP);
            selectedInputMFP.getOrigParent().add(selectedInputMFP, selectedInputMFP.getOrigConstraints());
        }

        // De-Select our fields
        selectedInputMFP.setSelectionState(false);
        selectedOutputMFP.setSelectionState(false);

        // Re-Validate our containers (so that the interface is painted correctly)
        mappedFieldsContainer.validate();
        this.validate();
    }//GEN-LAST:event_doUnmapButtonActionPerformed

    private void saveMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMappingButtonActionPerformed
        // Apply mapping file filter
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(mapFilter);

        if( this.currMappingFile != null || fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File file = null;
            if( this.currMappingFile != null ) {
                file = this.currMappingFile;
            }
            else {
                file = fc.getSelectedFile();
            }

            // Check if file exists and confirm it
            if( this.currMappingFile == null && file.exists() ) {
                if( JOptionPane.showConfirmDialog(this, "File already exists - overwrite?", "Overwrite File", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION ) return;
            }

            // Write mappings HashMap to file
            try {
                MapFileHandler.self().setMappings(MappingsHandler.Self().getMappings());
                MapFileHandler.self().setManipulations(ManipulationsHandler.self().getManipulations());
                MapFileHandler.self().setInputFields(inputFields);
                MapFileHandler.self().setOutputFields(outputFields);

                MapFileHandler.self().saveFile(file);

                /*FileOutputStream fos = new FileOutputStream( file );
                ObjectOutputStream oos = new ObjectOutputStream( fos );

                oos.writeObject(MappingsHandler.Self().getMappings());
                oos.writeObject(ManipulationsHandler.self().getManipulations());
                oos.writeObject(inputFields);
                oos.writeObject(outputFields);

                oos.close();
                fos.close();*/

                this.setTitle(this.defaultTitle + " - " + file.getName());
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_saveMappingButtonActionPerformed

    private void loadMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMappingButtonActionPerformed
        if( MappingsHandler.Self().getMappingCount() > 0 && JOptionPane.showConfirmDialog(this, "Loading a mappings file clears all current mappings Unsaved changes will be lost. Are you sure?", "Clear Mappings", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION ) return;

        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(mapFilter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();

            // Loads mappings from a given file
            try {
                // Clear all existing mappings
                inputFieldContainer.removeAll();
                outputFieldContainer.removeAll();
                mappedFieldsContainer.removeAll();
                inputFields.clear();
                outputFields.clear();

                // Load & parse the mappings file
                MapFileHandler.self().loadFile( file );

                // Get mappings & fields from file handler
                HashMap<String,HashMap<String,MappingRecord>> tempMappings = MapFileHandler.self().getMappings();
                HashMap<String,ArrayList<ManipulationRecord>> tempManipulations = MapFileHandler.self().getManipulations();
                this.handleFields(MainWindow.FIELD_INPUT, MapFileHandler.self().getInputFields());
                this.handleFields(MainWindow.FIELD_OUTPUT, MapFileHandler.self().getOutputFields());

                // Clear Mappings-Handler
                MappingsHandler.Self().clear();
                // Clear Manipulations-Handler
                ManipulationsHandler.self().clear();

                // Re-Assign all manipulations
                ManipulationsHandler.self().setManipulations(tempManipulations);

                // Re-Run all mappings
                Iterator<Map.Entry<String,HashMap<String,MappingRecord>>> mapsIt = tempMappings.entrySet().iterator();
                while( mapsIt.hasNext() ) {
                    Map.Entry<String,HashMap<String,MappingRecord>> entry = (Map.Entry<String,HashMap<String,MappingRecord>>) mapsIt.next();
                    HashMap<String,MappingRecord> mappingsList = entry.getValue();
                    String outputFieldID = entry.getKey();
                    //MappingFieldPanel currOutputMFP = null;

                    // Find output field (the panel)
                    this.selectedOutputMFP = outputFieldContainer.getFieldPanelByID(outputFieldID);
                    //this.selectedOutputMFP = outputFieldContainer.getFieldPanelByID(outputFieldID);

                    Iterator<String> mplIt = mappingsList.keySet().iterator();
                    while( mplIt.hasNext() ) {
                        String inputFieldID = mplIt.next();

                        // Find input field (the panel)
                        this.selectedInputMFP = inputFieldContainer.getFieldPanelByID(inputFieldID);

                        // Add mapping again
                        MappingsHandler.Self().addMapping(selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID());
                        // Set options of mapping
                        //MappingsHandler.self().manipulateMapping(selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID(), mappingsList.get(selectedInputMFP.getFieldID()).existsAction);
                        MappingRecord mr = MappingsHandler.Self().getMapping( selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID() );
                        mr.existsAction = mappingsList.get(selectedInputMFP.getFieldID()).existsAction;
                        mr.persistentMapping = mappingsList.get(selectedInputMFP.getFieldID()).persistentMapping;
                        MappingsHandler.Self().setMapping( selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID(), mr);

                        // Finally move the fields
                        this.doMapMove();
                    }
                }

                // Unset the selected input fields
                this.selectedInputMFP = null;
                this.selectedOutputMFP = null;

                // Save current file-name
                this.currMappingFile = file;
                this.setTitle( this.defaultTitle + " - " + this.currMappingFile.getName() );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_loadMappingButtonActionPerformed

    private void processMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processMappingButtonActionPerformed
        // Check if we have mappings at all
        if( MappingsHandler.Self().getMappingCount() <= 0 ) {
            JOptionPane.showMessageDialog(this, "Please create at least one mapping before processing it.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //pmw.setMappings(MappingsHandler.self().getMappings());
        pmw.setVisible(true);
    }//GEN-LAST:event_processMappingButtonActionPerformed

    private void manipulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manipulationsButtonActionPerformed
        mw.display( this.selectedInputMFP.getFieldID(), this.selectedOutputMFP.getFieldID() );
        //mw.setVisible(true);
    }//GEN-LAST:event_manipulationsButtonActionPerformed

    private void newMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMappingButtonActionPerformed
        // Remove any mappings / manipulations
        MappingsHandler.Self().clear();
        ManipulationsHandler.self().clear();

        // Clear all existing mappings
        inputFieldContainer.removeAll();
        outputFieldContainer.removeAll();
        mappedFieldsContainer.removeAll();
        inputFields.clear();
        outputFields.clear();

        // Remove any selections
        this.selectedInputMFP = null;
        this.selectedOutputMFP = null;

        // Remove title file
        this.currMappingFile = null;
        this.setTitle( this.defaultTitle );

        this.validate();
    }//GEN-LAST:event_newMappingButtonActionPerformed

    private void globalVMMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalVMMenuItemActionPerformed
        this.mw.display( "*" , "*" );
    }//GEN-LAST:event_globalVMMenuItemActionPerformed

    private void openInputMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openInputMenuItemActionPerformed
        this.openMenuItemActionPerformed(evt);
    }//GEN-LAST:event_openInputMenuItemActionPerformed

    private void openOutputMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openOutputMenuItemActionPerformed
        this.openMenuItemActionPerformed(evt);
    }//GEN-LAST:event_openOutputMenuItemActionPerformed

    // TODO: Create a better interface for communication between JDBC window and mainwindow
    private void inputJDBCMenutemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputJDBCMenutemActionPerformed
        this.jdcbw.display(this, MainWindow.FIELD_INPUT);
    }//GEN-LAST:event_inputJDBCMenutemActionPerformed

    private void outputJDBCMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputJDBCMenuItemActionPerformed
        this.jdcbw.display(this, MainWindow.FIELD_OUTPUT);
    }//GEN-LAST:event_outputJDBCMenuItemActionPerformed

    private void mainPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainPanelMouseClicked
        // Display a popup menu when the user right-clicks on our main panel
        if( evt.getButton() == MouseEvent.BUTTON3 ) {
            this.rightClickPopupMenu.show(this.mainPanel, evt.getX(), evt.getY());
        }
        else {
            this.rightClickPopupMenu.setVisible(false);
        }
    }//GEN-LAST:event_mainPanelMouseClicked

    private void addCustomFieldMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomFieldMenuItemActionPerformed
        // TODO Replace with something more sofisticated
        this.afw.display(this);
    }//GEN-LAST:event_addCustomFieldMenuItemActionPerformed

    private void generalManipulationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalManipulationMenuItemActionPerformed
        String fieldID = ((MappingFieldPanel) this.mfpRCPopupMenu.getInvoker()).getFieldID();

        if( this.inputFieldContainer.containsFieldPanel((MappingFieldPanel) this.mfpRCPopupMenu.getInvoker()) ) {
            this.mw.display(fieldID, "*");
        }
        else {
            this.mw.display("*", fieldID);
        }
    }//GEN-LAST:event_generalManipulationMenuItemActionPerformed

    private void saveAsMappingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMappingMenuItemActionPerformed
        // Remember old mapping file and call save as function
        File tmpCMF = this.currMappingFile;
        this.currMappingFile = null;
        this.saveMappingButtonActionPerformed(evt);
        // Restore old name if no file was chosen
        if( this.currMappingFile == null ) this.currMappingFile = tmpCMF;
    }//GEN-LAST:event_saveAsMappingMenuItemActionPerformed

    private void marcToMarcXMLMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcToMarcXMLMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.iso2709Filter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();
            String outputFileName = inputFile.getPath();
            outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_marcxml.xml";
            File outputFile = new File( outputFileName );

            try {
                Charset uft8Charset = Charset.forName("UTF-8");
                MARC21Converter.convertToMARCXML(inputFile, outputFile, uft8Charset, uft8Charset );

                JOptionPane.showMessageDialog(this, "Successfully converted to MARCXML. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(this, "Error while converting the MARC21 File to MARCXML: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_marcToMarcXMLMenuItemActionPerformed

    private void marcxmlToModsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcxmlToModsMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.xmlFilter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();
            String outputFileName = inputFile.getPath();
            outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_mods.xml";
            File outputFile = new File( outputFileName );

            // Now load the XSL from the internal resources
            //InputStream xslFile = getClass().getResourceAsStream( "converter/resources/MARC21slim2MODS3-4.xsl" );

            try {
                //XSLTransformer.transform(inputFile, outputFile, xslFile);
                MARCXMLConverter.convertToMODS(inputFile, outputFile);


                JOptionPane.showMessageDialog(this, "Successfully converted to MODS. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the MARCXML File to MODS: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_marcxmlToModsMenuItemActionPerformed

    private void marcToModsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcToModsMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.iso2709Filter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();

            try {
                ///File tempFile = File.createTempFile( "smt_conversion_marcMods", ".tmp" );

                String outputFileName = inputFile.getPath();
                outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_mods.xml";
                File outputFile = new File( outputFileName );

                /*MARC21Converter.convertToMARCXML(inputFile, tempFile);

                // Now load the XSL from the internal resources
                InputStream xslFile = getClass().getResourceAsStream( "converter/resources/MARC21slim2MODS3-4.xsl" );
                XSLTransformer.transform(tempFile, outputFile, xslFile);*/
                
                Charset uft8Charset = Charset.forName("UTF-8");
                MARC21Converter.convertToMODS(inputFile, outputFile, uft8Charset, uft8Charset);

                JOptionPane.showMessageDialog(this, "Successfully converted to MODS. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the MARC21 File to MODS: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_marcToModsMenuItemActionPerformed

    private void marcToOlefMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcToOlefMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.iso2709Filter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();

            try {
                String outputFileName = inputFile.getPath();
                outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_olef.xml";
                File outputFile = new File( outputFileName );

                Charset uft8Charset = Charset.forName("UTF-8");
                MARC21Converter.convertToOLEF(inputFile, outputFile, uft8Charset, uft8Charset);

                JOptionPane.showMessageDialog(this, "Successfully converted to OLEF. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the MARC21 File to OLEF: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_marcToOlefMenuItemActionPerformed

    private void marcxmlToOlefMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcxmlToOlefMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.xmlFilter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();

            try {
                String outputFileName = inputFile.getPath();
                outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_olef.xml";
                File outputFile = new File( outputFileName );

                MARCXMLConverter.convertToOLEF(inputFile, outputFile);

                JOptionPane.showMessageDialog(this, "Successfully converted to OLEF. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the MARCXML File to OLEF: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_marcxmlToOlefMenuItemActionPerformed

    private void modsToOlefMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modsToOlefMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.xmlFilter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();

            try {
                String outputFileName = inputFile.getPath();
                outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_olef.xml";
                File outputFile = new File( outputFileName );

                MODSConverter.convertToOLEF(inputFile, outputFile);

                JOptionPane.showMessageDialog(this, "Successfully converted to OLEF. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the MODS File to OLEF: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_modsToOlefMenuItemActionPerformed

    private void refnumToOlefMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refnumToOlefMenuItemActionPerformed
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(this.xmlFilter);

        if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            File inputFile = fc.getSelectedFile();

            try {
                String outputFileName = inputFile.getPath();
                outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + "_olef.xml";
                File outputFile = new File( outputFileName );

                REFNUMConverter.convertToOLEF(inputFile, outputFile);

                JOptionPane.showMessageDialog(this, "Successfully converted to OLEF. Output File: " + outputFile.getPath() );
            }
            catch( Exception e ) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, "Error while converting the refNum File to OLEF: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_refnumToOlefMenuItemActionPerformed

    public void openSchema( int fieldType, String schemaName ) {
        LinkedHashMap<String,LinkedHashMap> fields = SchemaHandler.self().getSchemaFields( schemaName );

        this.handleFields(fieldType, fields);
    }

    /**
     * Move Panels for mapping (NOTE: Don't forget to re-validate the container, the function doesn't take care of it)
     * @return int 1 if no field was mapped, 2 if both fields are already mapped, 3 if one of the fields wasn't mapped
     */
    private int doMapMove() {
        // Remove selected panels from the original position, if they aren't mapped yet
        Container comp = null;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        //c.gridy = MappingsHandler.self().getMappingCount();
        c.gridy = this.currGridY++;
        // Both fields aren't mapped yet, just move them
        if( !selectedInputMFP.getMapped() && !selectedOutputMFP.getMapped() ) {
            // Remove input field
            comp = selectedInputMFP.getParent();
            comp.remove(selectedInputMFP);
            comp.validate();
            // Remove output field
            comp = selectedOutputMFP.getParent();
            comp.remove(selectedOutputMFP);
            comp.validate();

            // Now place the fields on the middle panel
            // Input Field...
            c.gridx = 0;
            c.anchor = GridBagConstraints.NORTHEAST;
            selectedInputMFP.setMapped(true);
            mappedFieldsContainer.add(selectedInputMFP,c);
            // ... Output Field
            c.gridx = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            selectedOutputMFP.setMapped(true);
            mappedFieldsContainer.add(selectedOutputMFP,c);

            return 1;
        }
        // Both fields are mapped, we can't do much here - just inform the user
        else if( selectedInputMFP.getMapped() && selectedOutputMFP.getMapped() ) {
            //JOptionPane.showMessageDialog(this, "Both fields are already mapped, so it's not possible to re-arrange them in order to provide visual feedback. However the mapping is done in the background and will be used when converting the data!" );
            return 2;
        }
        // One of the fields is already mapped, the other isn't, so move the one that isn't to the already mapped one
        else {
            MappingFieldPanel moveTo = null;
            MappingFieldPanel toMove = null;

            // Check which component we have to move
            if( selectedInputMFP.getMapped() ) {
                moveTo = selectedInputMFP;
                toMove = selectedOutputMFP;
                // Also prepare our constraints
                c.gridx = 1;
                c.anchor = GridBagConstraints.NORTHWEST;
            }
            else {
                moveTo = selectedOutputMFP;
                toMove = selectedInputMFP;
                // Also prepare our constraints
                c.gridx = 0;
                c.anchor = GridBagConstraints.NORTHEAST;
            }

            // Get constraints first and increase gridheight
            int gridypos = ((GridBagLayout) mappedFieldsContainer.getLayout()).getConstraints(moveTo).gridy;
            // Get basic layout & comps info
            GridBagLayout mfcLayout = (GridBagLayout) mappedFieldsContainer.getLayout();
            Component[] mfcComps = mappedFieldsContainer.getComponents();
            // Re-Position all the elements
            for( int i = 0; i < mfcComps.length; i++ ) {
                GridBagConstraints compC = mfcLayout.getConstraints(mfcComps[i]);
                // Check if we have the selected field
                if( mfcComps[i].equals(moveTo) ) {
                    compC.gridheight++;
                }
                // Else check if we have to move the element
                else if( compC.gridy >= gridypos ) {
                    compC.gridy++;
                }

                mfcLayout.setConstraints(mfcComps[i], compC);
            }

            // Finally move the "toMove" field to the "moveTo" field
            c.gridy = gridypos;
            comp = toMove.getParent();
            comp.remove(toMove);
            comp.validate();
            toMove.setMapped(true);
            mappedFieldsContainer.add(toMove,c);

            return 3;
        }
    }

    /**
     * Called by the menu-handlers when an "open"-menu item gets selected
     *
     * @param evt Event from the actionperformed handlers
     */
    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        String fcTitle = "";
        int fieldType = 0;

        // Check where the call comes from
        // ... INPUT
        if( evt.getSource().equals(openInputMenuItem) ) {
            fcTitle = "Select Input Format File";
            fieldType = MainWindow.FIELD_INPUT;
        }
        // ... OUTPUT
        else {
            fcTitle = "Select Output Format File";
            fieldType = MainWindow.FIELD_OUTPUT;
        }

        // TODO: Add filter for input / output formats
        fc.resetChoosableFileFilters();

        // Open file dialog and wait for input
        if (fc.showDialog(this, fcTitle) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            // Now start the input conversion / reading
            LinkedHashMap<String,LinkedHashMap> fields = icw.convertFile(file);

            // Check if we got something
            if( fields != null ) {
                this.handleFields(fieldType, fields);
            }
        }
    }

    /**
     * Handle (add) a given list of fields to the interface
     * @param fieldType Type of fields (input or output)
     * @param fields List of fields to add
     */
    public void handleFields( int fieldType, LinkedHashMap<String,LinkedHashMap> fields ) {
        LinkedHashMap<String,LinkedHashMap> currFields = null;
        FieldContainer currContainer = null;
        GridBagConstraints c = new GridBagConstraints();

        // Basic layout constraints
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,5,5);
        c.gridx = 0;
        //c.weightx = 1.0;

        // Check where the call comes from
        // ... INPUT
        if( fieldType == MainWindow.FIELD_INPUT ) {
//            currPanels = inputFieldPanels;
            currFields = inputFields;
            currContainer = inputFieldContainer;
            c.anchor = GridBagConstraints.NORTHWEST;
        }
        // ... OUTPUT
        else {
//            currPanels = outputFieldPanels;
            currFields = outputFields;
            currContainer = outputFieldContainer;
            c.anchor = GridBagConstraints.NORTHEAST;
        }

        Iterator<Map.Entry<String,LinkedHashMap>> it = fields.entrySet().iterator();
        int index = 0;

        // Remove old mapping-Fields
        currContainer.removeAll();

        // Prepare fields-list
        currFields.clear();
        currFields.putAll(fields);

        // Add all panels
        while( it.hasNext() ) {
            Map.Entry<String,LinkedHashMap> me = it.next();

            // Create new mapping-field panel for input
            MappingFieldPanel newPanel = new MappingFieldPanel( (String) me.getKey(), (HashMap) me.getValue() );
            newPanel.addItemListener(this);
            newPanel.addMouseListener(new MouseAdapter() {
               public void mouseClicked(MouseEvent evt) {
                   mfpMouseClicked(evt);
               }
            });

            // Setup layout of new panel
            newPanel.setVisible(true);
            c.gridy = index;

            // Add panel to the list & interface
            currContainer.add(newPanel,c);
            newPanel.setOrigPositionInfo(c, currContainer);
            index++;
        }
        // Validate the window (and thus repaint it)
        currContainer.doLayout();
        this.validate();
    }

    //TODO Replace with something senseful
    public void addField( String fieldID, String fieldName, int fieldType ) {
        LinkedHashMap fieldInfo = new LinkedHashMap();

        fieldInfo.put("name", fieldName);
        fieldInfo.put("subfields", null);

        GridBagConstraints c = new GridBagConstraints();

        // Basic layout constraints
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,5,5);
        c.gridx = 0;

        MappingFieldPanel newPanel = new MappingFieldPanel(fieldID, fieldInfo);
        newPanel.addItemListener(this);
        newPanel.setVisible(true);

        switch( fieldType ) {
            case FIELD_OUTPUT:
                outputFields.put(fieldID, fieldInfo);
                c.anchor = GridBagConstraints.NORTHEAST;
                c.gridy = outputFields.size();

                newPanel.setOrigPositionInfo(c, outputFieldContainer);
                outputFieldContainer.add(newPanel, c);
                outputFieldContainer.doLayout();
                break;
                
            default:
                inputFields.put(fieldID, fieldInfo);
                c.anchor = GridBagConstraints.NORTHEAST;
                c.gridy = inputFields.size();

                newPanel.setOrigPositionInfo(c, inputFieldContainer);
                inputFieldContainer.add(newPanel, c);
                inputFieldContainer.doLayout();
                break;
        }

        this.validate();
  }

    /**
     * Implements ItemListener
     * @param e ItemEvent
     */
    public void itemStateChanged( ItemEvent e ) {
        MappingFieldPanel source = (MappingFieldPanel) e.getSource();

        // Check if we have an input or an output field
/*        boolean bInputFound = inputFieldPanels.contains(source);
        for( int i = 0; i < inputFieldPanels.size(); i++ ) bInputFound |= ((MappingFieldPanel)inputFieldPanels.get(i)).hasSubField(source);*/
        boolean bInputFound = inputFieldContainer.containsFieldPanel(source);

        ArrayList<String> mappedFieldIDs = null;
        FieldContainer container = null;

        // Field got selected
        if( e.getStateChange() == 1 ) {
            // ... INPUT
            if( bInputFound ) {
                if( selectedInputMFP != null ) selectedInputMFP.setSelectionState(false);
                selectedInputMFP = source;

                mappedFieldIDs = MappingsHandler.Self().getMappingsFrom(source.getFieldID());
                container = outputFieldContainer;
            }
            // ... OUTPUT
            else {
                if( selectedOutputMFP != null ) selectedOutputMFP.setSelectionState(false);
                selectedOutputMFP = source;

                mappedFieldIDs = MappingsHandler.Self().getMappingsTo(source.getFieldID());
                container = inputFieldContainer;
            }
        }
        else {
            if( bInputFound ) {
                selectedInputMFP = null;
                mappedFieldIDs = MappingsHandler.Self().getMappingsFrom(source.getFieldID());
                container = outputFieldContainer;
            }
            else {
                selectedOutputMFP = null;
                mappedFieldIDs = MappingsHandler.Self().getMappingsTo(source.getFieldID());
                container = inputFieldContainer;
            }
        }

        if( mappedFieldIDs != null ) {
            Iterator<String> mfiIt = mappedFieldIDs.iterator();
            while( mfiIt.hasNext() ) {
                String mappedID = mfiIt.next();

                container.getFieldPanelByID(mappedID).setHighlight(e.getStateChange() == 1);
            }
        }

        // Check if two fields are selected, and activate buttons as required
        if( selectedInputMFP != null && selectedOutputMFP != null ) {
            if( selectedInputMFP.getMapped() && selectedOutputMFP.getMapped() ) {
                // Check if both fields are already connected
                if( MappingsHandler.Self().isMapped(selectedInputMFP.getFieldID(),selectedOutputMFP.getFieldID()) ) {
                //if( mappings.get(selectedOutputMFP.getFieldID()).contains(selectedInputMFP.getFieldID()) ) {
                    doUnmapButton.setEnabled(true);
                    manipulationsButton.setEnabled(true);
                }
                else {
                    doMapButton.setEnabled(true);
                }
            }
            else {
                doMapButton.setEnabled(true);
            }
        }
        else {
            doMapButton.setEnabled(false);
            doUnmapButton.setEnabled(false);
            manipulationsButton.setEnabled(false);
        }
    }

    /**
     * Called when on a MappingFieldPanel the mouse gets clicked
     * @param evt
     */
    private void mfpMouseClicked( MouseEvent evt ) {
        if( evt.getButton() == MouseEvent.BUTTON3 ) {
            /*String sourceFieldID = ((MappingFieldPanel) evt.getSource()).getFieldID();

            // Check if we have an input or an output field
            // Display the manipulations window then
            if( this.inputFieldContainer.getFieldPanelByID(sourceFieldID) != null ) {
                this.mw.display(sourceFieldID, "*");
            }
            else {
                this.mw.display("*", sourceFieldID);
            }*/

            // NOTE: the MouseEvent comes from the MappingFieldPanel, so we have to invoke the show function with it as source
            this.mfpRCPopupMenu.show((MappingFieldPanel) evt.getSource(), evt.getX(), evt.getY());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addCustomFieldMenuItem;
    private javax.swing.JButton doMapButton;
    private javax.swing.JButton doUnmapButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu formatFilesMenu;
    private javax.swing.JMenuItem generalManipulationMenuItem;
    private javax.swing.JMenuItem globalVMMenuItem;
    private javax.swing.JMenu inputFormatMenu;
    private javax.swing.JMenuItem inputJDBCMenutem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JButton loadMappingButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu manipulationMenu;
    private javax.swing.JButton manipulationsButton;
    private javax.swing.JMenuItem marcToMarcXMLMenuItem;
    private javax.swing.JMenuItem marcToModsMenuItem;
    private javax.swing.JMenuItem marcToOlefMenuItem;
    private javax.swing.JMenuItem marcxmlToModsMenuItem;
    private javax.swing.JMenuItem marcxmlToOlefMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPopupMenu mfpRCPopupMenu;
    private javax.swing.JMenuItem modsToOlefMenuItem;
    private javax.swing.JButton newMappingButton;
    private javax.swing.JMenuItem openInputMenuItem;
    private javax.swing.JMenuItem openOutputMenuItem;
    private javax.swing.JMenu outputFormatMenu;
    private javax.swing.JMenuItem outputJDBCMenuItem;
    private javax.swing.JButton processMappingButton;
    private javax.swing.JMenuItem refnumToOlefMenuItem;
    private javax.swing.JPopupMenu rightClickPopupMenu;
    private javax.swing.JMenuItem saveAsMappingMenuItem;
    private javax.swing.JButton saveMappingButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables

}
