package com.simpla.turnedTables.Utils;

import java.text.DecimalFormat;

public class DoubleFormat {

    public static String setFormat(double number){
        DecimalFormat precision = new DecimalFormat("0.0");
        return precision.format(number);
    }
}
