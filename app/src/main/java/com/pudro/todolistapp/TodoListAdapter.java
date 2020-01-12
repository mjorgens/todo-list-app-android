package com.pudro.todolistapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.pudro.todolistapp.models.TodoList;

/**
 * A simple {@link FirestoreRecyclerAdapter} subclass.
 */
public class TodoListAdapter extends FirestoreRecyclerAdapter<TodoList, TodoListAdapter.TodoListHolder> {
    private OnTodoListClick listener;


    /**
     * Interface to process item clicks.
     */
    public interface OnTodoListClick {
        void onListClick(TodoList list);

        void onListLongClick(TodoList list);
    }

    /**
     * Constructor for the class
     * @param options FireRecyclerOptions query options
     */
    public TodoListAdapter(@NonNull FirestoreRecyclerOptions<TodoList> options) {
        super(options);
    }

    /**
     * Implementation for {@link FirestoreRecyclerAdapter#bindViewHolder(RecyclerView.ViewHolder, int)}
     * @param holder The holder for the {@link RecyclerView}.
     * @param position The current position of the holder.
     * @param model The {@link TodoList} object of the current holder.
     */
    @Override
    protected void onBindViewHolder(@NonNull TodoListHolder holder, int position, @NonNull final TodoList model) {
        holder.list = model;
        holder.itemTitle.setText(model.getTitle());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onListClick(model);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onListLongClick(model);
                }
                return true;
            }
        });
    }

    /**
     * Implementation for {@link FirestoreRecyclerAdapter#onCreateViewHolder(ViewGroup, int)}
     * @param viewGroup
     * @param i
     * @return the view holder
     */
    @NonNull
    @Override
    public TodoListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get view from inflater
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);
        return new TodoListHolder(view);
    }

    /**
     * A simple {@link RecyclerView.ViewHolder} subclass
     */
    public class TodoListHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TodoList list;
        public View view;

        /**
         * Constructor for the class.
         * @param itemView The View
         */
        public TodoListHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            itemTitle = (TextView) itemView.findViewById(R.id.listName);
        }
    }

    /**
     * Method to set the callback listener.
     * @param listener The fragment that is using the adapter.
     */
    public void setOnTodoListClickListener(@NonNull OnTodoListClick listener) {
        this.listener = listener;
    }
}
