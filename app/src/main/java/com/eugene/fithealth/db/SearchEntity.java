package com.eugene.fithealth.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "search_table")
public class SearchEntity {
    @PrimaryKey
    @NonNull
    private String text;
    private long timestamp;

    public SearchEntity(@NonNull String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
