package adapter;

import android.database.Cursor;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractCursorAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;

    public abstract void onBindViewHolder(V holder, Cursor cursor);

    public AbstractCursorAdapter(Cursor c) {
        setHasStableIds(true);
        swapCursor(c);
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        throwIfInvalid(position);

        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (mDataValid) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        throwIfInvalid(position);


        return mCursor.getLong(mRowIDColumn);
    }

    public Cursor getItem(int position) {
        throwIfInvalid(position);
        return mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }

        if (newCursor != null) {
            mCursor = newCursor;
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            mRowIDColumn = -1;
            mDataValid = false;
        }
    }

    private void throwIfInvalid(int position) {
        if (!mDataValid || !mCursor.moveToPosition(position)) {
            throw new IllegalStateException();
        }
    }
}