package pl.edu.agh.propertree.generator;

public enum Types {
    INTEGERS("integers", 0x01010000),
    DOUBLES("doubles", 0x01020000),
    BOOLEANS("booleans", 0x01030000),
    STRINGS("strings", 0x01040000),
    INTEGER_1D_ARRAYS("string_1d_arrays", 0x01050000),
    DOUBLE_1D_ARRAYS("string_1d_arrays", 0x01060000),
    STRING_1D_ARRAYS("string_1d_arrays", 0x01070000),
    INTEGER_2D_ARRAYS("integer_2d_arrays", 0x01080000),
    DOUBLE_2D_ARRAYS("string_2d_arrays", 0x01090000),
    STRING_2D_ARRAYS("string_2d_arrays", 0x010A0000);

    public final String typeName;
    public final int typeValue;

    Types(String typeName, int typeValue) {
        this.typeName = typeName;
        this.typeValue = typeValue;
    }

    public static String getTypeName(int resourceId) {
        int typeIndex = (resourceId & 0x00FF0000) >> 16;
        return Types.values()[typeIndex - 1].typeName;
    }

    public static Types getType(int resourceId) {
        int typeIndex = (resourceId & 0x00FF0000) >> 16;
        return Types.values()[typeIndex - 1];
    }
}
