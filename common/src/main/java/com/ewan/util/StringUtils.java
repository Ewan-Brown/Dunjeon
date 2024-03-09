package com.ewan.util;

import org.dyn4j.geometry.Vector2;

import java.text.DecimalFormat;

public class StringUtils {
    final private static DecimalFormat twoDecimalsKept = new DecimalFormat("#.0");
    final private static DecimalFormat allDecimalsKept = new DecimalFormat("#.0000000000000000");
    public static String formatVector(Vector2 v){
        if(v == null)
            return "(null)";
        return String.format("(%s, %s)", twoDecimalsKept.format(v.x), twoDecimalsKept.format(v.y));
    }
    public static String formatVectorFullPrecision(Vector2 v){
        return String.format("(%s, %s)", allDecimalsKept.format(v.x), allDecimalsKept.format(v.y));
    }
    public static String formatVectorDiff(Vector2 v1, Vector2 v2){
        return String.format("(%s, %s) -> (%s, %s)", twoDecimalsKept.format(v1.x), twoDecimalsKept.format(v1.y), twoDecimalsKept.format(v2.x), twoDecimalsKept.format(v2.y));
    }
}
