package pl.edu.agh.propertree.finder;

public class Address {
    private int id;
    private String path;
    private int lineNumber;

    public Address(int id, String path, int lineNumber) {
        this.id = id;
        this.path = path;
        this.lineNumber = lineNumber;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
