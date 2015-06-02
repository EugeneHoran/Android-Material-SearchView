package com.eugene.fithealth.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eugene.fithealth.R;

public class FragmentMeals extends Fragment {
    private View v;
    TextView titleSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_today, container, false);
        return v;
    }
}

