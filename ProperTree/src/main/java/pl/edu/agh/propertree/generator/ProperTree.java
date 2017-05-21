package pl.edu.agh.propertree.generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public abstract class ProperTree {

    public static void main(String[] args) {
        Map<String, Integer> scanResult = new HashMap<>();
        AddressGenerator.scanConfigStructure(getConfigPath(), scanResult);
        RGenerator.generateRClass(scanResult);
    }

    private static String getConfigPath() {
        String configPath = null;
        try {
            FileReader reader = new FileReader("configPath");
            BufferedReader bufferedReader = new BufferedReader(reader);
            configPath = bufferedReader.readLine().trim();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return configPath;
    }
}
