package it.bsamu.sam.virtualgymbuddy.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import adapter.AbstractCursorAdapter;
import it.bsamu.sam.virtualgymbuddy.databinding.ExercisesFragmentBinding;
import relational.AppDb;



public abstract class AbstractCursorRecyclerViewFragment<A extends RecyclerView.Adapter> extends Fragment {
    /**
     * An abstract Fragment used for displaying instances of an entity fetched
     * from a db using a Cursor inside of a RecyclerView. The fragment is meant
     * to display ONE main entity.
     *
     * The class is parametric in the type of Adapter used for the RecyclerView and
     * required implementing all methods that directly interact with the entity
     * and the view(s) generated from instances of it.
     *
     */

    protected RecyclerView recyclerView;
    protected Cursor cursor;
    protected A adapter;

    protected AppDb db;

    private FloatingActionButton fab;

    protected abstract A instantiateAdapter();
    protected A getAdapter() {
        if (adapter != null) {
            return adapter;
        }
        synchronized (this) {
            if(adapter == null) {
                adapter = instantiateAdapter();
            }
            return adapter;
        }
    };
    protected abstract RecyclerView getRecyclerView(View parent);
    protected abstract View getMainView(LayoutInflater inflater, ViewGroup container);

    protected abstract void asyncFetchMainEntity();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(getContext());
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = getMainView(inflater, container);

        // create recycler view and set its adapter
        adapter = getAdapter();
        recyclerView = getRecyclerView(view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        // prevent restoring state until data has been reloaded
        adapter.setStateRestorationPolicy(
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // get all instances of the entity for this fragment from the db
        asyncFetchMainEntity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
