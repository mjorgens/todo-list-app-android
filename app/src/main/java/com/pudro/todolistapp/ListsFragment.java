package com.pudro.todolistapp;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.db.TodoListParser;
import com.pudro.todolistapp.models.AppViewModel;
import com.pudro.todolistapp.models.TodoList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListsFragment extends Fragment {
    private View root;
    private RecyclerView recyclerView;
    private TodoListAdapter adapter;
    private OnTodoListClick mCallback;
    private AppViewModel viewModel;

    /**
     * Interface to process list clicks
     */
    public interface OnTodoListClick{
        void onListClick();
        void onListLongClick();
    }

    public ListsFragment() {
        // Required empty public constructor
    }

    /**
     * Implementation of {@link Fragment#onAttach(Context)}
     * @param activity The activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // load the activity call back
        try {
            mCallback = (OnTodoListClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement the OnTodoListClick interface");
        }
    }

    /**
     * Implementation of {@link Fragment#onCreate(Bundle)}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_lists, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

        recyclerView = (RecyclerView) root.findViewById(R.id.rvLists);

        // Setup the query options for the adapter
        FirestoreRecyclerOptions<TodoList> options = new FirestoreRecyclerOptions.Builder<TodoList>()
                .setQuery(Firestore.getTodoLists(), new TodoListParser())
                .build();

        adapter = new TodoListAdapter(options);
        // Implementing interface methods for the adapter
        adapter.setOnTodoListClickListener(new TodoListAdapter.OnTodoListClick() {
            @Override
            public void onListClick(TodoList list) {
                // Save the current list to the ViewModel
                viewModel.selectedList = list;
                // Launch the TodoList fragment
                mCallback.onListClick();
            }

            @Override
            public void onListLongClick(TodoList list) {
                // Save the current list to the ViewModel
                viewModel.selectedList = list;
                // Launch the Edit list dialog
                mCallback.onListLongClick();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));;

        return root;
    }

    /**
     * Implementation of {@link Fragment#onStart()}
     */
    @Override
    public void onStart() {
        super.onStart();
        // Start the adapter listening for changes
        adapter.startListening();
    }

    /**
     * Implementation of {@link Fragment#onStop()} ()}
     */
    @Override
    public void onStop() {
        super.onStop();
        // Stop the adapter listening
        adapter.stopListening();
    }

}
