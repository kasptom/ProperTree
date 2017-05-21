package pl.edu.agh.propertree.finder;

class Address {
    private int id;
    private String path;
    private int lineNumber;

    Address(int id, String path, int lineNumber) {
        this.id = id;
        this.path = path;
        this.lineNumber = lineNumber;
    }

    int getId() {
        return id;
    }

    String getPath() {
        return path;
    }

    int getLineNumber() {
        return lineNumber;
    }
}
