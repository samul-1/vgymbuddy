package it.bsamu.sam.virtualgymbuddy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import adapter.AbstractCursorAdapter;
import adapter.TrainingDayAdapter;

public abstract class AbstractItemDetailFragment<A extends RecyclerView.Adapter> extends AbstractCursorRecyclerViewFragment<A>{
    /**
     * An abstract fragment used to display a single entry of an entity (detail mode).
     *
     * It inherits from AbstractCursorRecyclerViewFragment to allow simultaneous displaying of both
     * the main entry and its children, if any (e.g. a training program and its training days)
     */

    public static final String ITEM_ID_ARG = "item_id";
    protected long itemId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ITEM_ID_ARG)) {
            // get data for selected program
            itemId = getArguments().getLong(ITEM_ID_ARG);
            asyncFetchItem();
        } else {
            // not supposed to happen
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
