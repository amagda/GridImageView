package com.amagda.android.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] imagePaths = new String[]{
                "http://x.annihil.us/u/prod/marvel/i/mg/9/80/537ba5b368b7d.jpg",
                "http://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
                "http://x.annihil.us/u/prod/marvel/i/mg/6/60/538cd3628a05e.jpg",
                "http://x.annihil.us/u/prod/marvel/i/mg/7/10/537bc71e9286f.jpg"};
        ((GridImageView) findViewById(R.id.rectangle_images_count_1)).setImagePaths(imagePaths[0]);
        ((GridImageView) findViewById(R.id.rectangle_images_count_2)).setImagePaths(Arrays.copyOf(imagePaths, 2));
        ((GridImageView) findViewById(R.id.rectangle_images_count_3)).setImagePaths(Arrays.copyOf(imagePaths, 3));
        ((GridImageView) findViewById(R.id.rectangle_images_count_4)).setImagePaths(Arrays.copyOf(imagePaths, 4));
        ((GridImageView) findViewById(R.id.circle_images_count_1)).setImagePaths(imagePaths[0]);
        ((GridImageView) findViewById(R.id.circle_images_count_2)).setImagePaths(Arrays.copyOf(imagePaths, 2));
        ((GridImageView) findViewById(R.id.circle_images_count_3)).setImagePaths(Arrays.copyOf(imagePaths, 3));

        GridImageView gridImageView = (GridImageView) findViewById(R.id.circle_images_count_4);
        gridImageView.setPaddingBetweenImages(getResources().getDimensionPixelOffset(R.dimen.padding_btw_images));
        gridImageView.setShapeMode(GridImageView.CIRCLE_SHAPE_MODE);
        gridImageView.setImagePaths(
                "http://x.annihil.us/u/prod/marvel/i/mg/9/80/537ba5b368b7d.jpg",
                "http://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
                "http://x.annihil.us/u/prod/marvel/i/mg/6/60/538cd3628a05e.jpg",
                "http://x.annihil.us/u/prod/marvel/i/mg/7/10/537bc71e9286f.jpg");
    }
}
