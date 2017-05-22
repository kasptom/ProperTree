package pl.edu.agh.propertree.generator;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AddressGenerator {
    private static final String KEY_EQUALS_VALUE_REGEX =
            "(^[\\p{Print}&&\\S&&[^=]]+)([\\s]+=[\\s]+)([\\p{Print}&&\\S&&[^=]]+)([\\s]*)($|\\n)";
    private static final String KEY_EQUALS_MATRIX_REGEX = "(^[\\p{Print}&&\\S&&[^=]]+)([\\s]*=[\\s]*)([\\p{Print}&&[^=]]+)([\\s]*)($|\\n)";
    private static final String NO_WHITESPACES_REGEX = "(^)([\\s]*)([\\p{Print}&&\\S]+)([\\s]*)($|\\n)";
    private static final String MATRIX_ROW_REGEX = "(^)([\\s]*)([\\p{Print}&&[^=]]+)([\\s]*)($|\\n)";

    private static final Pattern KEY_EQUALS_VALUE_PATTERN = Pattern.compile(KEY_EQUALS_VALUE_REGEX);
    private static final Pattern NO_WHITESPACES_PATTERN = Pattern.compile(NO_WHITESPACES_REGEX);
    private static final Pattern KEY_EQUALS_MATRIX_PATTERN = Pattern.compile(KEY_EQUALS_MATRIX_REGEX);
    private static final Pattern MATRIX_ROW_PATTERN = Pattern.compile(MATRIX_ROW_REGEX);

    /**
     * Scans the config files located in CONFIG_ROOT_PATH and its subdirectories.
     * Uses DFS to visit all of the subdirectories. Saves the result in {@code scanResult}.
     *
     * @param scanResult map of names and values assigned to them
     */
    static void scanConfigStructure(String configRootPath, String referencesFilePath, Map<String, Integer> scanResult) {
        if (!prepareReferenceTableFile(referencesFilePath)) {
            throw new RuntimeException("Could not prepare reference table file");
        }

        scan(configRootPath, referencesFilePath, scanResult);
    }

    private static void scan(String configRootPath, String referencesFilePath, Map<String, Integer> scanResult) {
        File root = new File(configRootPath);
        File[] contents = root.listFiles();
        if (contents == null || contents.length == 0) {
            System.err.println(String.format("Directory %s is empty", root.getPath()));
            return;
        }

        try {
            for (File file : contents) {
                if (file.isDirectory()) {
                    scan(file.getPath(), referencesFilePath, scanResult);
                } else {
                    scanFile(file, scanResult, referencesFilePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanFile(File file, Map<String, Integer> scanResult, String referencesFilePath) throws IOException {
        FileReader fileReader = new FileReader(file.getPath());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String filePath = file.getPath();

        String line;
        Matcher matcher;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lineNumber++;

            matcher = KEY_EQUALS_VALUE_PATTERN.matcher(line);
            if (matcher.find()) { // key = value
                String name = matcher.group(1);
                String value = matcher.group(3).trim();
//                System.out.println(String.format("FOUND: %s %s", name, value));
                addConfigEntry(name, value, filePath, lineNumber, scanResult, referencesFilePath);
                continue;
            }

            //check if it is not a matrix
            matcher = KEY_EQUALS_MATRIX_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            String name = matcher.group(1);
            StringBuilder value = new StringBuilder(matcher.group(3).trim());

            //check if it has other dimensions
            int tmpLine = lineNumber;
            while (isNextRowMatrix(bufferedReader)) {
                lineNumber++;
//                System.out.println("NEXT MATRIX ROW");
                value.append(",").append(bufferedReader.readLine().trim());
            }

            addConfigEntry(name, value.toString(), filePath, tmpLine, scanResult, referencesFilePath);
        }
    }

    private static boolean isNextRowMatrix(BufferedReader bufferedReader) throws IOException {
        int READ_AHEAD_LIMIT = 1;
        String nextLine;
        bufferedReader.mark(READ_AHEAD_LIMIT);

        if ((nextLine = bufferedReader.readLine()) != null) {
            Matcher matcher = MATRIX_ROW_PATTERN.matcher(nextLine);
            bufferedReader.reset();
            return matcher.find() && !matcher.group(3).isEmpty();
        }
        bufferedReader.reset();

        return false;
    }

    private static boolean prepareReferenceTableFile(String referencesFilePath) {
        File referenceTableFile = new File(referencesFilePath);
        try {
            if (!referenceTableFile.exists()) {
                return referenceTableFile.createNewFile();
            } else {
                return referenceTableFile.delete() && referenceTableFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void addConfigEntry(String name, String value, String filePath, int lineNumber, Map<String, Integer> scanResult, String referencesTablePath) {

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(referencesTablePath, true);
            Integer id;
            if (scanResult.containsKey(name)) {
                id = scanResult.get(name);
                writeReferenceLine(filePath, lineNumber, fileWriter, id);
                fileWriter.close();
                return;
            }
            if (hasInteger(value)) {
                id = EntryIdFactory.nextIntId();
            } else if (hasDouble(value)) {
                id = EntryIdFactory.nextDoubleId();
            } else if (hasBoolean(value)) {
                id = EntryIdFactory.nextBooleanId();
            } else if (hasString(value)) {
                id = EntryIdFactory.nextStringId();
            } else if (hasInteger1DArray(value)) {
                id = EntryIdFactory.nextInteger1DArrayId();
            } else if (hasDouble1DArray(value)) {
                id = EntryIdFactory.nextDouble1DArrayId();
            } else if (hasString1DArray(value)) {
                id = EntryIdFactory.nextString1DArrayId();
            } else if (hasInteger2DArray(value)) {
                id = EntryIdFactory.nextInteger2DArrayId();
            } else if (hasDouble2DArray(value)) {
                id = EntryIdFactory.nextDouble2DArrayId();
            } else if (hasString2DArray(value)) {
                id = EntryIdFactory.nextString2DArrayId();
            } else {
                throw new IllegalStateException(String.format("Could not parse data from config file: %s, line: %d", filePath, lineNumber));
            }

            scanResult.put(name, id);
            writeReferenceLine(filePath, lineNumber, fileWriter, id);

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

    static boolean hasInteger(String value) {
        boolean hasInteger = true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            hasInteger = false;
        }
        return hasInteger;
    }

    private static boolean hasDouble(String value) {
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
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

    private static boolean hasString(String value) {
        Matcher noWhiteSpaceMatcher = NO_WHITESPACES_PATTERN.matcher(value);
        return noWhiteSpaceMatcher.find() && !noWhiteSpaceMatcher.group(3).isEmpty();
    }

    private static boolean hasInteger1DArray(String value) {
        boolean hasInteger1DArray = true;
        if (value.contains(","))
            return false;
        String[] values = value.split("\\s");
        try {
            for (String val : values) {
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(val);
            }
        } catch (NumberFormatException e) {
            hasInteger1DArray = false;
        }

        return hasInteger1DArray;
    }

    private static boolean hasDouble1DArray(String value) {
        boolean hasDouble1DArray = true;
        if (value.contains(","))
            return false;
        String[] values = value.split("\\s");
        try {
            for (String val : values) {
                //noinspection ResultOfMethodCallIgnored
                Double.parseDouble(val);
            }
        } catch (NumberFormatException e) {
            hasDouble1DArray = false;
        }

        return hasDouble1DArray;
    }

    private static boolean hasString1DArray(String value) {
        //TODO check input
        return false;
    }

    private static boolean hasInteger2DArray(String value) {
        boolean hasInteger2DArray = true;
        if (!value.contains(","))
            return false;
        String[] rows = value.split(",");
        try {
            for (String row : rows) {
                String[] values = row.split("\\s");
                for (String val : values) {
                    //noinspection ResultOfMethodCallIgnored
                    Integer.parseInt(val);
                }
            }
        } catch (NumberFormatException e) {
            hasInteger2DArray = false;
        }

        return hasInteger2DArray;
    }

    private static boolean hasDouble2DArray(String value) {
        boolean hasDouble2DArray = true;
        if (!value.contains(","))
            return false;
        String[] rows = value.split(",");
        try {
            for (String row : rows) {
                String[] values = row.split("\\s");
                for (String val : values) {
                    //noinspection ResultOfMethodCallIgnored
                    Double.parseDouble(val);
                }
            }
        } catch (NumberFormatException e) {
            hasDouble2DArray = false;
        }

        return hasDouble2DArray;
    }

    private static boolean hasString2DArray(String value) {
        //TODO check input
        return false;
    }

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

        static Integer nextIntId() {
            integersCounter++;
            return Types.INTEGERS.typeValue + integersCounter;
        }

        static Integer nextDoubleId() {
            doublesCounter++;
            return Types.DOUBLES.typeValue + doublesCounter;
        }

        static Integer nextBooleanId() {
            booleansCounter++;
            return Types.BOOLEANS.typeValue + booleansCounter;
        }

        static Integer nextStringId() {
            stringsCounter++;
            return Types.STRINGS.typeValue + stringsCounter;
        }

        static Integer nextInteger1DArrayId() {
            integer1DArraysCounter++;
            return Types.INTEGER_1D_ARRAYS.typeValue + integer1DArraysCounter;
        }

        static Integer nextDouble1DArrayId() {
            double1DArraysCounter++;
            return Types.DOUBLE_1D_ARRAYS.typeValue + double1DArraysCounter;
        }

        static Integer nextString1DArrayId() {
            string1DArraysCounter++;
            return Types.STRING_1D_ARRAYS.typeValue + string1DArraysCounter;
        }

        static Integer nextInteger2DArrayId() {
            integer2DArraysCounter++;
            return Types.INTEGER_2D_ARRAYS.typeValue + integer2DArraysCounter;
        }

        static Integer nextDouble2DArrayId() {
            double2DArraysCounter++;
            return Types.DOUBLE_2D_ARRAYS.typeValue + double2DArraysCounter;
        }

        static Integer nextString2DArrayId() {
            string2DArraysCounter++;
            return Types.STRING_2D_ARRAYS.typeValue + string2DArraysCounter;
        }
    }
}
