package com.chuxin.family.bitmap;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapHelper {


    public static Bitmap getBitmap(String paramString)
    {
        return getBitmap(paramString, 1);
    }

    public static Bitmap getBitmap(String paramString, int paramInt)
    {
        return getBitmap(new File(paramString), paramInt);
    }

    public static Bitmap getBitmap(File paramFile, int paramInt)
    {
        FileInputStream localFileInputStream;
        Bitmap localBitmap = null;
        try {
            localFileInputStream = new FileInputStream(paramFile);
            localBitmap = a(localFileInputStream, paramInt);
            localFileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localBitmap;
    }

    private static Bitmap a(InputStream paramInputStream, int paramInt)
    {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        localOptions.inPurgeable = true;
        localOptions.inInputShareable = true;
        localOptions.inSampleSize = paramInt;
        return BitmapFactory.decodeStream(paramInputStream, null, localOptions);
    }
}
