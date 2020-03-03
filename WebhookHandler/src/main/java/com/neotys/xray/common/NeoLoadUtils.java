package com.neotys.xray.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NeoLoadUtils {

    public static String convertDateLongToString(long longdate)
    {
        Date date=new Date(longdate);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd hh:mm:ss.S");
        return df2.format(date);
    }
}
