package com.example.appforreddit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.appforreddit.R;
import com.example.appforreddit.adapters.AdapterPublications;
import com.example.appforreddit.services.LoadPageService;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MAIN ACTIVITY";
    private SwipeRefreshLayout reLoadReddit;
    private ProgressBar progressBar;
    private TextView currPage;
    private ListView listView;
    private AdapterPublications adapter;
    private ArrayList<JSONObject> listPublications;
    private LoadPageService loadPageService;
    private Intent intent;
    private Button prevButton, nextButton;
    private int currentPage = 0;
    private Parcelable listViewState;
    Boolean serviceConnected = false;
    private final ServiceConnection connectionLoadPageService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service connection is CONNECTED");
            loadPageService = ((LoadPageService.LoadPageBinder) iBinder).getService();
            loadPage(null);
            serviceConnected = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service connection is DISCONNECTED");
            serviceConnected = false;
        }
    };

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listView.addFooterView(getLayoutInflater().inflate(R.layout.footer_button, null, false));
        reLoadReddit = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);
        prevButton = listView.findViewById(R.id.previousButton);
        nextButton = listView.findViewById(R.id.nextButton);
        currPage = listView.findViewById(R.id.currentPage);

        intent = new Intent(this, LoadPageService.class);
        startService(intent);
        reLoadReddit.setOnRefreshListener(this);
        Log.d(TAG, "onCreate is finish");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart is STARTED");
        bindService(intent, connectionLoadPageService, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy is STARTED");
        if(!serviceConnected) return;
        unbindService(connectionLoadPageService);
        serviceConnected = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState is STARTED");
        outState.putParcelable("listViewState", listView.onSaveInstanceState());
        outState.putInt("currentPage", currentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState is STARTED");
        listViewState = savedInstanceState.getParcelable("listViewState") == null ?
                null : savedInstanceState.getParcelable("listViewState");
        currentPage = savedInstanceState.getInt("currentPage", 0);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh Page is STARTED");
        new Handler().postDelayed(() -> {
            if(!reLoadReddit.isRefreshing()) {
                if (adapter == null) {
                    adapter = new AdapterPublications(getApplicationContext(), R.layout.publication, R.id.author, listPublications);
                    listView.setAdapter(adapter);
                    if(listViewState != null){
                        listView.onRestoreInstanceState(listViewState);
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    listView.setSelectionAfterHeaderView();
                }
                setViewCurrentPage();
                progressBar.setVisibility(ListView.INVISIBLE);
            }else {
                currentPage = 0;
                listPublications = loadPageService.loadPage(null, currentPage);
                adapter.notifyDataSetChanged();
                setViewCurrentPage();
                reLoadReddit.setRefreshing(false);
            }
        }, 1500);
    }

    @SuppressLint("NonConstantResourceId")
    public void navigateButtonClickListener(View view){
        String navigate = null;
        switch (view.getId()){
            case R.id.nextButton:
                currentPage++;
                navigate = "nextPage";
                break;
            case R.id.previousButton:
                currentPage--;
                navigate = "previousPage";
                break;
        }
        loadPage(navigate);
    }

    private void loadPage(String navigate){
        progressBar.setVisibility(ListView.VISIBLE);
        listPublications = loadPageService.loadPage(navigate, currentPage);
        onRefresh();
    }

    private void setViewCurrentPage(){
        toggleButton(prevButton);
        currPage.setText(String.valueOf(currentPage+1));
    }

    private void toggleButton(Button previousButton){
        if(currentPage==0){
            previousButton.setVisibility(ListView.INVISIBLE);
        }else {
            previousButton.setVisibility(ListView.VISIBLE);
        }
    }
}