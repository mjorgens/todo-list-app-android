package com.pudro.todolistapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.Todo;
import com.pudro.todolistapp.models.TodoList;

/**
 * A simple {@link FirestoreRecyclerAdapter} subclass.
 */
public class TodoAdapter extends FirestoreRecyclerAdapter<Todo, TodoAdapter.TodoHolder> {
    private TodoList list;
    private OnTodoClick listener;

    /**
     * Interface to process item clicks.
     */
    public interface OnTodoClick {
        void onItemLongClick(Todo item);
    }

    /**
     * Constructor for the class
     * @param options FireRecyclerOptions query options
     * @param list The TodoList object holding the list information
     */
    public TodoAdapter(@NonNull FirestoreRecyclerOptions<Todo> options, @NonNull TodoList list) {
        super(options);
        this.list = list;
    }

    /**
     * Implementation for {@link FirestoreRecyclerAdapter#bindViewHolder(RecyclerView.ViewHolder, int)}
     * @param holder The holder for the {@link RecyclerView}.
     * @param position The current position of the holder.
     * @param model The {@link Todo} object of the current holder.
     */
    @Override
    protected void onBindViewHolder(@NonNull final TodoHolder holder, int position, @NonNull Todo model) {
        holder.item = model;
        holder.name.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.priority.setText(Integer.toString(model.getPriority()));

        // Set the checkbox if completed;
        holder.completed.setChecked(model.isCompleted());
        // Listener for checkbox change
        holder.completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If checkbox changed update the holder item and item in the database
                holder.item.setCompleted(isChecked);
                Firestore.updateCompleted(list.getDocumentID(), holder.item.getDocumentID(), isChecked);
            }
        });
        // Listener for a long click
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    // Launches the edit dialog
                    listener.onItemLongClick(holder.item);
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
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get view from inflater
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_todo, viewGroup, false);
        return new TodoHolder(view);
    }

    /**
     * Method that deletes the document from the snapshot
     * @param pos The position in the snapshot
     */
    public void deleteItem(int pos) {
        getSnapshots().getSnapshot(pos).getReference().delete();
    }


    /**
     * A simple {@link RecyclerView.ViewHolder} subclass
     */
    public class TodoHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView priority;
        public CheckBox completed;
        public Todo item;
        public View view;

        /**
         * Constructor for the class.
         * @param itemView The View
         */
        public TodoHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            name = (TextView) itemView.findViewById(R.id.todoName);
            description = (TextView) itemView.findViewById(R.id.todoDescription);
            priority = (TextView) itemView.findViewById(R.id.todoPriority);
            completed = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    /**
     * Method to set the callback listener.
     * @param listener The fragment that is using the adapter.
     */
    public void setOnTodoClickListener(@NonNull TodoAdapter.OnTodoClick listener) {
        this.listener = listener;
    }
}
