package pl.edu.agh.propertree.generator;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurationManager {

    public final void init() {
        Map<String, Integer> scanResult = new HashMap<>();
        AddressGenerator.scanConfigStructure(getConfigPath(), getOutputPath(), scanResult);
        RGenerator.generateRClass(scanResult);
    }

    public abstract String getConfigPath();

    public abstract String getOutputPath();
}
