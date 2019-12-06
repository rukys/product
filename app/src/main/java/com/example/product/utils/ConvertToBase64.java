package com.example.product.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Business Development RDS
 * 18-03-2019
 */

public class ConvertToBase64 {
    public String bitmapToBase64(Bitmap bitmap, String format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (format.equals("PNG")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        }
        if (format.equals("JPEG")) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
