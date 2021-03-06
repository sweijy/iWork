package connect.utils.data;

import android.text.TextUtils;

import java.text.DecimalFormat;

public class RateFormatUtil {

    /** BTC (decimal turn o tLong) */
    public static final double BTC_TO_LONG = Math.pow(10,8);
    /** Other currency input formats */
    public static final String PATTERN_OTHER = "########0.00";
    /** Bitcoin input format */
    public static final String PATTERN_BTC = "##0.00000000";

    public static String foematNumber(String pattern,double value){
        DecimalFormat myformat = new DecimalFormat(pattern);
        String data = myformat.format(value);
        return data.replace(",", ".");
    }

    public static long stringToLongBtc(String value){
        if (TextUtils.isEmpty(value)) {
            return 0;
        } else {
            return doubleToLongBtc(Double.valueOf(value));
        }
    }

    public static long doubleToLongBtc(double value){
        return Math.round(value * BTC_TO_LONG);
    }

    public static String longToDoubleBtc(long value) {
        DecimalFormat myformat = new DecimalFormat(PATTERN_BTC);
        String format = myformat.format(value / BTC_TO_LONG);
        return format.replace(",", ".");
    }

    public static double longToDouble(long value) {
        return value / BTC_TO_LONG;
    }
}
