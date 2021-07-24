package com.partybuild.boxindex_cmcc;


import android.os.Build;

import java.lang.reflect.Method;

public class AndroidUtils {

    public static String getSN() {
        String serial = "";
        try {
            serial = android.os.Build.SERIAL;
            if (!serial.equals("")&&!serial.equals("unknown"))return serial;
        }catch (Exception e){
            serial="";
        }

        try {
            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");
            if (!serial.equals("")&&!serial.equals("unknown"))return serial;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)serial = Build.getSerial();
        } catch (Exception e) {
            serial="";
        }
        return serial;
    }
}