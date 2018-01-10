package com.rahbod.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class TestVollyActivity extends AppCompatActivity {

    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_volly);
        email = (EditText) findViewById(R.id.email);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthorize();
            }
        });

    }


    private void getAuthorize(){
        JSONObject params = new JSONObject();
        try {
            params.put("email", email.getText().toString());
            params.put("password", "masoud1387");
            AppController.getInstance().sendRequest("oauth/authorize", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getBoolean("status")) {
                            gerAccessToken(response.getString("authorization_code"));
                        }else
                            Toast.makeText(TestVollyActivity.this, "Error in get Authorization Code.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gerAccessToken(String ac){
        JSONObject params = new JSONObject();
        try {
            params.put("authorization_code", ac);
            params.put("grant_type", "access_token");
            AppController.getInstance().sendRequest("oauth/token", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getBoolean("status")) {
                            // save in shared preference
                            JSONObject token = response.getJSONObject("token");
                            token.getString("access_token");
                            token.getString("refresh_token");
                            token.getInt("expire_in");
                        }else
                            Toast.makeText(TestVollyActivity.this, "Error in get Token.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshAccessToken(String rt){
        JSONObject params = new JSONObject();
        try {
            params.put("refresh_token", rt);
            params.put("grant_type", "refresh_token");
            AppController.getInstance().sendRequest("oauth/token", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getBoolean("status")) {
                            // update in shared preference
                            JSONObject token = response.getJSONObject("token");
                            token.getString("access_token");
                            token.getInt("expire_in");
                        }else
                            Toast.makeText(TestVollyActivity.this, "Error in Refresh Token.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
