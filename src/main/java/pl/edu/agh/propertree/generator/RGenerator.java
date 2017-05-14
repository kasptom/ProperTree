package pl.edu.agh.propertree.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;

/**
 * Class which generates RHardcoded.java on basing on the configuration files
 */
public class RGenerator {

    public static void generateRClass() {
        TypeSpec resourceClass = TypeSpec.classBuilder("RGen")
                .addModifiers(Modifier.PUBLIC)
                .build();

        JavaFile javaFile = JavaFile.builder("pl.edu.agh.propertree.generated", resourceClass)
                .build();

        try {
            javaFile.writeTo(new File("src/main/java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
