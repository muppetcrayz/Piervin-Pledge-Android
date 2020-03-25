package com.sageconger.qop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    List<Integer> ids = new ArrayList<Integer>();
    List<Integer> choices = new ArrayList<Integer>();
    List<String> categories = new ArrayList<String>();
    MultiSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText username = findViewById(R.id.signUpEmail);
        final EditText password = findViewById(R.id.signUpPassword);
        final EditText firstname = findViewById(R.id.signUpFirstName);
        final EditText lastname = findViewById(R.id.signUpLastName);
        final EditText confirm = findViewById(R.id.signUpConfirm);
        final EditText phonenumber = findViewById(R.id.signUpPhoneNumber);
        spinner = findViewById(R.id.selectCampaigns);

        Button signUp = findViewById(R.id.registerButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trySignUp(username.getText().toString(), password.getText().toString(), confirm.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), phonenumber.getText().toString());
            }
        });

        getCampaigns();

    }

    void getCampaigns() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ids.add(jsonObject.getInt("id"));
                                categories.add(jsonObject.getString("name"));
                            }
                            // Spinner Drop down elements
                            spinner.setItems(categories, "Select Campaigns", new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(boolean[] selected) {
                                    for (int i=0; i<selected.length; i++) {
                                        if (selected[i] == true) {
                                            choices.add(ids.get(i));
                                        }
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
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
                params.put("query", "SELECT * FROM campaigns");
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);

    }

    void trySignUp(final String username, final String password, final String confirm, final String firstname, final String lastname, final String phonenumber) {
        if (!password.equals(confirm)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getID();
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
                params.put("query", "INSERT INTO users (username, password, first_name, last_name, phone_number) VALUES ('" + username + "', PASSWORD('" + password + "'), '" + firstname + "', '" + lastname + "', '" + phonenumber + "')");
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);

    }

    void getID() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            setCampaigns(array.getJSONObject(0).getString("maxID"));
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
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
                params.put("query", "select coalesce(max(id)) as maxID from users");
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }

    void setCampaigns(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Iterator<Integer> it = choices.iterator();
        while(it.hasNext())
        {
            Integer obj = it.next();
            final StringRequest jsonObjRequest = new StringRequest(

                    Request.Method.POST,"https://sageandrosemary.com/qop.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!it.hasNext()) {
                                finish();
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
                    params.put("query", "insert into users_campaigns(user_id, campaign_id) VALUES (" + id + ", " + obj + ")");
                    System.out.println(params);
                    return params;
                }

            };
            requestQueue.add(jsonObjRequest);
            //Do something with obj
        }
    }

}
