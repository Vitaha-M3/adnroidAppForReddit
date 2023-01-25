package com.example.appforreddit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterPublications extends ArrayAdapter<JSONObject>{
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
        TextView labelPost = itemView.findViewById(R.id.labelPost);
        ImageView imagePublication = itemView.findViewById(R.id.imagePublication);
        TextView comments = itemView.findViewById(R.id.comments);
        try {
            author.setText(list.get(position).getString("subreddit"));
            created.setText(adaptPostedBy(list.get(position).getLong("created")));
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
            comments.setText(list.get(position).getString("num_comments"));
        }catch(JSONException e){
            e.printStackTrace();
        }
        return itemView;
    }

    private String adaptPostedBy(Long created){
        StringBuilder postedBy = new StringBuilder("Posted ");
        long timeAgo = System.currentTimeMillis()/1000-created;
        int day = 86400;
        int hour = 3600;
        int daysOut = (int) Math.floor(timeAgo/day);
        int hoursOut = (int) Math.floor((timeAgo - daysOut * day)/hour);
        int minutesOut = (int) Math.floor((timeAgo - daysOut * day - hoursOut * hour)/60);
        postedBy.append(daysOut>0 ? daysOut + " day " : hoursOut > 0 ? hoursOut + " h " : minutesOut > 0 ? minutesOut + " m " : " < 1 minute ");
        if(daysOut > 0 && hoursOut > 0){
            postedBy.append(hoursOut).append(" h ");
        }
        postedBy.append("ago");
        return postedBy.toString();
    }
}
