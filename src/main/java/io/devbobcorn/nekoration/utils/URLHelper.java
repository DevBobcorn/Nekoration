package io.devbobcorn.nekoration.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class URLHelper {
    public static boolean isURL(String str) {
        /*
         * A plain file name (or any relative path) is also a valid relative
         * URI, so parsing alone is not enough: only treat the string as a URL
         * when it is an absolute URI with an http(s) scheme.
         */
        try {
            URI uri = new URI(str);
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"));
        }
        // If there was an Exception
        // while creating URI object
        catch (URISyntaxException e) {
            return false;
        }
    }
}
