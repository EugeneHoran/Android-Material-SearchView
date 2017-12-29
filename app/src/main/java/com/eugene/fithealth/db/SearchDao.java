package com.eugene.fithealth.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSearches(SearchEntity... searchEntities);

    @Delete
    public abstract void deleteSearches(SearchEntity... searchEntities);

    @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
    public abstract List<SearchEntity> getSearchList();

    @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
    public abstract LiveData<List<SearchEntity>> getSearchListLive();

}
