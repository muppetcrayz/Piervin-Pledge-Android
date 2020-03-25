package com.sageconger.qop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

public class ViewActivity extends AppCompatActivity {

    private ArrayList<Payment> payments = new ArrayList<>();
    private MyAdapter mAdapter;
    Pledge e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Context context = this;

        TextView date = findViewById(R.id.dateText);
        e = getIntent().getParcelableExtra("pledge");
        Date datey = e.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm aa");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String datetime = simpleDateFormat.format(datey);

        ListView littleList = findViewById(R.id.littleList);
        mAdapter = new MyAdapter();
        getPayments();
        littleList.setAdapter(mAdapter);

        Button edit = findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("addOrEdit", "edit");
                intent.putExtra("pledge", e);
                startActivity(intent);
            }
        });

        date.setText(datetime);

        TextView name = findViewById(R.id.nameText);
        name.setText(e.getName());

        TextView description = findViewById(R.id.descriptionText);
        description.setText(e.getDescription());

        TextView amount = findViewById(R.id.amountText);
        amount.setText(e.getAmount());

        TextView frequency = findViewById(R.id.frequencyText);
        frequency.setText(e.getFrequency());

        TextView campaign = findViewById(R.id.campaignText);
        TextView campaignLabel = findViewById(R.id.campaignLabel);
        if (e.getCampaign() == null) {
            campaign.setVisibility(View.INVISIBLE);
            campaignLabel.setVisibility(View.INVISIBLE);
        }
        else {
            campaign.setText(e.getCampaign().getName());
        }
    }

    void getPayments() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("[]")) {

                            }
                            else {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                        Date date = simpleDateFormat.parse(jsonObject.getString("date"));
                                        Payment payment = new Payment(date, jsonObject.getDouble("amount"), jsonObject.getDouble("balance"), jsonObject.getString("format"));
                                        payments.add(payment);
                                    } catch (ParseException e) {
                                        System.out.println(e);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
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
                        Toast.makeText(getApplicationContext(), "Timeout error, try again", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
                Integer creator = sharedPreferences.getInt("id", 0);
                params.put("query", "select * from payments where pledge_id = " + e.getId());
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }

    //Adapter Class
    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return payments.size();
        }

        public Payment getItem(int position) {
            return payments.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                        convertView = mInflater.inflate(R.layout.payment, null);
                        holder.paymentDate = (TextView)convertView.findViewById(R.id.paymentDate);
                holder.paymentAmount = (TextView)convertView.findViewById(R.id.paymentAmount);
                holder.paymentBalance = (TextView)convertView.findViewById(R.id.paymentBalance);
                holder.paymentFormat = (TextView)convertView.findViewById(R.id.paymentFormat);

                Payment payment = payments.get(position);
                Date datey = payment.getDate();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy");
                simpleDateFormat.setTimeZone(TimeZone.getDefault());
                String datetime = simpleDateFormat.format(datey);

                holder.paymentDate.setText(datetime);
                holder.paymentAmount.setText("$" + payment.getAmount().toString());
                holder.paymentBalance.setText("$" + payment.getBalance().toString());
                holder.paymentFormat.setText(payment.getFormat());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView paymentDate, paymentAmount, paymentBalance, paymentFormat;
    }

}
