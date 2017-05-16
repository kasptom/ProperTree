package pl.edu.agh.propertree.demo;

import pl.edu.agh.propertree.generator.AddressGenerator;
import pl.edu.agh.propertree.generator.RGenerator;

import java.util.HashMap;
import java.util.Map;

public class GenerateDemo {
    public static void main(String[] args) {
        Map<String, Integer> scanResult = new HashMap<>();
        AddressGenerator.scanConfigStructure(AddressGenerator.CONFIG_ROOT, scanResult);
        RGenerator.generateRClass(scanResult);
    }
}
