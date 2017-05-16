package pl.edu.agh.propertree.finder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ReferencesParser {
    private static final String REFERENCES_TABLE_PATH = "generated/gen_ref_tab";

    static List<Address> parse() {
        List<Address> addresses = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(REFERENCES_TABLE_PATH);
            BufferedReader buffer = new BufferedReader(fileReader);

            String line;
            String[] attrs;
            while ((line = buffer.readLine()) != null) {
                attrs = line.split(";");
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                int id = Integer.parseInt(attrs[0].trim().substring(2), 16);
                String path = attrs[1].trim();
                int lineNumber = Integer.parseInt(attrs[2]);

                addresses.add(new Address(id, path, lineNumber));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}
