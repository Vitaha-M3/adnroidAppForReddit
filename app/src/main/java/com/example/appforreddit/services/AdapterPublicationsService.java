package com.example.appforreddit.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appforreddit.activity.ImageFullscreenActivity;
import com.example.appforreddit.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterPublicationsService extends ArrayAdapter<JSONObject>{
    int listLayout;
    ArrayList<JSONObject> list;
    private final Context context;

    public AdapterPublicationsService(Context context, int listLayout, int field, ArrayList<JSONObject> list){
        super(context, listLayout, field, list);
        this.context = context;
        this.listLayout = listLayout;
        this.list = list;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(listLayout, null, false);
        TextView author = itemView.findViewById(R.id.author);
        TextView created = itemView.findViewById(R.id.created);
        TextView labelPost = itemView.findViewById(R.id.labelPost);
        ImageView imagePublication = itemView.findViewById(R.id.imagePublication);
        TextView comments = itemView.findViewById(R.id.comments);
        try {
            author.setText(list.get(position).getString("subreddit_name_prefixed"));
            created.setText(WhenPostedService.adaptWhenPosted(list.get(position).getLong("created")));
            labelPost.setText(list.get(position).getString("title"));
            Picasso.get().load(list.get(position).getString("url_overridden_by_dest")).into(imagePublication);
            imagePublication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ImageFullscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        intent.putExtra("image", list.get(position).getString("url_overridden_by_dest"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    view.getContext().startActivity(intent);
                }
            });
            comments.setText("Comments: " + list.get(position).getString("num_comments"));
        }catch(JSONException e){
            e.printStackTrace();
        }
        return itemView;
    }
}
