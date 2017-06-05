package pl.edu.agh.propertree.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class ReferencesTableGenerator {
    static void prepareReferencesTableFile(String referencesFilePath) {
        if (!prepareReferenceTableFile(referencesFilePath)) {
            throw new RuntimeException("Could not prepare reference table file");
        }
    }

    static void writeReferenceLine(String filePath, int lineNumber, FileWriter fileWriter, Integer id) throws IOException {
        String idHex = "0x" + Integer.toHexString(id);
        String toWrite = String.format("%s;%s;%d\n", idHex, filePath, lineNumber);
        fileWriter.write(toWrite);
    }

    private static boolean prepareReferenceTableFile(String referencesFilePath) {
        File referenceTableFile = new File(referencesFilePath);
        try {
            if (!referenceTableFile.exists()) {
                return referenceTableFile.createNewFile();
            } else {
                return referenceTableFile.delete() && referenceTableFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
