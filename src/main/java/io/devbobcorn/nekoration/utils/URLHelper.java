package io.devbobcorn.nekoration.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class URLHelper {
    public static boolean isURL(String str) {
        /* Try creating a valid URI */
        try {
            new URI(str);
            return true;
        }
        // If there was an Exception
        // while creating URI object
        catch (URISyntaxException e) {
            return false;
        }
    }
}
