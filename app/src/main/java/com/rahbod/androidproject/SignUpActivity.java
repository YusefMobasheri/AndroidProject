package com.rahbod.androidproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity {
    ProgressDialog pd;
    EditText etUsername;
    EditText etPass;
    EditText etName;
    EditText etMobile;
    TextView tvError;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        pd = new ProgressDialog(SignUpActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        etUsername = (EditText) findViewById(R.id.email);
        etPass = (EditText) findViewById(R.id.password);
        etName = (EditText) findViewById(R.id.etName);
        etMobile = (EditText) findViewById(R.id.etMobile);
        tvError = (TextView) findViewById(R.id.tvError);
        btnSend= (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected())
                    Toast.makeText(SignUpActivity.this, "No internet access. Please check it.", Toast.LENGTH_LONG).show();
                else {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("username", etUsername.getText().toString());
                        params.put("password", etPass.getText().toString());
                        params.put("name", etName.getText().toString());
                        params.put("mobile", etMobile.getText().toString());
                        String response = new RestTask("http://rahbod.ir/projects/android/api.php?action=register",
                                "POST", params).execute().get();
                        JSONObject json = new JSONObject(response);
                        if(!json.getBoolean("status")){
                            Integer errorCode = json.getInt("errorCode");
                            tvError.setText(handleError(errorCode));
                        }else {
                            tvError.setTextColor(Color.GREEN);
                            Toast.makeText(SignUpActivity.this, "Sign Up successful. Please Login ...", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (InterruptedException e) {
                        Log.e("Er-i", e.getMessage());
                    } catch (ExecutionException e) {
                        Log.e("Er-e", e.getMessage());
                    } catch (JSONException e) {
                        Log.e("Er-j", e.getMessage());
                    }
                }
            }
        });
    }

    private class RestTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String method;
        private JSONObject params;

        RestTask(String url, String method, JSONObject params) {
            this.url = url;
            this.method = method;
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(false);

                if (params != null) {
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(params.toString().getBytes("UTF-8"));
                    os.close();
                }

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() && cm.getActiveNetworkInfo().isAvailable();
    }

    private String handleError(Integer errorCode){
        String message;
        switch (errorCode){
            case -1:
                message = "Database Connection Error.";
                break;
            case -2:
                message = "Action is not defined.";
                break;
            case -3:
                message = "Username and Password can not be empty.";
                break;
            case -4:
                message = "Username or Password invalid.";
                break;
            case -5:
                message = "Action invalid.";
                break;
            case -6:
                message = "Please fill all * fields.";
                break;
            case -7:
                message = "Oops! Registration unsuccessful. please try again.";
                break;
            case 0:
            default:
                message = "";
        }
        return message;
    }
}
