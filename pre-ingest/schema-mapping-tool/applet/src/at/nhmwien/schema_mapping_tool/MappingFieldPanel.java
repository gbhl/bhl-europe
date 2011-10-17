package at.nhmwien.schema_mapping_tool;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/*
 * mappingFieldPanel.java
 *
 * Created on 11.09.2009, 09:10:52
 */

/**
 * @author wkoller
 */
public class MappingFieldPanel extends javax.swing.JPanel implements ItemSelectable, ItemListener {
    ItemListener listeners = null;
    ArrayList<MappingFieldPanel> subFieldPanels = null;
    Border origBorder = null, highlightBorder = null, selectedBorder = null, mappedBorder = null;
    //Border highlightBorder = null;
    boolean bIsMapped = false;
    GridBagConstraints origConstraints = null;
    Container origParent = null;
    String fieldIDValue = null;

    private boolean bIsSelected = false;
    private boolean bIsHightlighted = false;

    /** Creates new form mappingFieldPanel */
    public MappingFieldPanel( String fieldID, HashMap fieldInfo ) {
        initComponents();
        subFieldPanels = new ArrayList<MappingFieldPanel>(0);
        origBorder = this.getBorder();

        // Create our different border
        this.highlightBorder = new LineBorder( new Color( 0, 0, 255 ), 1 );
        this.selectedBorder = new LineBorder( new Color( 255, 0, 0 ), 1 );
        this.mappedBorder = new LineBorder( new Color( 0, 255, 0 ), 1 );

        // Hide the subfields-panel by default
        subFieldsPanel.setVisible(false);

        // Add name and ID to labels
        fieldIDValue = fieldID;
        fieldNameValue.setText((String) fieldInfo.get("name"));
        fieldNameValue.setToolTipText(fieldID);

        // Add any subfields
        int index = 0;
        subFieldsPanel.setLayout(new GridBagLayout());
        HashMap subfields = (HashMap) fieldInfo.get("subfields");
        if( subfields != null ) {
            // Enable expand button for sub-fields
            expandButton.setEnabled(true);

            Iterator fi = subfields.entrySet().iterator();
            while( fi.hasNext() ) {
                // Get next entry
                Map.Entry me = (Map.Entry) fi.next();
                // Create panel
                MappingFieldPanel newPanel = new MappingFieldPanel( (String) me.getKey(), (HashMap) me.getValue() );
                // Add to notification chain
                newPanel.addItemListener(this);
                newPanel.addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent evt ) {
                        // Pass click event on this sub-panel to upper level
                        dispatchEvent( evt );
                    }
                });
                /*newPanel.addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent evt ) {
                        //((MappingFieldPanel) evt.getSource()).formMouseClicked(evt);
                        dispatchEvent(evt);
                        MouseListener[] listeners = getMouseListeners();
                        for( int i = 0; i < listeners.length; i++ ) {
                            listeners[i].mouseClicked(evt);
                        }
                    }
                });*/

                // Prepare layout
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = index;
                c.gridx = 0;
                c.insets = new Insets(2,2,2,2);
                c.anchor = GridBagConstraints.NORTHWEST;

                // Add panels to the list & interface
                subFieldsPanel.add(newPanel,c);
                newPanel.setOrigPositionInfo(c, subFieldsPanel);
                newPanel.setVisible(true);

                // Add to our sub-field list
                subFieldPanels.add(newPanel);

                index++;
            }
        }
        else {
            // We do not have any subfields, so disable the expand button
            expandButton.setEnabled(false);
        }
    }

    /**
     * Check if this panel (or any sub-field) contains a given subField
     * @param subField Sub-Field to search for
     * @return boolean true if Sub-Field is found, false if not
     */
    public boolean hasSubField( MappingFieldPanel subField ) {
        boolean bFound = subFieldPanels.contains(subField);
        for( int i = 0; i < subFieldPanels.size(); i++ ) bFound |= ((MappingFieldPanel) subFieldPanels.get(i)).hasSubField(subField);

        return bFound;
    }

    /**
     * Find a field by a given Id and return the panel for it
     * @param fieldID ID of field to search for
     * @return mappingFieldPanel The field-panel, null if not found
     */
    public MappingFieldPanel getFieldPanelByID( String fieldID ) {
        MappingFieldPanel targetPanel = null;

        // Check if this panel is the target panel
        if( this.getFieldID().compareTo(fieldID) == 0 ) {
            targetPanel = this;
        }
        // If not, search all sub-fields
        else {
            for( int i = 0; i < subFieldPanels.size(); i++ ) {
                // check if sub-field is the target panel
                if( subFieldPanels.get(i).getFieldID().compareTo(fieldID) == 0 ) {
                    targetPanel = subFieldPanels.get(i);

                    break;
                }
                // if not, call search function
                else {
                    targetPanel = subFieldPanels.get(i).getFieldPanelByID(fieldID);

                    if( targetPanel != null ) break;
                }
            }
        }

        return targetPanel;
    }

    /** 
     * Reference implementations for ItemSelectable
    */
    public Object[] getSelectedObjects() {
        return new Object[1];
    }

    /**
    * Adds one listener that wil be informed on all user-base changes in the selection.
    */
    public void addItemListener( ItemListener il ) {
        listeners = AWTEventMulticaster.add( listeners, il );
    }

    /**
    * Implements ItemSelectable.
    */
    public void removeItemListener( ItemListener il ) {
        listeners = AWTEventMulticaster.remove( listeners, il );
    }

    /**
     * Implements ItemListener
     */
    public void itemStateChanged( ItemEvent e ) {
        if( listeners != null ) listeners.itemStateChanged(e);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subFieldsPanel = new javax.swing.JPanel();
        fieldPanel = new javax.swing.JPanel();
        expandButton = new javax.swing.JButton();
        fieldNameValue = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout subFieldsPanelLayout = new javax.swing.GroupLayout(subFieldsPanel);
        subFieldsPanel.setLayout(subFieldsPanelLayout);
        subFieldsPanelLayout.setHorizontalGroup(
            subFieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        subFieldsPanelLayout.setVerticalGroup(
            subFieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        expandButton.setText("+");
        expandButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
        expandButton.setMaximumSize(new java.awt.Dimension(20, 20));
        expandButton.setMinimumSize(new java.awt.Dimension(20, 20));
        expandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandButtonActionPerformed(evt);
            }
        });

        fieldNameValue.setText("FieldName");
        fieldNameValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldNameValueMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout fieldPanelLayout = new javax.swing.GroupLayout(fieldPanel);
        fieldPanel.setLayout(fieldPanelLayout);
        fieldPanelLayout.setHorizontalGroup(
            fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(expandButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldNameValue)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fieldPanelLayout.setVerticalGroup(
            fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expandButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNameValue, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(subFieldsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(fieldPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subFieldsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        //ItemEvent e = null;

        if (evt.getSource() == this) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if( this.getSelectionState() ) {
                    this.setSelectionState(false);
                }
                else {
                    this.setSelectionState(true);
                }

                //e = new ItemEvent( this, 10001, this, ItemEvent.SELECTED );
                //this.setSelectionState(true);
            } /*else if( evt.getButton() == MouseEvent.BUTTON3 ) {
            this.rightClickPopupMenu.show(this, evt.getX(), evt.getY());

            this.setSelectionState(false);
            } else {
                //e = new ItemEvent( this, 10001, this, ItemEvent.DESELECTED );
                this.setSelectionState(false);
            }*/
        }

//        this.getParent().dispatchEvent(evt);

        // Fire our event
        //this.itemStateChanged(e);
    }//GEN-LAST:event_formMouseClicked

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if( expandButton.getText().compareTo("+") == 0 ) {
            subFieldsPanel.setVisible(true);
            expandButton.setText("-");
        }
        else {
            subFieldsPanel.setVisible(false);
            expandButton.setText("+");
        }
    }//GEN-LAST:event_expandButtonActionPerformed

    private void fieldNameValueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fieldNameValueMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_fieldNameValueMouseClicked

    /**
     * Change the selection state of this panel
     * @param bSelected true == selected, false == deselected
     */
    public void setSelectionState( boolean bSelected ) {
        ItemEvent e = null;
        Border lb = null;

        this.bIsSelected = bSelected;

        // If the panel gets selected, fire event and highlight border
        if( bSelected ) {
            e = new ItemEvent( this, 10001, this, ItemEvent.SELECTED );
            //lb = new LineBorder( new Color( 255, 0, 0), 1 );
            lb = this.selectedBorder;
        }
        else {
            e = new ItemEvent( this, 10001, this, ItemEvent.DESELECTED );
            //lb = origBorder;
            if( this.bIsHightlighted ) {
                lb = this.highlightBorder;
            }
            else if( this.bIsMapped ) {
                lb = this.mappedBorder;
            }
            else {
                lb = this.origBorder;
            }
        }

        // Setup new border
        this.setBorder(lb);
        // Fire our event
        this.itemStateChanged(e);
    }

    public void setHighlight( boolean bHighlight ) {
        this.bIsHightlighted = bHighlight;

        // No border change if this is field is selected too
        if( this.bIsSelected ) return;

        if( bHighlight ) {
            this.setBorder( this.highlightBorder );
        }
        else {
            if( this.bIsMapped ) {
                this.setBorder( this.mappedBorder );
            }
            else {
                this.setBorder( this.origBorder );
            }

            //this.setBorder( this.origBorder );
        }
    }

    public boolean getSelectionState() {
        return this.bIsSelected;
    }

    /**
     * Mark this field as mapped, also set the border to green
     */
    public void setMapped(boolean bMapped) {
        this.bIsMapped = bMapped;

        if( this.bIsMapped ) {
            this.setBorder( this.mappedBorder );
            //origBorder = new LineBorder( new Color( 0, 255, 0 ), 1 );
        }
        else {
            this.setBorder( this.origBorder );
            this.bIsHightlighted = false;
            this.bIsSelected = false;

            //origBorder = new LineBorder( new Color( 0, 0, 0 ), 1 );
        }

        //this.setBorder(origBorder);
    }

    /**
     * Returns true if the field is mapped, false otherwise
     * @return boolean
     */
    public boolean getMapped() {
        return this.bIsMapped;
    }

    /**
     * Save the original grid-bag constraints (required for unmapping and moving back to original position)
     * @param gbc Original GridBagConstraints
     */
    public void setOrigPositionInfo( GridBagConstraints gbc, Container parent ) {
        this.origConstraints = (GridBagConstraints) gbc.clone();
        this.origParent = parent;
    }

    public GridBagConstraints getOrigConstraints() {
        return this.origConstraints;
    }

    public Container getOrigParent() {
        return this.origParent;
    }

    /**
     * Returns the ID of this field
     * @return String ID of field
     */
    public String getFieldID() {
        return fieldIDValue;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expandButton;
    private javax.swing.JLabel fieldNameValue;
    private javax.swing.JPanel fieldPanel;
    private javax.swing.JPanel subFieldsPanel;
    // End of variables declaration//GEN-END:variables

}
