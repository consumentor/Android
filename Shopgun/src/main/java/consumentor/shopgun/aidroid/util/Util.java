package consumentor.shopgun.aidroid.util;

/**
 * Created by Simon on 18.09.13.
 */
public class Util {

    public static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }

    public static CharSequence trim(CharSequence s) {
        int start = 0;
        int end = s.length() -1;
        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }

        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }

        return s.subSequence(start, end);
    }
}
