package ssu.eatssu.utils;

import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponseStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date toDate(String dateFormat, String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.parse(dateString);
    }
}
