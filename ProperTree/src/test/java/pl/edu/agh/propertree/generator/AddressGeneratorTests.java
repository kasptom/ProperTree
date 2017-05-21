package pl.edu.agh.propertree.generator;

import org.junit.Test;
import pl.edu.agh.propertree.generator.AddressGenerator;

import static junit.framework.TestCase.assertEquals;

public class AddressGeneratorTests {
    @Test
    public void stringDouble_hasInt_returnFalse() {
        String stringDouble = "1.0";

        boolean result = AddressGenerator.hasInteger(stringDouble);

        assertEquals(false, result);
    }
}
