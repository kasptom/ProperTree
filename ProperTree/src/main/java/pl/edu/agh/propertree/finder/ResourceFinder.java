package pl.edu.agh.propertree.finder;

import pl.edu.agh.propertree.generator.Types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResourceFinder {

    private static final String POSTFIX_REGEX = "(-)([\\p{Print}&&[^-]]+)/[\\p{Print}&&[^/]]+$";
    private static final Pattern POSTFIX_PATTERN = Pattern.compile(POSTFIX_REGEX);

    private static List<Address> addresses = ReferencesParser.parse();

    public static Object getResource(int resourceId, String configPrefix) {
        String resourceType = Types.getTypeName(resourceId);
        if (resourceType.equals(Types.INTEGERS.typeName)) return findInteger(resourceId, configPrefix);

        if (resourceType.equals(Types.DOUBLES.typeName)) return findDouble(resourceId, configPrefix);

        if (resourceType.equals(Types.BOOLEANS.typeName)) return findBoolean(resourceId, configPrefix);

        if (resourceType.equals(Types.STRINGS.typeName)) return findString(resourceId, configPrefix);

        if (resourceType.equals(Types.INTEGER_1D_ARRAYS.typeName)) return findInteger1DArray(resourceId, configPrefix);

        if (resourceType.equals(Types.DOUBLE_1D_ARRAYS.typeName)) return findDouble1DArray(resourceId, configPrefix);

        if (resourceType.equals("string[]")) ; /*TODO*/

        if (resourceType.equals(Types.INTEGER_2D_ARRAYS.typeName)) return findInteger2DArray(resourceId, configPrefix);

        if (resourceType.equals(Types.DOUBLE_2D_ARRAYS.typeName)) return findDouble2DArray(resourceId, configPrefix);

        if (resourceType.equals("string[][]")) ; /*TODO*/

        return null;
    }

    private static Integer findInteger(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String stringValue = getValue(address);

        if (stringValue == null)
            throw new IllegalStateException(String.format("Could not find integer with resource id 0x%s for config prefix %s",
                    Integer.toHexString(resourceId), configPrefix));

        return Integer.valueOf(stringValue);
    }

    private static Double findDouble(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String stringValue = getValue(address);

        if (stringValue == null)
            throw new IllegalStateException(String.format("Could not find double with resource id 0x%s for config prefix %s",
                    Integer.toHexString(resourceId), configPrefix));

        return Double.valueOf(stringValue);
    }

    private static Boolean findBoolean(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        return Boolean.parseBoolean(getValue(address));
    }

    private static String findString(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        return String.valueOf(getValue(address));
    }

    private static Integer[] findInteger1DArray(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String[] rowStr = get1DArrayValues(address);
        Integer[] row = new Integer[rowStr.length];

        for (int i = 0; i < row.length; i++) {
            row[i] = Integer.parseInt(rowStr[i]);
        }
        return row;
    }

    private static Double[] findDouble1DArray(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String[] rowStr = get1DArrayValues(address);
        Double[] row = new Double[rowStr.length];

        for (int i = 0; i < row.length; i++) {
            row[i] = Double.parseDouble(rowStr[i]);
        }
        return row;
    }

    private static Integer[][] findInteger2DArray(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String[][] rowsStr = get2DArrayValues(address);
        Integer[][] rows = new Integer[rowsStr.length][rowsStr[0].length];

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[0].length; j++) {
                rows[i][j] = Integer.parseInt(rowsStr[i][j]);
            }
        }
        return rows;
    }

    private static Double[][] findDouble2DArray(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String[][] rowsStr = get2DArrayValues(address);
        Double[][] rows = new Double[rowsStr.length][rowsStr[0].length];

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[0].length; j++) {
                rows[i][j] = Double.parseDouble(rowsStr[i][j]);
            }
        }
        return rows;
    }

    private static Address findBestFittingPostfix(int resourceId, String configPostfix) {
        List<Address> filteredAddresses = addresses
                .stream()
                .filter(a -> hasCompatiblePostfix())
                .collect(Collectors.toList());

        filteredAddresses = filteredAddresses
                .stream()
                .filter(a -> a.getId() == resourceId && Objects.equals(getConfigPostfix(a.getPath()), configPostfix)).distinct().collect(Collectors.toList());
        if (filteredAddresses.isEmpty()) {
            filteredAddresses = addresses
                    .stream()
                    .filter(a -> a.getId() == resourceId && Objects.equals(getConfigPostfix(a.getPath()), "")).distinct().collect(Collectors.toList());
        }
        return filteredAddresses.get(0);
    }

    private static boolean hasCompatiblePostfix() {
        //TODO return false if prefix collides with the given one
        return true;
    }

    private static String getConfigPostfix(String path) {
        Matcher matcher = POSTFIX_PATTERN.matcher(path);

        if (matcher.find()) {
            return matcher.group(2);
        }

        return "";
    }

    @SuppressWarnings("unused")
    private static int getFieldPosition(int resourceId) {
        return resourceId & 0x0000FFFF;
    }

    @SuppressWarnings("unused")
    private static int getPackageIndex(int resourceId) {
        return (resourceId & 0xFF000000) >> 24;
    }

    //TODO load whole file to an object
    private static String getValue(Address address) {
        try {
            FileReader fileReader = new FileReader(address.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String[] keyValue;
            int lineCounter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                keyValue = line.split("=");
                lineCounter++;
                if (lineCounter == address.getLineNumber()) {
                    return keyValue[1].trim();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String[] get1DArrayValues(Address address) {
        String[] row = null;
        try {
            FileReader fileReader = new FileReader(address.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String[] keyValue;
            int lineCounter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                keyValue = line.split("=");
                lineCounter++;
                if (lineCounter == address.getLineNumber()) {
                    row = keyValue[1].trim().split("\\s");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return row;
    }

    private static String[][] get2DArrayValues(Address address) {
        List<String[]> rows = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(address.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String[] keyValue;
            int lineCounter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                keyValue = line.split("=");
                lineCounter++;
                if (lineCounter == address.getLineNumber()) {
                    rows.add(keyValue[1]
                            .trim()
                            .replace(",", "").split("\\s"));

                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("=") || line.isEmpty()) break;
                        rows.add(line.trim().replace(",", "").split("\\s"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[][] result = new String[rows.size()][rows.get(0).length];
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).length; j++) {
                result[i][j] = rows.get(i)[j];
            }
        }

        return result;
    }
}
