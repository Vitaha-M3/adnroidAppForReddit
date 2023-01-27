package com.example.appforreddit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appforreddit.R;
import com.squareup.picasso.Picasso;

public class ImageFullscreenActivity extends AppCompatActivity {
    private static final int IDM_SAVE = 101;
    private ImageView imageFullscreenView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        imageFullscreenView = findViewById(R.id.imageFullscreenView);
        String imageURL = (String) getIntent().getExtras().get("image");
        Picasso.get().load(imageURL).into(imageFullscreenView);

        registerForContextMenu(imageFullscreenView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(ContextMenu.NONE, IDM_SAVE, ContextMenu.NONE,"Save image");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        CharSequence message;
        if (item.getItemId() == IDM_SAVE){
            saveImage(imageFullscreenView);
            message = "Image saved!";
        }else
            return super.onContextItemSelected(item);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return true;
    }

    private void saveImage(ImageView targetImage){
        Bitmap bitmap = ((BitmapDrawable)targetImage.getDrawable()).getBitmap();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "image1", "image from Reddit");
    }
}