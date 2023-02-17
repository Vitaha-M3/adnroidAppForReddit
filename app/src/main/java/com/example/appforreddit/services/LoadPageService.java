package com.example.appforreddit.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appforreddit.utils.PaginationUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class LoadPageService extends Service {
    private final String BASE_URL = "https://reddit.com/top.json?limit=20";
    private final String TAG = "LoadPageService";
    private final IBinder loadPageBinder = new LoadPageBinder();
    private String currentURL;
    private PaginationUtil paginationUtil;
    private ArrayList<JSONObject> listItems;

    public ArrayList<JSONObject> loadPage(String pageNavigation, int currentPage){
        this.currentURL = BASE_URL;

        buildLink(pageNavigation, currentPage);

        loadDataFromURL(currentPage);
        return listItems;
    }

    private void buildLink(String pageNavigation, int currentPage){
        if(pageNavigation != null){
            StringBuilder stringBuilder= new StringBuilder(BASE_URL);
            switch (pageNavigation){
                case "nextPage":
                    stringBuilder.append("&after=").append(paginationUtil.getPage(currentPage));
                    currentURL = stringBuilder.toString();
                    break;
                case "previousPage":
                    if(currentPage==0){
                        currentURL = BASE_URL;
                        loadDataFromURL(currentPage);
                    }else {
                        stringBuilder.append("&after=").append(paginationUtil.getPage(currentPage));
                        currentURL = stringBuilder.toString();
                    }
                    break;
            }
        }else if (currentPage>0){
            currentURL = currentURL + "&after=" + paginationUtil.getPage(currentPage);
        }
    }

    private void loadDataFromURL(int currentPage){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, currentURL,
                response -> {
                    try{
                        JSONObject jObj = new JSONObject(Objects.requireNonNull(encodingToUTF8(response)));
                        JSONArray children = jObj.getJSONObject("data").getJSONArray("children");
                        getArrListFromJSONArr(children, listItems);
                        paginationUtil.setNextPage(currentPage+1, jObj.getJSONObject("data").getString("after"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getArrListFromJSONArr(JSONArray jsonArray, ArrayList<JSONObject> listItems) {
        listItems.clear();
        try{
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    listItems.add(jsonArray.getJSONObject(i).getJSONObject("data"));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String encodingToUTF8(String response){
        byte[] code = response.getBytes(StandardCharsets.ISO_8859_1);
        response = new String(code, StandardCharsets.UTF_8);
        return response;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "LoadPageService onCreate");
        paginationUtil = new PaginationUtil();
        listItems = new ArrayList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "LoadPageService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "LoadPageService onBind");
        return loadPageBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "LoadPageService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "LoadPageService onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoadPageService onDestroy");
        paginationUtil = null;
        listItems = null;
    }

    public class LoadPageBinder extends Binder {
        public LoadPageService getService() {
            return LoadPageService.this;
        }
    }
}
