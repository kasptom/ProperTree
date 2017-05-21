package pl.demo;

import pl.edu.agh.propertree.finder.ResourceFinder;
import pl.edu.agh.propertree.generated.R;

import java.util.Locale;

public class ConfigurablePerson {

    private String name;
    private String nationality;
    private String currency;
    private double height;
    private double weight;

    private ConfigurablePerson(String configPrefix) {
        name = (String) ResourceFinder.getResource(R.strings.name, configPrefix);
        nationality = (String) ResourceFinder.getResource(R.strings.nationality, configPrefix);
        currency = (String) ResourceFinder.getResource(R.strings.currency, configPrefix);

        height = (int) ResourceFinder.getResource(R.integers.height, configPrefix);
        weight = (int) ResourceFinder.getResource(R.integers.weight, configPrefix);
    }

    private void printAttributes() {
        System.out.println(String.format(Locale.getDefault(), "name: %s\n" +
                "nationality: %s\n" +
                "currency: %s\n" +
                "height: %f\n" +
                "weight: %f\n", name, nationality, currency, height, weight));
    }

    public static void main(String[] args) {
        ConfigurablePerson english = new ConfigurablePerson("en");
        ConfigurablePerson polish = new ConfigurablePerson("pl");

        english.printAttributes();
        polish.printAttributes();

        Integer[] otherArray = (Integer[]) ResourceFinder.getResource(R.integer1DArrays.otherArray, "");
        assert otherArray != null;
        System.out.println(otherArray[0]);
        Integer[][] testIntegerArray = (Integer[][]) ResourceFinder.getResource(R.integer2DArrays.testIntegerArray, "");
        assert testIntegerArray != null;
        System.out.println(testIntegerArray[0][0]);

        Double[] oneDimDoubleArray = (Double[]) ResourceFinder.getResource(R.double1DArrays.oneDimDoubleArray, "en");
        Double[][] twoDimDoubleArray = (Double[][]) ResourceFinder.getResource(R.double2DArrays.twoDimDoubleArray, "en");

        assert oneDimDoubleArray != null;
        System.out.println(oneDimDoubleArray[3]);
        assert twoDimDoubleArray != null;
        System.out.println(twoDimDoubleArray[0][2]);
    }
}