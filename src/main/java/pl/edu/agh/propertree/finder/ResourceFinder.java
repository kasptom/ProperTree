package pl.edu.agh.propertree.finder;

import pl.edu.agh.propertree.generator.Types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        if (resourceType.equals(Types.STRINGS.typeName)) return findString(resourceId, configPrefix);

        if (resourceType.equals(Types.DOUBLES.typeName)) return findDouble(resourceId, configPrefix);

        if (resourceType.equals(Types.INTEGERS.typeName)) return findInteger(resourceId, configPrefix);

        if (resourceType.equals("string[]")) {/*TODO*/}

        if (resourceType.equals("double[]")) {/*TODO*/}

        if (resourceType.equals("int[]")) {/*TODO*/}

        if (resourceType.equals("string[][]")) {/*TODO*/}

        if (resourceType.equals("double[][]")) {/*TODO*/}

        if (resourceType.equals("int[][]")) {/*TODO*/}

        if (resourceType.equals("boolean")) {/*TODO*/}

        return null;
    }

    private static Address findBestFittingPostfix(int resourceId, String configPrefix) {
        List<Address> filteredAddresses = addresses
                .stream()
                .filter(a -> hasCompatiblePostfix())
                .collect(Collectors.toList());

        filteredAddresses = filteredAddresses
                .stream()
                .filter(a -> a.getId() == resourceId && Objects.equals(getConfigPostfix(a.getPath()), configPrefix)).distinct().collect(Collectors.toList());
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

    private static Double findDouble(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String stringValue = getValue(address);

        if (stringValue == null)
            throw new IllegalStateException(String.format("Could not find double with resource id 0x%s for config prefix %s",
                    Integer.toHexString(resourceId), configPrefix));

        return Double.valueOf(stringValue);
    }

    private static Integer findInteger(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        String stringValue = getValue(address);

        if (stringValue == null)
            throw new IllegalStateException(String.format("Could not find double with resource id 0x%s for config prefix %s",
                    Integer.toHexString(resourceId), configPrefix));

        return Integer.valueOf(stringValue);
    }

    private static String findString(int resourceId, String configPrefix) {
        Address address = findBestFittingPostfix(resourceId, configPrefix);
        return String.valueOf(getValue(address));
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
}
