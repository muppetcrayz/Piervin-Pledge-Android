package com.sageconger.qop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = findViewById(R.id.loginUsername);
        final EditText password = findViewById(R.id.loginPassword);

        Button logIn = findViewById(R.id.loginButton);
        Button signUp = findViewById(R.id.signUpButton);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin(username.getText().toString(), password.getText().toString());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    void tryLogin(final String username, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            if (response.equals("[]")) {
                                Toast.makeText(getApplicationContext(), "Username or password is incorrect, try again", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("id", Integer.parseInt(jsonObject.getString("id")));
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e) {
                            System.out.println(e);
                            Toast.makeText(getApplicationContext(), "JSON parse error, try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("query", "SELECT * FROM users WHERE username = '" + username + "' AND password = PASSWORD('" + password + "')");
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);

    }

}
