package me.bokov.bsc.surfaceviewer.surfacelang;

public enum SurfaceLangPrimitive {

    FLOAT("float"),
    INT("int"),
    SDF3D("sdf3d");

    public final String codeName;

    SurfaceLangPrimitive(String codeName) {
        this.codeName = codeName;
    }

    public static boolean isValidPrimitiveType(String typeName) {
        for(var prim : values()) {
            if (prim.codeName.equals(typeName)) {
                return true;
            }
        }

        return false;
    }

    public static SurfaceLangPrimitive primitiveForName(String typeName) {
        for(var prim : values()) {
            if (prim.codeName.equals(typeName)) return prim;
        }
        return null;
    }

}
