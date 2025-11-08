package fa.training.fithub.util;

public class StringUtilsBuild {
    public static boolean isNotBlank(String value) {
        if(value != null && value.isEmpty() == false) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
