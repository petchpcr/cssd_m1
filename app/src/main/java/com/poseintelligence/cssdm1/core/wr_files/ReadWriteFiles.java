package com.poseintelligence.cssdm1.core.wr_files;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ReadWriteFiles {

    public static void writeFile(ContentResolver resolver,String relativePath,String displayName, String content) {
        Uri uri = findFileUri(resolver, displayName, relativePath);

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, displayName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativePath);
            uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
        }

        if (uri != null) {
            try (OutputStream outStream = resolver.openOutputStream(uri, "wa")) {
                if (outStream != null) {
                    outStream.write(content.getBytes());
                    outStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeLogFile(ContentResolver resolver,String displayName, String content) {

        final String relativePath = Environment.DIRECTORY_DOCUMENTS + "/Cssd M1 Log";

        ReadWriteFiles.writeFile(resolver,relativePath,displayName,content);

    }

    private static Uri findFileUri(ContentResolver resolver, String displayName, String relativePath) {
        String[] projection = {MediaStore.Files.FileColumns._ID};
        String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "=? AND "
                + MediaStore.Files.FileColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = {displayName, relativePath + "/"};

        try (Cursor cursor = resolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), String.valueOf(id));
            }
        }

        return null;
    }
}
