package com.sageconger.qop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class MainActivity extends Activity implements OnTouchListener{

    private MyAdapter mAdapter;
    private ArrayList<Pledge> pledges = new ArrayList<Pledge>();
    private ArrayList<String> mData = new ArrayList<String>();

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();
        getEvents();
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setDividerHeight(0);
        listView.setDivider(null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long idk){
                Intent intent = new Intent(getApplicationContext(),ViewActivity.class);
                intent.putExtra("pledge", pledges.get(position));
                startActivity(intent);
            }
        });

        Button logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("id");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button plus = findViewById(R.id.addButton);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddActivity.class);
                intent.putExtra("addOrEdit", "add");
                startActivity(intent);
            }
        });
    }

    void getEvents() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,"https://sageandrosemary.com/qop.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("[]")) {
                                Toast.makeText(getApplicationContext(), "No pledges found", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                        Date date = simpleDateFormat.parse(jsonObject.getString("date"));
                                        String separator = jsonObject.getString("campaign_name");
                                        Pledge pledge = new Pledge(jsonObject.getString("id"), jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getString("amount"), jsonObject.getString("frequency"), date, jsonObject.getString("created_by"), new Campaign(jsonObject.getString("campaign_id"), jsonObject.getString("campaign_name")));
                                        if (!mData.contains(separator)) {
                                            mAdapter.addSeparatorItem(separator);
                                            pledges.add(new Pledge());
                                        }
                                        mAdapter.addItem(jsonObject.getString("id"));
                                        pledges.add(pledge);
                                    } catch (ParseException e) {
                                        System.out.println(e);
                                    }
                                }
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
                params.put("query", "select pledges.*, campaigns.name as campaign_name, campaigns.id as campaign_id from pledges join campaigns on pledges.campaign_id = campaigns.id where created_by = " + creator);
                System.out.println(params);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    //Adapter Class
    private class MyAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private LayoutInflater mInflater;

        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        public int getCount() {
            return mData.size();
        }

        public Pledge getItem(int position) {
            return pledges.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.section_list, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.text);
                        holder.textView.setText(pledges.get(position).getName());
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.section_header, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                        holder.textView.setText(mData.get(position));
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView dateView;
    }
}