/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.mappingProcess;
import java.io.*;

/**
 *
 * @author wkoller
 */
public class MappingRecord implements Serializable {
    // We need a fixed serialVersionUID because else the loader will complain even if everything is compatible
    static final long serialVersionUID = 4309229556033378135L;
    
    public enum existsActionType {
        REPLACE, CONCATENATE, COPY, NEW, KEEP, REPLACEIFNOTEMPTY, PREPEND
    };

    public String targetID = null;                                      // ID this mapping Record points to
    public existsActionType existsAction = existsActionType.REPLACE;    // Action to take when the target already exists
    public boolean persistentMapping = false;                           // Is this a persistant mapping (which means it gets copied across any entries)

    // Define our serializable fields
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("targetID", String.class),
        new ObjectStreamField("existsAction", existsActionType.class),
        new ObjectStreamField("persistentMapping", boolean.class)
    };

    private void readObject(ObjectInputStream ois) throws IOException {
        try {
            ObjectInputStream.GetField fields = ois.readFields();
            this.targetID = (String) fields.get( "targetID", null );
            this.existsAction = (existsActionType) fields.get( "existsAction", existsActionType.REPLACE );
            this.persistentMapping = (boolean) fields.get( "persistentMapping", false );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        ObjectOutputStream.PutField fields = oos.putFields();
        fields.put( "targetID", this.targetID );
        fields.put( "existsAction", this.existsAction );
        fields.put( "persistentMapping", this.persistentMapping );

        // Write version one types
        oos.writeFields();
    }

    /**
     * Overwrite equals function, because that way we can compare both MappingRecords and just Strings
     * DEPRECATED
     * @param obj Object to compare to
     * @return
     */
    /*public boolean equals(Object obj) {
        // Compare to a MappingRecord (check targetID only)
        if( obj instanceof MappingRecord ) {
            MappingRecord cmp = (MappingRecord) obj;
            if( cmp.targetID.equals( this.targetID ) ) {
                return true;
            }
        }
        // Compare to a given String (which means compare to target ID)
        else if( obj instanceof String ) {
            String cmpID = (String) obj;
            if( cmpID.equals( this.targetID ) ) {
                return true;
            }
        }

        return false;
    }*/
}
