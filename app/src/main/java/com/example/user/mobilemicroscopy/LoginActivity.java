package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity {

    private static final String SIMPLE_USER_WEB_API_URL = "http://ec2-13-210-117-22.ap-southeast-2.compute.amazonaws.com/api/users.php";

    /**
     * The username field
     */
    EditText usernameEditText;

    /**
     * The password field
     */
    EditText passwordEditText;

    /**
     * The login button
     */
    Button loginButton;

    /**
     * The guest login button
     */
    Button guestLoginButton;

    /**
     * hold the username
     */
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide(); // hide the action bar

        // find the fields
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        // find login button
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

        // find guest login button
        guestLoginButton = (Button) findViewById(R.id.guest_login_button);

        // specify the action when the button is clicked
        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to login using a query against AWS RDS through a simple web API
     */
    public void login() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

//            String webServerUrl = "http://ec2-13-210-117-22.ap-southeast-2.compute.amazonaws.com/api/users.php";

//                Uri imageUri = Uri.parse(webServerUrl);
//                Intent webIntent = new Intent(Intent.ACTION_VIEW, imageUri);
//
//                startActivity(webIntent);

            // make a background task to login
            LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
            loginAsyncTask.execute(SIMPLE_USER_WEB_API_URL);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            Log.d("AAAAAAAAAAAAAAAAAAAA", "bbbbbbbbbbbbbbbbbb");
        }
    }

    /**
     * Class to login in background
     */
    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse = "";
            InputStream inputStream = null;
            OutputStream outputStream = null;

            username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            URL url = null;
            try {
                url = new URL(strings[0]);
                Log.d("AAAAAAAAAAAAAAAAa", strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password);
                String query = builder.build().getEncodedQuery();


                outputStream = connection.getOutputStream();

                BufferedWriter bufferedWriter = null;
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                bufferedWriter.write(query);
                bufferedWriter.flush();
                bufferedWriter.close();

                outputStream.close();

                connection.connect();
                Log.d("AAAAAAAAAAAAAAAAAA", connection.getResponseCode() + "");

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                    jsonResponse = readInputStream(inputStream);
                    Log.d("AAAAAAAAAAAAAAAAAAbbb", jsonResponse);
//                    extractFeatureFromJson(jsonResponse);
                } else {
                    Log.e("AAAAAAAAAAAAA", "Error response code: " + connection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);

            extractStatus(jsonResponse);
        }

        /**
         * Read information from input stream
         *
         * @param inputStream
         * @return
         * @throws IOException
         */
        private String readInputStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Extract information from a JSON response
         *
         * @param jsonResponse
         */
        private void extractStatus(String jsonResponse) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(jsonResponse)) {
                return;
            }

            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray records = root.getJSONArray("records");

                if (records.length() != 0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    Log.e("AAAAAAAAAAAAAAAAAAA", username);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else if (records.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Username and password not match. Please try again", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e("AAAAAAAAAAAAAAAAAAA", "Problem parsing the earthquake JSON results", e);
            }
        }
    }
}
