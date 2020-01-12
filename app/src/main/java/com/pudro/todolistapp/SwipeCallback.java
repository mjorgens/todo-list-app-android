package com.pudro.todolistapp;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.Todo;
import com.pudro.todolistapp.models.TodoList;

/**
 * A simple {@link ItemTouchHelper.SimpleCallback} class
 */
public class SwipeCallback extends ItemTouchHelper.SimpleCallback {
    private TodoAdapter adapter;
    private TodoList list;
    private View view;

    /**
     * Constructor for the class
     *
     * @param view    The view
     * @param adapter The adapter using the class
     * @param list    The current list
     */
    public SwipeCallback(@NonNull View view, @NonNull TodoAdapter adapter, @NonNull TodoList list) {
        // Set the swipe to do both left and right
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.view = view;
        this.list = list;
    }

    /**
     * Implementation of {@link ItemTouchHelper.SimpleCallback#onChildDraw(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)}
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        // Set the background color
        ColorDrawable background = new ColorDrawable(view.getContext().getResources().getColor(R.color.delete_background));
        // Set the background icon
        Drawable icon = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_delete_forever_white_24dp);

        // Margin math for the icon
        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = viewHolder.itemView.getTop() + (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // Swiping right
        if (dX > 0) {
            // Set the margin for left and right icon
            int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
            int iconRight = viewHolder.itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            // Set the background color bounds
            background.setBounds(0, viewHolder.itemView.getTop(), viewHolder.itemView.getLeft() + (int) dX, viewHolder.itemView.getBottom());
        }

        // Swiping left
        if (dX < 0) {
            // Set the margin for left and right icon
            int iconLeft = viewHolder.itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = viewHolder.itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            // Set the background color bounds
            background.setBounds(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
        }
        // Draw the background and icon
        background.draw(c);
        icon.draw(c);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    /**
     * Implementation of {@link ItemTouchHelper.SimpleCallback#onSwiped(RecyclerView.ViewHolder, int)}
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        // Get the current item from the adapter
        final Todo deletedItem = adapter.getItem(viewHolder.getAdapterPosition());
        // Delete the item from the adapter
        adapter.deleteItem(viewHolder.getAdapterPosition());

        // Snackbar to undo removing the item from the list
        Snackbar.make(view, R.string.item_deleted_snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_label, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Firestore.addItemToList(list.getDocumentID(), deletedItem);
                    }
                }).show();
    }
}

