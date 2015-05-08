package com.mobanic.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobanic.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultiSpinner extends Spinner implements OnMultiChoiceClickListener {

    private SearchFiltersListener mListener;
    private ArrayAdapter<String> mAdapter;
    private Set<String> mChoicesList;
    private boolean[] mCheckboxes;
    private String mSearchKey;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SearchSpinner,
                0, 0);

        try {
            mSearchKey = a.getString(R.styleable.SearchSpinner_searchKey);
        } finally {
            a.recycle();
        }

        mListener = (SearchFiltersListener) context;

        mAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<String>());
        mAdapter.add(mSearchKey);

        setAdapter(mAdapter);
    }

    public void setItems(Set<String> choicesList) {
        mChoicesList = choicesList;
        mCheckboxes = new boolean[choicesList.size()];

        mAdapter.clear();
        mAdapter.addAll(mChoicesList);
        mAdapter.add(mSearchKey);
        mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        setSelection(mAdapter.getCount());
    }

    @Override
    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
        mCheckboxes[position] = isChecked;
    }

    @Override
    public boolean performClick() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> makes = sharedPrefs.getStringSet("Make", null);

        if (mChoicesList == null) {
            Toast.makeText(getContext(), "No cars to choose from", Toast.LENGTH_SHORT).show();
        } else if (mSearchKey.equals("Model") && (makes == null || makes.size() == 0)) {
            Toast.makeText(getContext(), "Select make first", Toast.LENGTH_SHORT).show();
        } else {
            CharSequence[] choices = mChoicesList.toArray(
                    new CharSequence[mChoicesList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMultiChoiceItems(choices, mCheckboxes, this);
            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateSelectedItems();
                }
            });
            builder.show();
        }
        return true;
    }

    private void updateSelectedItems() {
        String shownValue = null;

        Set<String> selectedItems = new HashSet<>();
        for (int i = 0; i < mCheckboxes.length; i++) {
            if (mCheckboxes[i]) {
                shownValue = mAdapter.getItem(i);
                selectedItems.add(shownValue);
            }
        }
        mAdapter.clear();
        mAdapter.addAll(mChoicesList);
        if (selectedItems.size() == 0) {
            mAdapter.add(mSearchKey);
        } else if (selectedItems.size() == 1) {
            mAdapter.add(shownValue);
        } else {
            if (!mSearchKey.contains("Trans")) {
                mAdapter.add(selectedItems.size() + " " + mSearchKey.toLowerCase() + "s");
            } else {
                mAdapter.add(selectedItems.size() + " trans. types");
            }
        }
        setSelection(mAdapter.getCount());

        mListener.onFilterSet(mSearchKey, selectedItems);
    }

    public interface SearchFiltersListener {
        void onFilterSet(String filterKey, Set<String> selectedItems);
    }
}