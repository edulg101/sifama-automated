package gov.antt.sifama.services.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {

    public static boolean getMatch(String text, String reg){

        Pattern pattern = Pattern.compile(reg + "(?!\\d)");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }
}
