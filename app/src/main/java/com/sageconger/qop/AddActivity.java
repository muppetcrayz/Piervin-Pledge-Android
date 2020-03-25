package com.sageconger.qop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AddActivity extends AppCompatActivity {

    String frequency;
    List<Campaign> campaigns = new ArrayList<>();
    String chosenCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        Integer creator = sharedPreferences.getInt("id", 0);
        String which = getIntent().getStringExtra("addOrEdit");
        Pledge e = getIntent().getParcelableExtra("pledge");

        getCampaigns();

        if (e != null && (Integer.parseInt(e.getCreator()) != creator)) {
            Toast.makeText(this, "You are not the creator of this event, so you can't edit it.", Toast.LENGTH_SHORT).show();
            finish();
        }

        EditText nameField = findViewById(R.id.nameField);
        EditText descriptionField = findViewById(R.id.descriptionField);
        EditText amountField = findViewById(R.id.amountField);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequency = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                frequency = "weekly";
            }
        });

        Button addButton = findViewById(R.id.addButton);

        if (which.equals("edit")) {
            nameField.setText(e.getName());
            descriptionField.setText(e.getDescription());
            amountField.setText(e.getAmount());
            if (e.getFrequency() == "weekly") {
                spinner.setSelection(0);
            }
            else if (e.getFrequency() == "monthly") {
                spinner.setSelection(1);
            }
            else {
                spinner.setSelection(2);
            }
            addButton.setText("Edit");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edit(e, nameField.getText().toString(), descriptionField.getText().toString(), amountField.getText().toString(), frequency);
                }
            });
        }
        else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add(nameField.getText().toString(), descriptionField.getText().toString(), amountField.getText().toString(), frequency);
                }
            });
        }
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
                                JSONObject object = jsonArray.getJSONObject(i);
                                campaigns.add(new Campaign(object.getString("id"), object.getString("name")));
                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                        String campaign[] = new String[campaigns.size()];
                        for (int i=0; i < campaigns.size(); i++) {
                            campaign[i] = campaigns.get(i).getName();
                        }
                        Spinner campaignSpinner = findViewById(R.id.campaignSpinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item, campaign);
                        campaignSpinner.setAdapter(adapter);
                        campaignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                chosenCampaign = campaigns.get(i).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                chosenCampaign = "";
                            }
                        });

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
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                Integer creator = sharedPreferences.getInt("id", 0);
                params.put("query", "select campaigns.* from campaigns join users_campaigns on campaigns.id = users_campaigns.campaign_id where user_id = " + creator);
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }

    void add(String name, String description, String amount, String frequency) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                Integer creator = sharedPreferences.getInt("id", 0);
                params.put("query", "insert into pledges(name, description, amount, frequency, date, created_by) VALUES ('" + name + "', '" + description + "', " + amount + ", '" + frequency + "', NOW(), " + creator + ")");
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }

    void edit(Pledge e, String name, String description, String amount, String frequency) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                params.put("query", "UPDATE pledges SET name = '" + name + "', description = '" + description + "', amount = '" + amount + "', frequency = '" + frequency + "' WHERE id = " + e.getId());
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }
}
