/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool;

import at.nhmwien.schema_mapping_tool.fileProcessors.*;

/**
 *
 * @author wkoller
 */
public abstract class DataSourceOptionsPanel extends javax.swing.JPanel {
    protected String sourceName = "";
    protected boolean bInput = false;
    protected FileProcessor processor = null;

    public abstract String getFileName();
    public abstract void saveOptions();
    public abstract void loadOptions();
    
    public void setSourceName( String nSourceName ) {
        this.sourceName = nSourceName;

        this.processor = ProcessorHandler.self().getProcessorForType(sourceName);
    }

    /**
     * Get the processor for this option panel
     * @return FileProcessor
     */
    public FileProcessor getProcessor() {
        this.saveOptions();

        return this.processor;
    }
    
    public void setType( boolean nbInput ) {
        this.bInput = nbInput;
    }
}
