package com.eugene.fithealth;

import android.app.Application;

import com.eugene.fithealth.db.SearchDatabase;
import com.eugene.fithealth.db.SearchRepository;
import com.eugene.fithealth.util.AppExecutors;

public class AppActivity extends Application {
    private AppExecutors appExecutors;

    public void onCreate() {
        super.onCreate();
        appExecutors = new AppExecutors();
    }

    public SearchDatabase getDatabase() {
        return SearchDatabase.getInstance(this);
    }

    public SearchRepository getRepository() {
        return SearchRepository.getInstance(appExecutors, getDatabase());
    }
}

