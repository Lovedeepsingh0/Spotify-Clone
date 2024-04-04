package com.oscarliang.spotifyclone.di;

import android.app.Application;

import com.oscarliang.spotifyclone.MusicApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, ServiceModule.class, ActivityBuildersModule.class})
public interface AppComponent {

    void inject(MusicApp musicApp);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}
