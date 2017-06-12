package com.pinger.test.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.pinger.test.app.TestApplication;
import com.pinger.widget.R;

public class GifIconTransformation extends BitmapTransformation {
    private boolean isGif = false;
    private static Bitmap gifbmp;

    public GifIconTransformation(Context context, String url) {
        super(context);

        if (url.toLowerCase().endsWith(".gif")) isGif = true;
    }

    public GifIconTransformation(Context context, boolean isGif) {
        super(context);
        this.isGif = isGif;
    }

    static {
        gifbmp = BitmapFactory.decodeResource(TestApplication.getContext().getResources(), R.mipmap.common_image_gif);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (isGif) return addGifIcon(toTransform);
        else return toTransform;
    }

    @Override
    public String getId() {
        return "org.a8sport.com.GifIconTransformation";
    }

    // 绘制水印
    private Bitmap addGifIcon(Bitmap oldbitmap) {
        int width = oldbitmap.getWidth();
        int height = oldbitmap.getHeight();

        int gifbmpWidth = gifbmp.getWidth();
        int gifbmpHeight = gifbmp.getHeight();

        Canvas canvas = new Canvas(oldbitmap);
        canvas.drawBitmap(oldbitmap, 0, 0, null);
        canvas.drawBitmap(gifbmp, width - gifbmpWidth, height - gifbmpHeight, null);


        return oldbitmap;
    }
}  