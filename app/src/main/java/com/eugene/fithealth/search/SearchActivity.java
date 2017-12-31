package com.eugene.fithealth.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.view.Menu;
import android.view.MenuItem;

import com.android.materialsearchview.MaterialSearchView;
import com.eugene.fithealth.R;
import com.eugene.fithealth.databinding.ActivitySearchBinding;
import com.eugene.fithealth.db.SearchEntity;
import com.eugene.fithealth.model.Food;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        MaterialSearchView.OnQueryTextListener,
        SearchRecyclerAdapter.SearchRecyclerInterface {

    private ActivitySearchBinding binding;
    private SearchViewModel model;
    private SearchRecyclerAdapter adapterSearch;
    private FatSecretRecyclerAdapter adapterFatSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setSupportActionBar(binding.toolbar);
        adapterSearch = new SearchRecyclerAdapter(this);
        adapterFatSecret = new FatSecretRecyclerAdapter();
        binding.recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recycler.setAdapter(adapterFatSecret);
        binding.searchHolder.setSearchRecyclerAdapter(adapterSearch);
        binding.searchHolder.addQueryTextListener(this);
        model = ViewModelProviders.of(this).get(SearchViewModel.class);
        observerFoodSearchList(model);
        observeSearchList(model);
    }

    @Override
    public void onBackPressed() {
        if (binding.searchHolder.isVisible()) {
            binding.searchHolder.hideSearch();
        } else {
            super.onBackPressed();
        }
    }

    // MaterialSearchView listeners
    @Override
    public boolean onQueryTextSubmit(String query) {
        binding.searchHolder.hideRecycler();
        model.searchFood(query, 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        binding.searchHolder.showRecycler();
        return true;
    }

    // adapter item clicked
    @Override
    public void onSearchItemClicked(String query) {
        binding.searchHolder.setSearchText(query);
        binding.searchHolder.hideRecycler();
        onQueryTextSubmit(query);
    }

    @Override
    public void onSearchDeleteClicked(SearchEntity searchEntity) {
        model.deleteSearchEntity(searchEntity);
    }

    private void observeSearchList(SearchViewModel model) {
        model.getSearchListLive().observe(this, new Observer<List<SearchEntity>>() {
            @Override
            public void onChanged(@Nullable List<SearchEntity> searchEntities) {
                adapterSearch.setItems(searchEntities);
            }
        });
    }

    private void observerFoodSearchList(SearchViewModel model) {
        model.getFoodList().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(@Nullable List<Food> foods) {
                adapterFatSecret.setItems(foods);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            binding.searchHolder.showSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
