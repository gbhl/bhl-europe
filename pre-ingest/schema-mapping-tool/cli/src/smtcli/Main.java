/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtcli;

import at.nhmwien.schema_mapping_tool.mappingProcess.*;
import at.nhmwien.schema_mapping_tool.fileProcessors.*;

import at.nhmwien.schema_mapping_tool.converter.MARC21Converter;
import at.nhmwien.schema_mapping_tool.converter.MARCXMLConverter;
import at.nhmwien.schema_mapping_tool.converter.MODSConverter;
import at.nhmwien.schema_mapping_tool.converter.REFNUMConverter;
import at.nhmwien.schema_mapping_tool.converter.XSLTransformer;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;

import java.nio.charset.Charset;
//import java.io.File;
import java.io.*;
import java.util.*;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author wkoller
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        XMLConfiguration config = new XMLConfiguration();

        Options options = new Options();
        options.addOption("h", "help", false, "Print Usage information");
        options.addOption("ifp", "input-file-processor", true, "Input File Processor Type" );
        options.addOption("ofp", "output-file-processor", true, "Output File Processor Type" );

        options.addOption("if", "input-file", true, "Name of Input File");
        options.addOption("of", "output-file", true, "Name of Output File");

        options.addOption("ife", "input-file-encoding", true, "Encoding of Input File");
        options.addOption("ofe", "output-file-encoding", true, "Encoding of Output File");

        options.addOption("ma", "map", true, "File containing the mappings");
        options.addOption("s", "settings", true, "File containing the settings for processing (CLI options override config settings)");

        options.addOption("v", "verbose", false, "Be verbose");
        options.addOption("p", "progress", false, "Print progress");

        options.addOption("le", "list-encodings", false, "List available encodings and exit" );
        options.addOption("lfp", "list-file-processors", false, "List available file-processors and exit" );
        options.addOption("lfpo", "list-file-processor-options", false, "List available options for a given file-processor (use -ifp or -ofp)" );

        options.addOption("m", "mode", true, "Select operating mode (c = conversion, m = mapping, x = xslt)" );
        options.addOption("cm", "conversion-mode", true, "Select conversion mode (only used for mode = c)\n1 = MARC21 to MARCXML\n2 = MARC21 to MODS\n3 = MARCXML to MODS\n4 = MARC21 to OLEF\n5 = MARCXML to OLEF\n6 = MODS to OLEF\n7 = refNum to OLEF");

        options.addOption( "iip", "input-id-prefix", true, "Prefix all input IDs with the given string, used to e.g. traverse down an XML tree." );
        options.addOption( "ct", "count-threshold", true, "Create new output file every <arg> entries." );

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (args.length <= 0 || cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("smt-cli", options);

                System.exit(1);
            }
            else if( cmd.hasOption( "lfp" ) ) {
                String[] availProc = ProcessorHandler.self().getProcessors();
                String availProcString = "";
                for( int i = 0; i < availProc.length; i++ ) {
                    availProcString += availProc[i] + "\n";
                }

                System.out.println( "Available File Processor:\n" + availProcString );
                System.exit(0);
            }
            else if( cmd.hasOption( "lfpo" ) ) {
                if( !cmd.hasOption( "ifp" ) && !cmd.hasOption( "ofp" ) ) {
                    System.err.println( "ERROR: Either specify ifp or ofp." );
                    System.exit(1);
                }
            }
            else if( cmd.hasOption( "le" ) ) {
                String availEncodings = "";
                SortedMap<String,Charset> availCharsets = Charset.availableCharsets();
                Iterator<Map.Entry<String,Charset>> it = availCharsets.entrySet().iterator();
                while( it.hasNext() ) {
                    Map.Entry<String,Charset> currEntry = it.next();

                    availEncodings += currEntry.getKey() + "\n";
                }

                System.out.println( "Available File Encodings:\n" + availEncodings );
                System.out.println( "NOTE: Not all encodings are compatible with all file processors!");
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Check what conversion mode we are running in
        if( cmd.hasOption( "m" ) ) {
            switch( cmd.getOptionValue( "m" ).charAt(0) ) {
                // Conversion mode, invoke conversion function and wait for result
                case 'c':
                    try {
                        Main.convertMode(Integer.parseInt(cmd.getOptionValue("cm")), cmd.getOptionValue("if"), cmd.getOptionValue("of"));
                        System.exit(1);
                    }
                    catch( Exception e ) {
                        System.err.println( "Error while running in convert Mode!\n");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    break;
                // XSLT Mode
                case 'x':
                    try {
                        // Apply transformation
                        XSLTransformer.transform( new File( cmd.getOptionValue("if")), new File(cmd.getOptionValue("of")), new File(cmd.getOptionValue("map")) );
                        System.out.println( "XSL Transformation succesfully done!" );
                        System.exit(0);
                    }
                    catch( Exception e ) {
                        System.err.println( "Error while applying XSL transformation.\n");
                        e.printStackTrace();
                        System.exit(1);
                    }
                    break;
                // Mapping mode, just continue
                case 'm':
                    break;
                // Something went wrong here, mode given but not a valid one
                default:
                    System.err.println( "Invalid operating mode '" + cmd.getOptionValue("m") + "'" );
                    System.exit(0);
                    break;

            }
        }

        // Check if a settings file was given, if yes load it
        if( cmd.hasOption("s") ) {
            try {
                config = new XMLConfiguration(cmd.getOptionValue("s"));
            }
            catch( Exception e ) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        // Override config file settings with any given command line settings
        if( cmd.hasOption("ifp") ) config.setProperty("config.input-file-processor", cmd.getOptionValue("ifp"));
        if( cmd.hasOption("ofp") ) config.setProperty("config.output-file-processor", cmd.getOptionValue("ofp"));
        if( cmd.hasOption("ife") ) config.setProperty("config.input-file-encoding", cmd.getOptionValue("ife"));
        if( cmd.hasOption("ofe") ) config.setProperty("config.output-file-encoding", cmd.getOptionValue("ofe"));
        if( cmd.hasOption("iip") ) config.setProperty("config.input-id-prefix", cmd.getOptionValue("iip"));
        if( cmd.hasOption("ct") ) config.setProperty("config.count-threshold", cmd.getOptionValue("ct"));

        //
        // LOAD MAPPINGS
        //
        try {
            String mapFileName = cmd.getOptionValue("map");
            MapFileHandler.self().loadFile(mapFileName);

            HashMap<String, HashMap<String, MappingRecord>> tempMappings = MapFileHandler.self().getMappings();
            HashMap<String, ArrayList<ManipulationRecord>> tempManipulations = MapFileHandler.self().getManipulations();

            // Clear Mappings-Handler
            MappingsHandler.Self().clear();
            // Clear Manipulations-Handler
            ManipulationsHandler.self().clear();

            // Re-Assign all manipulations
            ManipulationsHandler.self().setManipulations(tempManipulations);

            // Re-Run all mappings
            Iterator<Map.Entry<String, HashMap<String, MappingRecord>>> mapsIt = tempMappings.entrySet().iterator();
            while (mapsIt.hasNext()) {
                Map.Entry<String, HashMap<String, MappingRecord>> entry = (Map.Entry<String, HashMap<String, MappingRecord>>) mapsIt.next();
                HashMap<String, MappingRecord> mappingsList = entry.getValue();
                String outputFieldID = entry.getKey();
                //MappingFieldPanel currOutputMFP = null;

                Iterator<String> mplIt = mappingsList.keySet().iterator();
                while (mplIt.hasNext()) {
                    String inputFieldID = mplIt.next();

                    // Add mapping again
                    MappingsHandler.Self().addMapping(inputFieldID, outputFieldID);
                    // Set options of mapping
                    //MappingsHandler.self().manipulateMapping(selectedInputMFP.getFieldID(), selectedOutputMFP.getFieldID(), mappingsList.get(selectedInputMFP.getFieldID()).existsAction);
                    MappingRecord mr = MappingsHandler.Self().getMapping(inputFieldID, outputFieldID);
                    mr.existsAction = mappingsList.get(inputFieldID).existsAction;
                    mr.persistentMapping = mappingsList.get(inputFieldID).persistentMapping;
                    MappingsHandler.Self().setMapping(inputFieldID, outputFieldID, mr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

        // Set input and output order (if provided by the config file)
        if( config.containsKey( "config.input-field-order" ) ) MappingsHandler.Self().setInputOrder( (ArrayList<String>) config.getProperty( "config.input-field-order") );;
        if( config.containsKey( "config.output-field-order" ) ) MappingsHandler.Self().setInputOrder( (ArrayList<String>) config.getProperty( "config.output-field-order") );;

        //
        // START PROCESSING HERE
        //
        MappingProcess mp = new MappingProcess();
        try {
            if( cmd.hasOption( "v" ) ) System.out.println("Creating File Processors");

            FileProcessor ifp = ProcessorHandler.self().getProcessorForType((String) config.getProperty( "config.input-file-processor" ));
            FileProcessor ofp = ProcessorHandler.self().getProcessorForType((String) config.getProperty( "config.output-file-processor" ));

            System.out.println("Preparing & Initializing File Processors");

            mp.setProcessor(ifp, ofp);

            mp.setInputFile(cmd.getOptionValue("if"), Charset.forName((String) config.getProperty( "config.input-file-encoding" )));
            mp.setOutputFile(cmd.getOptionValue("of"), Charset.forName((String) config.getProperty( "config.output-file-encoding" )));

            // Set additional options
            if( config.containsKey("config.input-id-prefix") ) mp.setInputIDPrefix((String) config.getProperty("config.input-id-prefix"));
            if( config.containsKey("config.count-threshold") ) mp.setCountThreshold(Integer.valueOf((String) config.getProperty("config.count-threshold")) );

            if( cmd.hasOption( "v" ) ) System.out.println("Converting Mappings-Hash & Preparing Processors");

            try {
                mp.prepare();
            } catch (Exception e) {
                throw e;
            }

            //MappingsHandler.self().printMappings();

            System.out.println("Initialization Done - Starting with mapping process");
            while (mp.processMapping()) {
                if (Thread.interrupted()) {
                    System.out.println("Aborted");
                    break;
                }

                if( cmd.hasOption( "p" ) ) System.out.print("\rProcessing Entries - " + mp.getEntriesDone() + " Done.");
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error Occured! Processed entries: " + mp.getEntriesDone());
        }
        System.out.println("\nProcessing Finished - Total Records: " + mp.getEntriesDone());
        mp.done();
    }

    /**
     * Running in convert mode
     * @param convertMode Integer indicating the conversion to do
     * @param inputFileName Name of input file
     * @param outputFileName Name of output file
     */
    public static void convertMode( int convertMode, String inputFileName, String outputFileName ) throws Exception {
        if( inputFileName == null || outputFileName == null ) {
            System.err.println( "Please specify both input and output file name!" );
            return;
        }

        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);

        if( inputFile == null || outputFile == null || !inputFile.exists() ) {
            System.err.println( "Input / Output Error. Make sure input file exists!" );
            return;
        }

        switch( convertMode ) {
            // MARC21 to MARCXML
            case 1:
                System.out.print( "Starting conversion of MARC21 to MARCXML..." );
                MARC21Converter.convertToMARCXML(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // MARC21 to MODS
            case 2:
                System.out.print( "Starting conversion of MARC21 to MODS..." );
                MARC21Converter.convertToMODS(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // MARCXML to MODS
            case 3:
                System.out.print( "Starting conversion of MARCXML to MODS..." );
                MARCXMLConverter.convertToMODS(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // MARC21 to OLEF
            case 4:
                System.out.print( "Starting conversion of MARC21 to OLEF..." );
                MARC21Converter.convertToOLEF(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // MARCXML to OLEF
            case 5:
                System.out.print( "Starting conversion of MARCXML to OLEF..." );
                MARCXMLConverter.convertToOLEF(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // MODS to OLEF
            case 6:
                System.out.print( "Starting conversion of MODS to OLEF..." );
                MODSConverter.convertToOLEF(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            // refNum to OLEF
            case 7:
                System.out.print( "Starting conversion of refNum to OLEF..." );
                REFNUMConverter.convertToOLEF(inputFile, outputFile);
                System.out.println( "done!" );
                break;
            default:
                System.err.println( "Invalid conversion mode '" + convertMode + "' selected.");
                System.exit(0);
                break;
        }
    }
}
