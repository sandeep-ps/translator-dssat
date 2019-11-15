package org.agmip.translators.dssat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.agmip.acmo.util.AcmoUtil;
import org.agmip.functions.DataCombinationHelper;
import org.agmip.util.JSONAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class DssatCmdApp {

    private static boolean isCompressed;
    private static boolean isToModel;
    private static boolean isHelp;
    private static ArrayList<String> inputPaths;
    private static String outputPath;
    private static final Logger LOG = LoggerFactory.getLogger(DssatCmdApp.class);

    public static void main(String... args) throws IOException {
        init();
        readCommand(args);
        if (inputPaths == null) {
            LOG.error("There is no valid input path, please check input arguments");
            return;
        }

        if (isHelp) {
            System.out.println("DSSAT Translator Help");
            System.out.println();
            System.out.println("### Convert from JSON to DSSAT model");
            System.out.println("$ java -jar translator-dssat-1.2.20-jar-with-dependencies.jar <input_file.json>");
            System.out.println();
            System.out.println("### Convert from DSSAT model to JSON");
            System.out.println("$ java -jar translator-dssat-1.2.20-jar-with-dependencies.jar <input_file.SQX.zip>");
            System.out.println("Here, <input_file.SQX.zip> is a ZIP file containing the SQX file.");
            System.out.println();
            return;
        }

        if (isToModel) {
            LOG.info("Translate {} to DSSAT...", inputPaths);
            DssatControllerOutput translator = new DssatControllerOutput();
            HashMap data = readJson();
            translator.writeFile(outputPath, data);
            if (isCompressed) {
                translator.createZip(new File(outputPath + File.separator + "DSSAT_Input.zip"));
            }
            writeAcmo(data);
        } else {
            LOG.info("Translate {} to JSON...", inputPaths.get(0));
            DssatControllerInput translator = new DssatControllerInput();
            Map result = translator.readFile(inputPaths.get(0));
            BufferedOutputStream bo;
            outputPath += File.separator;

            // Create output directory if it doesn't exist
            File directory = new File(outputPath);
            boolean success = directory.mkdirs();

            if (success) {
                File f = new File(outputPath + new File(inputPaths.get(0)).getName().replaceAll("\\.\\w+$", ".json"));
                bo = new BufferedOutputStream(new FileOutputStream(f));
                // Output json for reading
                bo.write(JSONAdapter.toJSON(result).getBytes());
                bo.flush();
                bo.close();
            }
            else
            {
                LOG.error("Error creating output directory.");
                return;
            }
        }
        LOG.info("Job done!");
    }

    private static void init() {
        isCompressed = false;
        isToModel = false;
        isHelp = false;
        inputPaths = new ArrayList();
        outputPath = getOutputPath("");
    }

    private static void readCommand(String[] args) {

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-zip")) {
                isCompressed = true;
            } else if (arg.toUpperCase().endsWith(".JSON")) {
                isToModel = true;
                inputPaths.add(arg);
            } else if (arg.toUpperCase().endsWith(".ZIP")) {
                isToModel = false;
                inputPaths.add(arg);
            }
            else if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h")) {
                    isHelp = true;
            } else {
                outputPath = getOutputPath(arg);
            }
        }
        if (!isHelp) {
            LOG.info("Read from {}", inputPaths);
            LOG.info("Output directory: {}", outputPath);
        }
    }

    private static HashMap readJson() throws IOException {
        HashMap data = DataCombinationHelper.combine(inputPaths);
        DataCombinationHelper.fixData(data);
        return data;
    }

    private static String getOutputPath(String arg) {

        String path;
        if (arg != null && !"".equals(arg)) {
            path = new File(arg).getPath() + File.separator + "DSSAT";
        } else {
            path = "DSSAT";
        }

        File dir = new File(path);
        int cnt = 1;
        while (dir.exists() && dir.list().length > 0) {
            dir = new File(path + "-" + cnt);
            cnt++;
        }
        return dir.getPath();
    }

    private static void writeAcmo(HashMap data) {
        AcmoUtil.writeAcmo(outputPath, data, "dssat", new HashMap());
    }
}
