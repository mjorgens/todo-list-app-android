package com.pudro.todolistapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.db.TodoParser;
import com.pudro.todolistapp.models.AppViewModel;
import com.pudro.todolistapp.models.Todo;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class TodoListDialogFragment extends DialogFragment {
    private View root;
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private OnTodoClick mCallback;
    private AppViewModel viewModel;

    /**
     * Interface to handle to item clicks
     */
    public interface OnTodoClick {
        void onListRemove();
    }

    public TodoListDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Implementation of {@link DialogFragment#onAttach(Context)}
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // load the activity call back
        try {
            mCallback = (OnTodoClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement the OnTodoClick interface");
        }
    }

    /**
     * Implementation of {@link DialogFragment#onCreate(Bundle)}
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dialog_fragment_todo_list, container, false);

        // // Get the ViewModel for the current list info
        viewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

        // Set the Toolbar title
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle(viewModel.selectedList.getTitle());

        // Add the Toolbar to the ActionBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Get the ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Display options for the ActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Add OptionsMenu
        setHasOptionsMenu(true);

        FloatingActionButton fab = root.findViewById(R.id.fabTodo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTodoDialog dialog = new AddTodoDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        recyclerView = (RecyclerView) root.findViewById(R.id.rvTodos);

        // Setup the query options for the adapter
        FirestoreRecyclerOptions<Todo> options = new FirestoreRecyclerOptions.Builder<Todo>()
                .setQuery(Firestore.getTodoItems(viewModel.selectedList.getDocumentID()), new TodoParser())
                .build();

        adapter = new TodoAdapter(options, viewModel.selectedList);

        // Implementing interface methods for the adapter
        adapter.setOnTodoClickListener(new TodoAdapter.OnTodoClick() {
            @Override
            public void onItemLongClick(Todo item) {
                EditTodoDialog dialog = new EditTodoDialog();
                // set the item in the ViewModel
                viewModel.selectedItem = item;
                // Launch the item edit dialog
                dialog.show(getFragmentManager(), null);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);

        // Attach delete on swipe to the RecyclerView
        new ItemTouchHelper(new SwipeCallback(root, adapter, viewModel.selectedList))
                .attachToRecyclerView(recyclerView);

        return root;
    }

    /**
     * Implementation of {@link DialogFragment#onStart()}
     */
    @Override
    public void onStart() {
        super.onStart();
        // Start the adapter listening for changes
        adapter.startListening();
    }

    /**
     * Implementation of {@link DialogFragment#onStop()} ()}
     */
    @Override
    public void onStop() {
        super.onStop();
        // Stop the adapter listening
        adapter.stopListening();
    }

    /**
     * Implementation of {@link DialogFragment#onCreateDialog(Bundle)}
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Remove the previous main title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * Implementation of {@link DialogFragment#onCreateOptionsMenu(Menu, MenuInflater)}
     *
     * @param menu     The menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Remove the previous Menu
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_todo, menu);
    }

    /**
     * Implementation of {@link DialogFragment#onOptionsItemSelected(MenuItem)}
     *
     * @param item Selected menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Add user to list
            case R.id.menu_add_user:
                launchAddUserDialog();
                return true;
            // Remove current user from list
            case R.id.menu_remove_user:
                launchRemoveUserDialog();
                dismiss();
                return true;
            case android.R.id.home:
                // Close fragment
                dismiss();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to launch add user dialog
     */
    private void launchAddUserDialog() {
        AddUserDialog dialog = new AddUserDialog();
        dialog.show(getFragmentManager(), null);
    }

    /**
     * Method to launch remove user dialog
     */
    private void launchRemoveUserDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.remove_list_dialog_title)
                .setMessage(R.string.remove_list_message)
                .setPositiveButton(R.string.remove_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Firestore.removeMember(viewModel.selectedList.getDocumentID());
                        mCallback.onListRemove();
                    }
                })
                .setNegativeButton(R.string.cancel_label, null)
                .show();

    }
}
