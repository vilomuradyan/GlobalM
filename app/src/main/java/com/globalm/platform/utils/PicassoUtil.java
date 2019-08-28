package com.globalm.platform.utils;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public abstract class PicassoUtil {

    public static void loadCircleImageIntoImageView(String url,
                                                    ImageView imageView,
                                                    @DrawableRes int placeholder) {
        Picasso.get()
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(imageView);
    }

    public static void loadCircleImageIntoImageView(Uri uri,
                                                    ImageView imageView,
                                                    @DrawableRes int placeholder) {
        Picasso.get()
                .load(uri)
                .placeholder(placeholder)
                .error(placeholder)
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(imageView);
    }

    public static void loadImageIntoImageView(String url,
                                              ImageView imageView,
                                              @DrawableRes int placeholder) {
        Picasso.get()
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
