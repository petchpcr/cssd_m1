package com.poseintelligence.cssdm1.core.connect;

import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Login;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.Parameter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HTTPConnect  {
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {

        CssdProject.isNonActiveTime = 0;
        int responseCode = 0;
        URL url;
        String response = "";

        try {
            url = new URL(requestURL);

            Parameter pm = CssdProject.getPm();

            if(pm!=null){
                int B_ID = CssdProject.getPm().getBdCode();
                postDataParams.put("B_ID", B_ID+"");
                String login_token = CssdProject.getPm().getLogin_token();
                postDataParams.put("token", login_token);
            }

            postDataParams.put("p_DB", CssdProject.D_DATABASE);

            //System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn instanceof HttpsURLConnection) {

                SSLContext sslContext = getSSLContext();
                if (sslContext != null) {
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
                }
            }

//            HttpsURLConnection  conn = (HttpsURLConnection ) url.openConnection();
//            SSLContext sslContext = getSSLContext();
//            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection","close");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));


            Log.d("tog_http_F","requestURL = "+requestURL + "?" + getPostDataString(postDataParams));
            System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            try {
                responseCode =
                        conn.getResponseCode();
            }catch (Exception e){

            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();
            } else {
                response = "Error Registering";
            }

            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));
        Log.d("tog_http_F","result = "+response);

        Log.d("tog_http_F","expired_token = "+response.replace(" ","").equals("expired_token"));
        if(response.replace(" ","").equals("expired_token")){
            CssdProject.setExpired_token(true);
        }

        return response;
    }

    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams, int time) {
        CssdProject.isNonActiveTime = 0;
        int responseCode = 0;
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(time);
            conn.setConnectTimeout(time);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection","close");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            try {
                responseCode =
                        conn.getResponseCode();
            }catch (Exception e){

            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();
            } else {
                response = "Error Registering";
            }

            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String sendPostRequestBackground(String requestURL, HashMap<String, String> postDataParams) {

//        Log.d("tog_http_B","1isNonActiveTime = "+CssdProject.isNonActiveTime);
        int responseCode = 0;
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            Parameter pm = CssdProject.getPm();

            if(pm!=null){
                int B_ID = CssdProject.getPm().getBdCode();
                postDataParams.put("B_ID", B_ID+"");
            }

            postDataParams.put("p_DB", CssdProject.D_DATABASE);

            //System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection","close");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            try {
                responseCode =
                        conn.getResponseCode();
            }catch (Exception e){

            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();
            } else {
                response = "Error Registering";
            }

            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d("tog_http_B","Data = "+postDataParams);
//        Log.d("tog_http_B","result = "+response);
//
//        Log.d("tog_http_B","2isNonActiveTime = "+CssdProject.isNonActiveTime);
        return response;
    }

    public String sendPostRequestJson_data(String requestURL, String postDataParams) {
        CssdProject.isNonActiveTime = 0;
        int responseCode = 0;
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            //System.out.println("URL = " + requestURL + "?" + getPostDataString(postDataParams));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection","close");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams);

            writer.flush();
            writer.close();
            os.close();

            try {
                responseCode =
                        conn.getResponseCode();
            }catch (Exception e){

            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();
            } else {
                response = "Error Registering";
            }

            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("tog_","Data = "+postDataParams);
        Log.d("tog_","result = "+response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String ChkPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return result.toString();
    }

    private SSLContext getSSLContext() {
        try {
            InputStream caInput = new BufferedInputStream(CssdProject.getAppContext().getResources().openRawResource(com.poseintelligence.cssdm1.R.raw.siphv_cert));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}