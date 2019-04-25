package com.angcyo.tesstwo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/04/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class Tesstwo {
    //识别语言英文
    static final String DEFAULT_LANGUAGE = "eng";
    private static final String TAG = "tesstwo";
    //训练数据路径，必须包含tesseract文件夹
    static String TESSBASE_PATH;

    Thread parseThread;

    volatile Bitmap frame;

    Object lock = new Object();

    OnResultCallback onResultCallback;

    public Tesstwo() {
        parseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isInterrupted()) {
                    synchronized (lock) {
                        if (frame == null) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    long startTime = System.currentTimeMillis();
                    localreInner(frame);
                    long endTime = System.currentTimeMillis();
                    Log.e("angyco", "耗时:" + (endTime - startTime));
                    frame.recycle();
                    frame = null;
                }
            }
        };
        parseThread.start();
    }

    public static void init(Context context) {
        TESSBASE_PATH = context.getCacheDir().getAbsolutePath();
        new AssestUtils(context, TESSBASE_PATH).init();
    }

    static int dip2px(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    /**
     * @param bitmap 包含身份证轮廓的照片
     */
    public Bitmap localre(Bitmap bitmap) {
//        int x, y, w, h;
//        x = (int) (bitmap.getWidth() * 0.330);
//        y = (int) (bitmap.getHeight() * 0.73);
//        w = (int) (bitmap.getWidth() * 0.60 + 0.5f);
//        h = (int) (bitmap.getHeight() * 0.12 + 0.5f);
//
//        //定位到身份证号码的位置
//        Bitmap noBitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
//        noBitmap = ImageFilter.gray2Binary(noBitmap);
//        noBitmap = ImageFilter.grayScaleImage(noBitmap);


        if (frame == null) {
            frame = bitmap;
            synchronized (lock) {
                lock.notify();
            }
        }

        //return noBitmap;
        return null;
    }

    public Bitmap localreInner(Bitmap bitmap) {
        int x, y, w, h;
        x = (int) (bitmap.getWidth() * 0.330);
        y = (int) (bitmap.getHeight() * 0.750);
        w = (int) (bitmap.getWidth() * 0.6 + 0.5f);
        h = (int) (bitmap.getHeight() * 0.12 + 0.5f);

        //定位到身份证号码的位置
        Bitmap noBitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
        bitmap.recycle();
        Bitmap bm = noBitmap.copy(Bitmap.Config.ARGB_8888, true);

        String content = null;

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        //设置识别模式
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);//PSM_SINGLE_LINE
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789Xx");
        //设置要识别的图片
        bm = ImageFilter.gray2Binary(bm);
        bm = ImageFilter.grayScaleImage(bm);
        baseApi.setImage(bm);
        content = baseApi.getUTF8Text();
        if (!TextUtils.isEmpty(content) && IDCardUtil.isIdCard(content)) {
            Log.e(TAG, "localre: " + content);

            if (onResultCallback != null) {
                onResultCallback.onResult(content);
            }
        }
        baseApi.clear();
        baseApi.end();
        return noBitmap;
    }

    public void release() {
        parseThread.interrupt();
        parseThread = null;
    }

    public void setOnResultCallback(OnResultCallback onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    public interface OnResultCallback {
        void onResult(String idCardNo);
    }
}
