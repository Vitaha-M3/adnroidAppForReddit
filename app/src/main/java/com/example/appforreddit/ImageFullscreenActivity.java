package com.example.appforreddit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class ImageFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        ImageView imageFullscreenView = findViewById(R.id.imageFullscreenView);
        String imageURL = (String) getIntent().getExtras().get("image");
        Picasso.get().load(imageURL).into(imageFullscreenView);
    }
}