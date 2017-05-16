package pl.edu.agh.propertree.generator;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressGenerator {
    private static final String CONFIG_ROOT_PATH = "configuration/";
    private static final String GENERATED_REFERENCES_TABLE_PATH = "generated/gen_ref_tab";
    public static final File CONFIG_ROOT = new File(CONFIG_ROOT_PATH);

    private static final String PROPER_LINE_REGEX = "([\\p{Print}&&\\S]+)([\\s]+=[\\s]+)([\\p{Print}]+)";
    private static final String NO_WHITESPACES_REGEX = "[\\p{Print}&&\\S]+";
    private static final Pattern PROPER_LINE_PATTERN = Pattern.compile(PROPER_LINE_REGEX);
    private static final Pattern NO_WHITESPACES_PATTERN = Pattern.compile(NO_WHITESPACES_REGEX);

    static {
        prepareReferenceTableFile();
    }

    /**
     * Scans the config files located in CONFIG_ROOT_PATH and its subdirectories.
     * Uses DFS to visit all of the subdirectories. Saves the result in {@code scanResult}.
     *
     * @param scanResult map of names and values assigned to them
     */
    public static void scanConfigStructure(File root, Map<String, Integer> scanResult) {
        File[] contents = root.listFiles();
        if (contents == null || contents.length == 0) {
            System.err.println(String.format("Directory %s is empty", root.getPath()));
            return;
        }

        try {
            for (File file : contents) {
                if (file.isDirectory()) {
                    scanConfigStructure(file, scanResult);
                } else {
                    scanFile(file, scanResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanFile(File file, Map<String, Integer> scanResult) throws IOException {
        FileReader fileReader = new FileReader(file.getPath());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String filePath = file.getPath();

        String line;
        Matcher matcher;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lineNumber++;
            matcher = PROPER_LINE_PATTERN.matcher(line);

            if (!matcher.find()) {
                return;
            }

            String name = matcher.group(1);
            String value = matcher.group(3).trim();
            /*
            while (matcher.find()) {
                 //TODO use it for arrays
            }
            */

            addConfigEntry(name, value, filePath, lineNumber, scanResult);
        }
    }

    private static boolean prepareReferenceTableFile() {
        File referenceTableFile = new File(GENERATED_REFERENCES_TABLE_PATH);
        return referenceTableFile.exists() && referenceTableFile.delete();
    }

    private static void addConfigEntry(String name, String value, String filePath, int lineNumber, Map<String, Integer> scanResult) {

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(GENERATED_REFERENCES_TABLE_PATH, true);
            Integer id;
            if (hasInteger(value)) {
                if (scanResult.containsKey(name)) {
                    id = scanResult.get(name);
                } else {
                    id = EntryIdFactory.nextIntId();
                    scanResult.put(name, id);
                }

                writeReferenceLine(filePath, lineNumber, fileWriter, id);
            } else if (hasDouble(value)) {
                if (scanResult.containsKey(name)) {
                    id = scanResult.get(name);
                } else {
                    id = EntryIdFactory.nextDoubleId();
                    scanResult.put(name, id);
                }

                writeReferenceLine(filePath, lineNumber, fileWriter, id);
            } else if (hasString(value)) {
                if (scanResult.containsKey(name)) {
                    id = scanResult.get(name);
                } else {
                    id = EntryIdFactory.nextStringId();
                    scanResult.put(name, id);
                }

                writeReferenceLine(filePath, lineNumber, fileWriter, id);
            }


            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeReferenceLine(String filePath, int lineNumber, FileWriter fileWriter, Integer id) throws IOException {
        String idHex = "0x" + Integer.toHexString(id);
        String toWrite = String.format("%s;%s;%d\n", idHex, filePath, lineNumber);
        fileWriter.write(toWrite);
    }

    public static boolean hasInteger(String value) {
        boolean hasInteger = true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            hasInteger = false;
        }
        return hasInteger;
    }

    public static boolean hasDouble(String value) {
        boolean hasDouble = true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            hasDouble = false;
        }
        return hasDouble;
    }

    private static boolean hasBoolean(String value) {
        boolean hasBoolean = true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            hasBoolean = false;
        }
        return hasBoolean;
    }

    public static boolean hasString(String value) {
        Matcher noWhiteSpaceMatcher = NO_WHITESPACES_PATTERN.matcher(value);
        return noWhiteSpaceMatcher.find() && !noWhiteSpaceMatcher.group().isEmpty();
    }

    @SuppressWarnings("unused")
    static class EntryIdFactory {
        private static int stringsCounter = 0;
        private static int doublesCounter = 0;
        private static int integersCounter = 0;

        private static int string1DArraysCounter = 0;
        private static int double1DArraysCounter = 0;
        private static int integer1DArraysCounter = 0;

        private static int string2DArraysCounter = 0;
        private static int double2DArraysCounter = 0;
        private static int integer2DArraysCounter = 0;

        private static int booleansCounter = 0;

        static Integer nextStringId() {
            stringsCounter++;
            return 0x01010000 + stringsCounter;
        }

        static Integer nextDoubleId() {
            doublesCounter++;
            return 0x01020000 + doublesCounter;
        }

        static Integer nextIntId() {
            integersCounter++;
            return 0x01030000 + integersCounter;
        }

        static Integer nextString1DArrayId() {
            string1DArraysCounter++;
            return 0x01040000 + string1DArraysCounter;
        }

        static Integer nextDouble1DArrayId() {
            double1DArraysCounter++;
            return 0x01050000 + double1DArraysCounter;
        }

        static Integer nextInteger1DArrayId() {
            integer1DArraysCounter++;
            return 0x01060000 + integer1DArraysCounter;
        }

        static Integer nextString2DArrayId() {
            string2DArraysCounter++;
            return 0x01070000 + string2DArraysCounter;
        }

        static Integer nextDouble2DArrayId() {
            double2DArraysCounter++;
            return 0x01080000 + double2DArraysCounter;
        }

        static Integer nextInteger2DArrayId() {
            integer2DArraysCounter++;
            return 0x01090000 + integer2DArraysCounter;
        }

        static Integer nextBooleanId() {
            booleansCounter++;
            return 0x010A0000 + booleansCounter;
        }
    }
}
