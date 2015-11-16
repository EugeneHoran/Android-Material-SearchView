package com.eugene.fithealth;

import android.app.Application;
import android.content.Context;

import com.eugene.fithealth.LogQuickSearchData.LogQuickSearch;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends Application {
    private static AppActivity appContext;

    public void onCreate() {
        super.onCreate();
        appContext = this;
        initializeDatabase();
    }

    public static Context context() {
        return appContext;
    }

    private void initializeDatabase() {
        List<Class<? extends Model>> models = new ArrayList<>(0);
        models.add(LogQuickSearch.class);
        String dbName = this.getResources().getString(R.string.database_name);
        DatabaseAdapter.setDatabaseName(dbName);
        DatabaseAdapter adapter = new DatabaseAdapter(appContext);
        adapter.setModels(models);
    }

}

