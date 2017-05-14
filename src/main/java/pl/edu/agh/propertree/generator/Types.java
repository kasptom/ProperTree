package pl.edu.agh.propertree.generator;

public enum Types {
    STRINGS("strings"), INTEGERS("integers"), DOUBLES("doubles"),

    STRING_1D_ARRAYS("string_1d_arrays"), INTEGER_1D_ARRAYS("string_1d_arrays"), DOUBLE_1D_ARRAYS("string_1d_arrays"),

    STRING_2D_ARRAYS("string_2d_arrays"), INTEGER_2D_ARRAYS("integer_2d_arrays"), DOUBLE_2D_ARRAYS("string_2d_arrays"),

    BOOLEANS("booleans");

    public final String typeName;

    Types(String typeName) {
        this.typeName = typeName;
    }

    public static String getTypeName(int resourceId) {
        int typeIndex = (resourceId & 0x00FF0000) >> 16;
//        System.out.println(String.format("type index: %d", typeIndex));
        return Types.values()[typeIndex - 1].typeName;
    }

    public static Types getType(int resourceId) {
        int typeIndex = (resourceId & 0x00FF0000) >> 16;
        return Types.values()[typeIndex - 1];
    }
}
