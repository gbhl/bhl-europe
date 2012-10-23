/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.nhmwien.schema_mapping_tool.fileProcessors;

import java.sql.*;

/**
 *
 * @author wkoller
 */
// TODO: Add UI for JDBC processing
public class JDBCProcessor extends FileProcessor {
    protected String connectString = "";
    protected String tableName = "";
    protected Connection conn = null;
    protected Statement statement = null;
    protected ResultSet rs = null;
    protected ResultSetMetaData rsmd = null;
    protected int rowCount = 0;
    protected int currRowCount = 0;
    protected int stepCount = 100;

    // Set the file by name
    public void setFileName( String name ) {
        int tableNamePos = name.lastIndexOf( '&' );
        this.tableName = name.substring(tableNamePos + 1);
        this.connectString = name.substring(0, tableNamePos);
    }

    public void prepareFileRead() throws Exception {
        conn = DriverManager.getConnection( this.connectString + "&characterEncoding=" + this.fileEncoding.displayName() );
        //conn = DriverManager.getConnection( this.connectString );

        statement = conn.createStatement();
        rs = statement.executeQuery( "SELECT count(*) FROM " + this.tableName );
        rs.next();
        this.rowCount = rs.getInt( 1 );
        statement.close();

        statement = conn.createStatement();
        rs = statement.executeQuery("SELECT * FROM " + this.tableName + " LIMIT 0," + this.stepCount);
        rsmd = rs.getMetaData();
        currRowCount = stepCount;
    }

    public void prepareFileWrite() throws Exception {
        //this.prepareFileRead();
        conn = DriverManager.getConnection( this.connectString + "&characterEncoding=" + this.fileEncoding.displayName() );
        //conn = DriverManager.getConnection( this.connectString );
        //System.out.println( this.connectString + "&characterEncoding=" + this.fileEncoding.displayName() );

        rs = null;
    }

    public boolean hasNext() {
        try {
            if( this.recordsStack.size() > 0 ) return true;

            if( rs.next() ) return true;

            if( this.currRowCount < this.rowCount ) {
                //long fetchStart = System.currentTimeMillis();
                statement.close();
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT * FROM " + this.tableName + " LIMIT " + this.currRowCount + "," + this.stepCount);

                this.currRowCount += stepCount;

                rs.next();

                //System.out.println( "Fetch took: " + ((System.currentTimeMillis() - fetchStart)) );

                return true;
            }

            return false;

            //return ( this.recordsStack.size() > 0 || rs.next() );
        }
        catch( Exception e ) {
            return false;
        }
    }

    public void nextEntry() throws Exception {
        if( rs != null ) {
            //try {
                for( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
                    String columnContent = rs.getString( i );
                    //String columnContent = new String( rs.getBytes( i ), this.fileEncoding );
                    String columnName = rsmd.getColumnName( i );

                    DataRecord nRecord = new DataRecord();
                    nRecord.setIDRecord(columnName);
                    nRecord.setRecordContent(columnContent);

                    this.recordsStack.add(nRecord);
                }
            //}
            //catch( Exception e ) {
            //    return;
            //}
        }
        else {
            if( this.recordsStack.size() > 0 ) {
                // Prepare insert statement
                String fieldNames = "";
                String fieldValues = "";

                for( int i = 0; i < this.recordsStack.size() - 1; i++ ) {
                    fieldNames += "`" + this.recordsStack.get(i).getIDRecord()  + "`, ";
                    //fieldValues += "'" + this.recordsStack.get(i).getRecordContent().replace( "'" , "\'" ) + "', ";
                    fieldValues += "?, ";
                }

                fieldNames += "`" + this.recordsStack.get(this.recordsStack.size()-1).getIDRecord()  + "`";
                //fieldValues += "'" + this.recordsStack.get(this.recordsStack.size()-1).getRecordContent().replace( "'" , "\'" ) + "'";
                fieldValues += "?";
                //try {
                    //Statement statement = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement( "INSERT INTO " + this.tableName + " ( " + fieldNames + " ) values ( " + fieldValues + " )" );

                    for( int i = 0; i < this.recordsStack.size(); i++ ) {
                        statement.setString(i + 1, this.recordsStack.get(i).getRecordContent() );
                        //statement.setBytes(i + 1, this.recordsStack.get(i).getRecordContent().getBytes( this.fileEncoding ) );
                        //statement.setString(i + 1, new String( this.recordsStack.get(i).getRecordContent().getBytes( this.fileEncoding ), this.fileEncoding ) );
                    }

                    statement.execute();
                    statement.close();
                    //statement.executeUpdate( "INSERT INTO " + this.tableName + " ( " + fieldNames + " ) values ( " + fieldValues + " )" );
                /*}
                catch( Exception e ) {
                    e.printStackTrace();
                }*/

                this.recordsStack.clear();
            }
        }
    }

    @Override
    public void skipEntry() throws Exception {
        this.recordsStack.clear();
    }

    public DataRecord getDataRecord( String IDRecord ) {
        DataRecord nRecord = new DataRecord();
        nRecord.setIDRecord(IDRecord);
        nRecord.setRecordContent( "" );

        for( int i = 0; i < this.recordsStack.size(); i++ ) {
            DataRecord currRecord = this.recordsStack.get(i);

            if( currRecord.getIDRecord().equals( nRecord.getIDRecord() ) ) {
                nRecord.setRecordContent( currRecord.getRecordContent() );

                break;
            }
        }

        return nRecord;
    }

    public void addDataRecord( DataRecord nRecord, boolean bAddNew, boolean bReplaceAll ) {
        for( int i = 0; i < this.recordsStack.size(); i++ ) {
            DataRecord currRecord = this.recordsStack.get(i);

            if( currRecord.getIDRecord().equals( nRecord.getIDRecord() ) ) {
                this.recordsStack.remove(i);

                break;
            }
        }

        this.recordsStack.add(nRecord);
    }

    public void done() {
        try {
            conn.close();
        }
        catch( Exception e ) {
            return;
        }
    }

    public String[] getSupportedFileExtensions() {
        String arr[] = { "SQL" };

        return arr;
    }
}
