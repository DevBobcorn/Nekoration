package com.devbobcorn.nekoration.client.rendering;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LocalImageLoader {
    public static byte[] read(String name){
        InputStream input = null;
        ByteArrayOutputStream arrStream = new ByteArrayOutputStream();

        try {
            input = new FileInputStream(name);
            byte[] arr = new byte[4 * 128 * 128];
            int len = -1;
            while ((len = input.read(arr)) != -1){
                arrStream.write(arr, 0, len);
            }
            return arrStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
