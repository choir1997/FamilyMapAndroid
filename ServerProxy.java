package edu.byu.cs240.familymap.familymapclient;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;

import Request.LoginRequest;
import Request.RegisterRequest;

public class ServerProxy {

    public static String login(String serverHost, String serverPort, LoginRequest loginRequest) throws Exception {
        try {
            String respData;
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.setRequestProperty("Content-Type", "application/json; utf-8");

            http.setRequestProperty("Accept", "application/json");

            http.setAllowUserInteraction(true);

            //connect to server and send the http request and receive response

            http.connect();

            Gson gson = new Gson();

            //todo: need to get request body from user
            String reqData = gson.toJson(loginRequest);

            OutputStream reqBody = http.getOutputStream();

            writeString(reqData, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                respData = readString(resBody);
            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                respData = readString(resBody);
            }

            return respData;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error in background task");
        }
    }

    public static String register(String serverHost, String serverPort, RegisterRequest registerRequest) throws Exception {
        try {
            String respData;
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.setRequestProperty("Content-Type", "application/json; utf-8");

            http.setRequestProperty("Accept", "application/json");

            http.setAllowUserInteraction(true);

            //connect to server and send the http request and receive response

            http.connect();

            Gson gson = new Gson();

            //todo: need to get request body from user
            String reqData = gson.toJson(registerRequest);

            OutputStream reqBody = http.getOutputStream();

            writeString(reqData, reqBody);


            reqBody.close();

            //todo: fix http.getResponseCode() --> keeps giving error
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                respData = readString(resBody);
            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                respData = readString(resBody);
            }

            return respData;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error in background task register");
        }
    }

    public static String getEvents(String serverHost, String serverPort, String authToken) throws Exception {
        try {
            String respData;
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.setRequestProperty("Authorization", authToken);

            http.setRequestProperty("Content-Type", "application/json; utf-8");

            http.setRequestProperty("Accept", "application/json");

            http.setAllowUserInteraction(true);

            //connect to server and send the http request and receive response

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                respData = readString(resBody);
            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                respData = readString(resBody);
            }

            return respData;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error in server proxy get events");
        }
    }

    public static String getPeople(String serverHost, String serverPort, String authToken) throws Exception {
        try {
            String respData;
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.setRequestProperty("Authorization", authToken);

            http.setRequestProperty("Content-Type", "application/json; utf-8");

            http.setRequestProperty("Accept", "application/json");

            //connect to server and send the http request and receive response

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                respData = readString(resBody);
            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                respData = readString(resBody);
            }

            return respData;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error in server proxy get People");
        }
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
