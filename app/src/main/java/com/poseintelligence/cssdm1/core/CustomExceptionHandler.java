package com.poseintelligence.cssdm1.core;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.poseintelligence.cssdm1.core.date.DateTime;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler(Context context) {
        Log.d("tt_log","CustomExceptionHandler");
        this.localPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

        this.url = null;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        String timestamp = DateTime.getDateTime();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
        if (url != null) {
            sendToServer(stacktrace, filename);
        }

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {

            Log.d("tt_log","localPath = "+localPath+ "/" +filename);
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String stacktrace, String filename) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
        try {
            httpPost.setEntity(
                    new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}