package pl.edu.agh.propertree.demo;

import pl.edu.agh.propertree.Configurable;
import pl.edu.agh.propertree.finder.ResourceFinder;
import pl.edu.agh.propertree.generated.R;

import java.util.Locale;

public class ConfigurablePerson implements Configurable {

    private final String configPrefix;

    private String name;
    private String nationality;
    private String currency;
    private double height;
    private double weight;

    private ConfigurablePerson(String configPrefix) {
        this.configPrefix = configPrefix;
        name = (String) ResourceFinder.getResource(R.strings.name, configPrefix);
        nationality = (String) ResourceFinder.getResource(R.strings.nationality, configPrefix);
        currency = (String) ResourceFinder.getResource(R.strings.currency, configPrefix);

        height = (double) ResourceFinder.getResource(R.doubles.height, configPrefix);
        weight = (double) ResourceFinder.getResource(R.doubles.weight, configPrefix);
    }

    @Override
    public String getConfigurationType() {
        return configPrefix;
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
    }
}
