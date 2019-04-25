package com.angcyo.ocridcard;

import android.app.Application;

import com.angcyo.tesstwo.Tesstwo;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/04/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Tesstwo.init(this);
    }
}
