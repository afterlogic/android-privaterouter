package com.PrivateRouter.PrivateMail.view.utils;

import android.os.Environment;

import java.io.File;

public class PathUtils {
    public static File getUniqueFile (String name) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + name);
        int fileIndex = 1;
        while (file.exists())  {
            int index = name.lastIndexOf(".");
            String ext = "";
            String clearName= name;
            if (index!=-1) {
                clearName =  name.substring(0, index);
                ext = name.substring(index+1);
            }

            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    File.separator + clearName +"(" + fileIndex+")."+ext;
            fileIndex++;
            file = new File(fileName );
        }

        return file;
    }
}
