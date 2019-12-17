package ru.devpav.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtil {

    private static final String urlPattern = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";


    public static String substringRegexp(String text) {
        final Pattern pattern = Pattern.compile(urlPattern);

        final Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            text = text.substring(matcher.start(), matcher.end());
        }

        return text;
    }


}
