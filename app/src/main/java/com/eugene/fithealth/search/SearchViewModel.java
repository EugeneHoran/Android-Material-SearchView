package com.eugene.fithealth.search;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.eugene.fithealth.AppActivity;
import com.eugene.fithealth.api.FatSecretRequest;
import com.eugene.fithealth.db.SearchEntity;
import com.eugene.fithealth.db.SearchRepository;
import com.eugene.fithealth.model.Food;
import com.eugene.fithealth.model.Foods;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchViewModel extends AndroidViewModel {
    private SearchRepository repository;
    private MutableLiveData<List<Food>> getFoodList;
    private LiveData<List<SearchEntity>> getSearchListLive;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = ((AppActivity) application).getRepository();
        initSearch();
    }

    private void initSearch() {
        getSearchListLive = repository.getSearchListLive();
    }

    private void insertSearchEntity(String query) {
        repository.insertSearches(new SearchEntity(query, new Date().getTime()));
    }

    void deleteSearchEntity(SearchEntity searchEntity) {
        repository.deleteSearches(searchEntity);
    }

    void searchFood(String query, int page) {
        insertSearchEntity(query);
        String[] searchQuery = new String[2];
        searchQuery[0] = query;
        searchQuery[1] = String.valueOf(page);
        new FoodTask().execute(searchQuery);
    }

    LiveData<List<SearchEntity>> getSearchListLive() {
        return getSearchListLive;
    }

    MutableLiveData<List<Food>> getFoodList() {
        if (getFoodList == null) {
            getFoodList = new MutableLiveData<>();
        }
        return getFoodList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @SuppressLint("StaticFieldLeak")
    class FoodTask extends AsyncTask<String, Void, Foods> {
        @Override
        protected Foods doInBackground(String... query) {
            return new FatSecretRequest().getFoods(query[0], query[1]);
        }

        @Override
        protected void onPostExecute(Foods foods) {
            super.onPostExecute(foods);
            getFoodList().setValue(foods.getFood());
        }
    }
}
