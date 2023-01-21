package com.example.appforreddit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://reddit.com/top.json";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        loadJSONFromURL(JSON_URL);
    }

    private void loadJSONFromURL(String url){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(ListView.INVISIBLE);
                        try{
                            JSONObject jObj = new JSONObject(Objects.requireNonNull(encodingToUTF8(response)));
                            JSONArray children = jObj.getJSONObject("data").getJSONArray("children");
                            ArrayList<JSONObject> listItems = getArrListFromJSONArr(children);
                            ListAdapter adapter = new AdapterPublications(getApplicationContext(), R.layout.row, R.id.author, listItems);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringReq);
    }

    private ArrayList<JSONObject> getArrListFromJSONArr(JSONArray jsonArray){
        ArrayList<JSONObject> arrList = new ArrayList<>();
        try{
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    arrList.add(jsonArray.getJSONObject(i));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return arrList;
    }

    private String encodingToUTF8(String response){
        try {
            byte[] code = response.getBytes("ISO-8859-1");
            response = new String(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}