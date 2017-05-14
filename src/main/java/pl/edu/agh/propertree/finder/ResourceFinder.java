package pl.edu.agh.propertree.finder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResourceFinder {

    private static final String POSTFIX_REGEX = "(-)([\\p{Print}&&[^-]]+)/[\\p{Print}&&[^/]]+$";
    private static final Pattern POSTFIX_PATTERN = Pattern.compile(POSTFIX_REGEX);

    public static final List<String> AVAILABLE_TYPES = new ArrayList<>(Arrays.asList(
            "string", "double", "int",
            "string[]", "double[]", "int[]",
            "string[][]", "double[][]", "int[][]",
            "boolean"
    ));
    private static List<Address> addresses = ReferencesParser.parse();

    public static Object getResource(int resourceId, String configPrefix) {
        String resourceType = getTypeName(resourceId);
        if (resourceType.equals("string")) return findString(resourceId, configPrefix);

        if (resourceType.equals("double")) return findDouble(resourceId, configPrefix);

        if (resourceType.equals("int")){/*TODO*/}

        if (resourceType.equals("string[]")) {/*TODO*/}

        if (resourceType.equals("double[]")) {/*TODO*/}

        if (resourceType.equals("int[]")) {/*TODO*/}

        if (resourceType.equals("string[][]")) {/*TODO*/}

        if (resourceType.equals("double[][]")) {/*TODO*/}

        if (resourceType.equals("int[][]")) {/*TODO*/}

        if (resourceType.equals("boolean")) {/*TODO*/}

        return null;
    }

    private static Address findWithPrefixOrDefault(int resourceId, String configPrefix) {
        List<Address> filteredAddresses = addresses
                .stream()
                .filter(a -> a.getId() == resourceId && Objects.equals(getConfigPrefix(a.getPath()), configPrefix)).distinct().collect(Collectors.toList());
        if (filteredAddresses.isEmpty()) {
            filteredAddresses = addresses
                    .stream()
                    .filter(a -> a.getId() == resourceId && Objects.equals(getConfigPrefix(a.getPath()), "")).distinct().collect(Collectors.toList());
        }
        return filteredAddresses.get(0);
    }

    private static Double findDouble(int resourceId, String configPrefix) {
        Address address = findWithPrefixOrDefault(resourceId, configPrefix);
        String stringValue = getValue(address);

        if (stringValue == null)
            throw new IllegalStateException(String.format("Could not find double with resource id 0x%s for config prefix %s",
                    Integer.toHexString(resourceId), configPrefix));

        return Double.valueOf(stringValue);
    }

    private static String findString(int resourceId, String configPrefix) {
        Address address = findWithPrefixOrDefault(resourceId, configPrefix);
        return String.valueOf(getValue(address));
    }

    private static String getConfigPrefix(String path) {
        Matcher matcher = POSTFIX_PATTERN.matcher(path);

        if (matcher.find()) {
            return matcher.group(2);
        }

        return "";
    }


    private static String getTypeName(int resourceId) {
        int typeIndex = (resourceId & 0x00FF0000) >> 16;
//        System.out.println(String.format("type index: %d", typeIndex));

        return AVAILABLE_TYPES.get(typeIndex - 1);
    }

    private static int getFieldPosition(int resourceId) {
        return resourceId & 0x0000FFFF;
    }

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
}
