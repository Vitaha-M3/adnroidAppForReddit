package com.example.appforreddit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterPublications extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> list;
    private final Context context;

    public AdapterPublications(Context context, int listLayout, int field, ArrayList<JSONObject> list){
        super(context, listLayout, field, list);
        this.context = context;
        this.listLayout = listLayout;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(listLayout, null, false);
        TextView author = itemView.findViewById(R.id.author);
        TextView created = itemView.findViewById(R.id.created);
        try {
            author.setText(list.get(position).getJSONObject("data").getString("subreddit"));
            created.setText(list.get(position).getJSONObject("data").getString("created"));
        }catch(JSONException e){
            e.printStackTrace();
        }
        return itemView;
    }
}
