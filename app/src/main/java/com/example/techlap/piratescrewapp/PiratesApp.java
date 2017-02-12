package com.example.techlap.piratescrewapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by tech lap on 06/10/2016.
 */
public class PiratesApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //firebase only store stringd in your cashe
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // so we need the offline cappabilities of picaso
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        //indecate where he get the image from cash,online
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(final ImageView imageView, final Uri uri, final Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                    }
                });
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
