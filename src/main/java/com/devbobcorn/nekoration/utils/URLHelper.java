package com.devbobcorn.nekoration.utils;

import java.net.URL;

public class URLHelper {
    public static boolean isURL(String str){
        /* Try creating a valid URL */
        try {
            new URL(str).toURI();
            return true;
        }
            
        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }
}
