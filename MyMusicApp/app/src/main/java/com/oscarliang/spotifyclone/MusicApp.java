package com.oscarliang.spotifyclone;

import android.app.Application;

import com.oscarliang.spotifyclone.di.AppInjector;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import javax.inject.Inject;

public class MusicApp extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;

    //--------------------------------------------------------
    // Overriding methods
    //--------------------------------------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }
    //========================================================

}
