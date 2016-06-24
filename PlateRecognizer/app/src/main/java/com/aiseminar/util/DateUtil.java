package com.aiseminar.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ares on 6/18/16.
 */
public class DateUtil {
    public static String getDateFormatString(Date date) {
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd_HHmmss");
            dateString = format.format(date);
        }

        return dateString;
    }
}
