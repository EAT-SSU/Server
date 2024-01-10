package ssu.eatssu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponseStatus;

public class DateUtils {

    private DateUtils() { }

    public static Date toDate(String date) throws BaseException {
        try {
            return DateUtils.toDate("yyyyMMdd", date);
        } catch (ParseException e) {
            throw new BaseException(BaseResponseStatus.INVALID_DATE);
        }
    }

    public static Date toDate(String dateFormat, String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.parse(dateString);
    }
}
