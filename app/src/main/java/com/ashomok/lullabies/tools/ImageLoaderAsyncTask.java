package com.ashomok.lullabies.tools;

import android.content.Context;

import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by Iuliia on 13.04.2016.
 */

public class ImageLoaderAsyncTask extends AsyncTask<Integer, Void, RequestCreator> {

    private final ImageView imageView;

    private int data = 0;

    private final Context context;

    private int width;

    private int height;

    public ImageLoaderAsyncTask(ImageView imageView) {
        this.imageView = imageView;
        context = imageView.getContext();
        setSize(context);
    }

    @Override
    protected RequestCreator doInBackground(Integer... params) {
        data = params[0];
        return  Picasso.with(context).load(data).resize(width, height);
    }

    @Override
    protected void onPostExecute(RequestCreator requestCreator) {

        requestCreator.into(imageView);
    }

    private void setSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        if (width > 1500) {
            width = 1500;
        }

        if (height > 700) {
            height = 700;
        }
    }

}
