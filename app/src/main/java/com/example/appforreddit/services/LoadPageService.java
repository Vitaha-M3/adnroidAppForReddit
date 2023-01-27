package com.example.appforreddit.services;

import android.app.Activity;
import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appforreddit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class LoadPageService {
    private final String BASE_URL= "https://reddit.com/top.json";
    private String currentURL;
    private final Context superContext;
    private final Activity superActivity;
    private ListView listView;
    private  String nextPage = "";
    private  String previousPage = "";
    private int currentPage;
    private final PaginationService paginationService;

    public LoadPageService(Context context, Activity activity){
        this.superContext = context;
        this.superActivity = activity;
        paginationService = new PaginationService();
    }

    public void loadPage(ListView listView, String pageNavigation, int currentPage){
        this.currentURL = BASE_URL;
        this.listView = listView;
        this.currentPage = currentPage;
        buildLink(pageNavigation, this.currentPage);

        loadPageFromURL();

        if (paginationService.pagesIsEmpty()){
            paginationService.setNextPage(currentPage+1, nextPage);
        }
    }

    private void buildLink(String pageNavigation, int currentPage){
        if(pageNavigation != null){
            StringBuilder stringBuilder= new StringBuilder(BASE_URL);
            switch (pageNavigation){
                case "nextPage":
                    stringBuilder.append("?after=").append(nextPage);
                    stringBuilder.append("&limit=20");
                    currentURL = stringBuilder.toString();
                    break;
                case "previousPage":
                    if(currentPage==1){
                        refreshLoadPage();
                    }else {
                        previousPage = paginationService.getPreviousPage(currentPage-1);
                        stringBuilder.append("?after=").append(previousPage);
                        stringBuilder.append("&limit=20");
                        currentURL = stringBuilder.toString();
                    }
                    break;
            }
        }
    }

    private void loadPageFromURL() {
        final ProgressBar progressBar = superActivity.findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, currentURL,
                response -> {
                    progressBar.setVisibility(ListView.INVISIBLE);
                    try{
                        JSONObject jObj = new JSONObject(Objects.requireNonNull(encodingToUTF8(response)));
                        JSONArray children = jObj.getJSONObject("data").getJSONArray("children");
                        ArrayList<JSONObject> listItems = getArrListFromJSONArr(children);
                        ListAdapter adapter = new AdapterPublicationsService(superContext.getApplicationContext(), R.layout.publication, R.id.author, listItems);
                        nextPage = jObj.getJSONObject("data").getString("after");
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(superContext.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(superContext);
        requestQueue.add(stringRequest);
        if(currentPage > 0){
            paginationService.setNextPage(currentPage+1, nextPage);
        }
    }

    private ArrayList<JSONObject> getArrListFromJSONArr(JSONArray jsonArray) {
        ArrayList<JSONObject> arrList = new ArrayList<>();
        try{
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    arrList.add(jsonArray.getJSONObject(i).getJSONObject("data"));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return arrList;
    }

    private String encodingToUTF8(String response){
        byte[] code = response.getBytes(StandardCharsets.ISO_8859_1);
        response = new String(code, StandardCharsets.UTF_8);
        return response;
    }

    public void refreshLoadPage(){
        currentURL = BASE_URL;
        currentPage = 0;
        previousPage = "";
        loadPageFromURL();
    }
}
