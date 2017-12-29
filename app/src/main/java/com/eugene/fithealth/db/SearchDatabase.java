package com.eugene.fithealth.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = SearchEntity.class, version = 1, exportSchema = false)
public abstract class SearchDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "search-db";
    private static SearchDatabase instance;

    public abstract SearchDao getSearchDao();

    public static SearchDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (SearchDatabase.class) {
                if (instance == null) {
                    instance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private static SearchDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, SearchDatabase.class, DATABASE_NAME).build();
    }

}
