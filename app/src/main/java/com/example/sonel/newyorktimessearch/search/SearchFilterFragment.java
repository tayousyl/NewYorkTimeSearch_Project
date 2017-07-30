package com.example.sonel.newyorktimessearch.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sonel.newyorktimessearch.Fragment.DatePickerFragment;
import com.example.sonel.newyorktimessearch.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sonel on 7/27/2017.
 */

public class SearchFilterFragment extends DialogFragment {


    @Bind(R.id.spSortOrder)
    Spinner spinnerSortOrder;
    @Bind(R.id.btnSave)
    Button saveButton;
    @Bind(R.id.btnCancel) Button cancelButton;
    @Bind(R.id.etBeginDate)
    EditText beginDateEditText;
    @Bind(R.id.cbArts)
    CheckBox artsCheckbox;
    @Bind(R.id.cbFashion_and_Stlye) CheckBox fashionAndStyleCheckbox;
    @Bind(R.id.cbSports) CheckBox sportsCheckbox;

    private StringBuilder localDateBuilder ;
    Map<Long, ActivitySearch.SortOrder> sortLookUp = new HashMap<>();
    {
        sortLookUp.put(0L, ActivitySearch.SortOrder.NEWEST);
        sortLookUp.put(1L, ActivitySearch.SortOrder.OLDEST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);

        getDialog().setTitle("Filter Search by");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.sortorder_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSortOrder.setAdapter(adapter);
        spinnerSortOrder.setSelection(0, true);

        localDateBuilder = new StringBuilder();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Set from previous entries or set default
        beginDateEditText.setText(ActivitySearch.FilterAttributes.beginDateDisplay);
        for (Map.Entry<Long, ActivitySearch.SortOrder> entry : sortLookUp.entrySet()) {
            if (ActivitySearch.FilterAttributes.sortOrder.equals(entry.getValue())) {
                spinnerSortOrder.setSelection(entry.getKey().intValue());
            }
        }
        for (ActivitySearch.NewsDesk nd: ActivitySearch.FilterAttributes.newsDesks) {
            if (ActivitySearch.NewsDesk.ARTS.equals(nd)) {
                artsCheckbox.setChecked(true);
            } else {
                artsCheckbox.setChecked(false);
            }
            if (ActivitySearch.NewsDesk.SPORTS.equals(nd)) {
                sportsCheckbox.setChecked(true);
            } else {
                sportsCheckbox.setChecked(false);
            }
            if (ActivitySearch.NewsDesk.FASHION_AND_STYLE.equals(nd)) {
                fashionAndStyleCheckbox.setChecked(true);
            } else {
                fashionAndStyleCheckbox.setChecked(false);
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivitySearch.FilterAttributes.beginDate = localDateBuilder.toString();
                ActivitySearch.FilterAttributes.beginDateDisplay = beginDateEditText
                        .getText().toString();
                updateNewsDesk();
                updateSortOrder();
                ((ActivitySearch)getActivity()).reSearch();
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        beginDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(SearchFilterFragment.this, 300);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    //YYYYMMDD for begindate
    public void fromDate(int year, int month, int day) {
        beginDateEditText.setText(year + "/" + month + "/" + day);
        localDateBuilder = new StringBuilder();
        localDateBuilder.append(year);

        if (month < 10) {
            localDateBuilder.append("0" + month);
        } else {
            localDateBuilder.append(month);
        }

        if (day < 10) {
            localDateBuilder.append("0" + day);
        } else {
            localDateBuilder.append(day);
        }
    }

    private void updateNewsDesk() {
        Log.i("Filter_Frag", "Updating NewsDesk");
        List<ActivitySearch.NewsDesk> newsDesks = new ArrayList<ActivitySearch.NewsDesk>();

        if (artsCheckbox.isChecked()) {
            newsDesks.add(ActivitySearch.NewsDesk.ARTS);
        }
        if (fashionAndStyleCheckbox.isChecked()) {
            newsDesks.add(ActivitySearch.NewsDesk.FASHION_AND_STYLE);
        }
        if (sportsCheckbox.isChecked()) {
            newsDesks.add(ActivitySearch.NewsDesk.SPORTS);
        }

        ActivitySearch.FilterAttributes.newsDesks = newsDesks;
    }

    private void updateSortOrder() {

        ActivitySearch.FilterAttributes.sortOrder =
                sortLookUp.get(spinnerSortOrder.getSelectedItemId());
    }

}
