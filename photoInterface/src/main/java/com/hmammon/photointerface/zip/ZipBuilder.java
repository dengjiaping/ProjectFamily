package com.hmammon.photointerface.zip;

import android.content.Context;


import com.hmammon.photointerface.ZedLog;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZIPBuildre类
 * Created by Xcfh on 2014/10/20.
 */
public class ZipBuilder {

    private ZipOutputStream zos;

    private ArrayList<File> filesList;

    private File targetFile;

    public ZipFile zipFile;

    private int bufSize = 512;

    private ZipBuilder(Context context) {
        filesList = new ArrayList<>();
        StringBuilder fileName = new StringBuilder(UUID.randomUUID().toString());
        int i;
        while ((i = fileName.indexOf("-")) != -1) {
            fileName.deleteCharAt(i);
        }
        fileName.append(".zip");
        targetFile = new File(context.getExternalFilesDir(null) + File.separator + fileName.toString());
        ZedLog.i(this, "TargetFilePath = " + targetFile.getPath());
        try {
            if (!targetFile.exists())
                ZedLog.i(this, "CreateTargetFileResult = " + targetFile.createNewFile());
            else {
                targetFile.delete();
                ZedLog.i(this, "CreateTargetFileResult = " + targetFile.createNewFile());
            }
            zos = new ZipOutputStream(new FileOutputStream(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(Deflater.DEFAULT_COMPRESSION);
    }

    public ZipBuilder setBufferdSize(int size) {
        bufSize = size;
        return this;
    }

    public static ZipBuilder create(Context context) {
        return new ZipBuilder(context);
    }

    public ZipBuilder add(File file) {
        if (file.exists() && !file.isDirectory()) {
            ZedLog.i(this, "Add file Success!File size is " + file.length());
            filesList.add(file);
        } else {
            ZedLog.e(this, "Add file Failed!");
        }
        return this;
    }

    public ZipBuilder add(File[] files) {
        for (File file : files) add(file);
        return this;
    }

    public File zip() {
        if (filesList.size() == 0) return null;
        try {
            for (File file : filesList) {
                zip(file);
            }
            zos.close();
            zipFile = new ZipFile(targetFile);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return targetFile;
    }

    private void zip(File file) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        ZipEntry ze = new ZipEntry(file.getName());
        zos.putNextEntry(ze);
        byte[] buffer = new byte[bufSize];
        int byteRead;
        while ((byteRead = dis.read(buffer)) != -1) {
            zos.write(buffer, 0, byteRead);
        }
        zos.flush();
        dis.close();
    }

}
