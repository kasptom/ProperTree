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
 * Class which generates R.java on basing on the configuration files
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

        TypeSpec.Builder integersSubclassBuilder = createSubclassBuilder("integers");
        TypeSpec.Builder doublesSubclassBuilder = createSubclassBuilder("doubles");
        TypeSpec.Builder booleansSubclassBuilder = createSubclassBuilder("booleans");
        TypeSpec.Builder stringSubclassBuilder = createSubclassBuilder("strings");

        TypeSpec.Builder integer1DArraysBuilder = createSubclassBuilder("integer1DArrays");
        TypeSpec.Builder double1DArraysBuilder = createSubclassBuilder("double1DArrays");
        TypeSpec.Builder string1DArraysBuilder = createSubclassBuilder("string1DArrays");

        TypeSpec.Builder integer2DArraysBuilder = createSubclassBuilder("integer2DArrays");
        TypeSpec.Builder double2DArraysBuilder = createSubclassBuilder("double2DArrays");
        TypeSpec.Builder string2DArraysBuilder = createSubclassBuilder("string2DArrays");

        for (String key : scanResult.keySet()) {
            Integer id = scanResult.get(key);
            Types type = Types.getType(id);
            TypeSpec.Builder chosenBuilder = null;

            switch (type) {
                case INTEGERS:
                    chosenBuilder = integersSubclassBuilder;
                    break;
                case DOUBLES:
                    chosenBuilder = doublesSubclassBuilder;
                    break;
                case BOOLEANS:
                    chosenBuilder = booleansSubclassBuilder;
                    break;
                case STRINGS:
                    chosenBuilder = stringSubclassBuilder;
                    break;
                case INTEGER_1D_ARRAYS:
                    chosenBuilder = integer1DArraysBuilder;
                    break;
                case DOUBLE_1D_ARRAYS:
                    chosenBuilder = double1DArraysBuilder;
                    break;
                case STRING_1D_ARRAYS:
                    chosenBuilder = string1DArraysBuilder;
                    break;
                case INTEGER_2D_ARRAYS:
                    chosenBuilder = integer2DArraysBuilder;
                    break;
                case DOUBLE_2D_ARRAYS:
                    chosenBuilder = double2DArraysBuilder;
                    break;
                case STRING_2D_ARRAYS:
                    chosenBuilder = string2DArraysBuilder;
                    break;
            }
            chosenBuilder.addField(FieldSpec
                    .builder(TypeName.INT, key, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", id)
                    .build());
        }

        TypeSpec resourceClass = resourceClassBuilder
                .addType(integersSubclassBuilder.build())
                .addType(doublesSubclassBuilder.build())
                .addType(booleansSubclassBuilder.build())
                .addType(stringSubclassBuilder.build())
                .addType(integer1DArraysBuilder.build())
                .addType(double1DArraysBuilder.build())
                .addType(string1DArraysBuilder.build())
                .addType(integer2DArraysBuilder.build())
                .addType(double2DArraysBuilder.build())
                .addType(string2DArraysBuilder.build())
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
