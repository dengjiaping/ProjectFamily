package com.chuxin.family.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.chuxin.family.utils.CxImageFetcher.Request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public interface DiskCache {

    public boolean exists(String key);

    public File getFile(String key);

    public InputStream getInputStream(String key) throws IOException;

    public void store(Request request, InputStream is, boolean isImage);

    public void invalidate(String key);

    public void cleanup();

    public void clear();
    
    public void clearMemory();
    
    public boolean existAtMemory(String key);
    
    public BitmapDrawable getImageFromMemory(String key);
    
    public BitmapDrawable getExtralBitmapDrawable(String key, View loaderView);

}
