package com.wy.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            for (int i = 0; i < list.length; i++) {
                deleteFile(new File(list[i]));
            }
        }
        file.delete();
    }

    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }
}
