package it.bsamu.sam.virtualgymbuddy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import adapter.AbstractCursorAdapter;
import adapter.TrainingDayAdapter;

public abstract class AbstractItemDetailFragment<A extends AbstractCursorAdapter> extends AbstractCursorRecyclerViewFragment<A>{
    public static final String ITEM_ID_ARG = "item_id";
    long itemId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ITEM_ID_ARG)) {
            // get data for selected program
            itemId = getArguments().getLong(ITEM_ID_ARG);
            asyncFetchItem();
        } else {
            throw new AssertionError("no item id");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        paintItemData();
    }

    protected abstract void paintItemData();
    protected abstract void asyncFetchItem();
}