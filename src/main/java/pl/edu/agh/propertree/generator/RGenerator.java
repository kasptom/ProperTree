package pl.edu.agh.propertree.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class which generates RHardcoded.java on basing on the configuration files
 */
public class RGenerator {

    public static final List<String> AVAILABLE_TYPES = new ArrayList<>(Arrays.asList(
            "string", "double", "int",
            "string[]", "double[]", "int[]",
            "string[][]", "double[][]", "int[][]",
            "boolean"
    ));

    public static void generateRClass(Map<String, Integer> scanResult) {
        TypeSpec.Builder resourceClassBuilder = TypeSpec.classBuilder("R")
                .addModifiers(Modifier.PUBLIC);

        TypeSpec.Builder stringSubclassBuilder = createSubclassBuilder("strings");
        TypeSpec.Builder doublesSubclassBuilder = createSubclassBuilder("doubles");
        TypeSpec.Builder integersSubclassBuilder = createSubclassBuilder("integers");

//                .addType(TypeSpec.classBuilder("strings")
//                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                        .build())
        for (String key : scanResult.keySet()) {
            Integer id = scanResult.get(key);
            Types type = Types.getType(id);
            TypeSpec.Builder chosenBuilder = null;

            switch (type) {
                case STRINGS:
                    chosenBuilder = stringSubclassBuilder;
                    break;
                case DOUBLES:
                    chosenBuilder = doublesSubclassBuilder;
                    break;
                case INTEGERS:
                    chosenBuilder = integersSubclassBuilder;
                    break;
            }
            if (chosenBuilder != null) {
                chosenBuilder.addField(FieldSpec
                        .builder(TypeName.INT, key, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$L", id)
                        .build());
            }
        }

        TypeSpec resourceClass = resourceClassBuilder
                .addType(stringSubclassBuilder.build())
                .addType(doublesSubclassBuilder.build())
                .addType(integersSubclassBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder("pl.edu.agh.propertree.generated", resourceClass)
                .build();

        try {
            javaFile.writeTo(new File("src/main/java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TypeSpec.Builder createSubclassBuilder(String className) {
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    }
}
