package com.example.appforreddit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.appforreddit.R;
import com.example.appforreddit.services.LoadPageService;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout reLoadReddit;
    ListView listView;
    LoadPageService loadPage = new LoadPageService(this, this);
    Button prevButton;
    Button nextButton;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        reLoadReddit = findViewById(R.id.swipe);

        listView.addFooterView(getLayoutInflater().inflate(R.layout.footer_button, null, false));
        prevButton = listView.findViewById(R.id.previousButton);
        nextButton = listView.findViewById(R.id.nextButton);
        currentPage = 0;

        loadPage.loadPage(listView, null, currentPage);
        prevButton.setVisibility(View.INVISIBLE);

        nextButton.setOnClickListener(view -> {
            loadPage.loadPage(listView, "nextPage", currentPage++);
            toggleButton(prevButton);
        });

        prevButton.setOnClickListener(view -> {
            loadPage.loadPage(listView, "previousPage", currentPage--);
            toggleButton(prevButton);
        });
        reLoadReddit.setOnRefreshListener(this);
    }

    private void toggleButton(Button previousButton){
        if(currentPage==0){
            previousButton.setVisibility(ListView.INVISIBLE);
        }else {
            previousButton.setVisibility(ListView.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPage.refreshLoadPage();
                currentPage = 0;
                prevButton.setVisibility(View.INVISIBLE);
                reLoadReddit.setRefreshing(false);
            }
        }, 10000);
    }
}