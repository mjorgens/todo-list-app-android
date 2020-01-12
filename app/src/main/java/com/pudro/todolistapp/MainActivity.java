
package com.pudro.todolistapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.AppViewModel;

/**
 * A simple {@link AppCompatActivity} class
 * Implements {@link ListsFragment.OnTodoListClick} and {@link TodoListDialogFragment.OnTodoClick}
 */
public class MainActivity extends AppCompatActivity implements ListsFragment.OnTodoListClick, TodoListDialogFragment.OnTodoClick {
    private FirebaseUser user;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static int RC_SIGN_IN = 42;
    private static String TAG = "App";
    private FloatingActionButton fab;
    private FragmentManager fm;
    private AppViewModel viewModel;

    /**
     * Implementation of {@link AppCompatActivity#onCreate(Bundle)}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the ViewModel for the app
        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addList();
            }
        });

        fm = getSupportFragmentManager();

    }

    /**
     * Implementation of {@link AppCompatActivity#onStart()}
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Get the current user
        user = auth.getCurrentUser();
        // Check if user is not null... logged in
        if (user != null) {
            loadLoggedIn();
        } else {
            loadLoggedOut();
        }
    }

    /**
     * Implementation of {@link AppCompatActivity#onCreateOptionsMenu(Menu)}
     *
     * @param menu The menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Implementation of {@link AppCompatActivity#onOptionsItemSelected(MenuItem)}
     *
     * @param item Selected menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //vLogin
            case R.id.menu_login:
                if (user == null) {
                    loadLogin();
                }
                return true;
            // Logout
            case R.id.menu_logout:
                if (user != null) {
                    logout();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Implementation of {@link AppCompatActivity#onActivityResult(int, int, Intent)}
     *
     * @param requestCode Code of the launched activity
     * @param resultCode Code of the result
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Return from login activity
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successful login
            if (resultCode == RESULT_OK) {
                user = auth.getCurrentUser();
            } else {
                // User pressed back button
                if (response == null) {
                    Log.e(TAG, "onActivityResult: user pressed back");
                    return;
                }
                // No network error
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e(TAG, "onActivityResult: No internet");
                    return;
                }
                // Catchall error
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    /**
     * Method to load logout fragment
     */
    private void loadLoggedOut() {
        fab.hide();
        fm.beginTransaction()
                .replace(R.id.fragment, new LoginFragment())
                .commit();
    }

    /**
     * Method to load the list RecyclerView fragment
     */
    private void loadLoggedIn() {
        fab.show();
        Firestore.updateUser();
        fm.beginTransaction()
                .replace(R.id.fragment, new ListsFragment())
                .commit();
    }

    /**
     * Method to load login activity
     */
    private void loadLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                )).build(), RC_SIGN_IN);
    }

    /**
     * Method to logout user
     */
    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user = null;
                        fab.hide();
                        loadLoggedOut();
                    }
                });
    }

    /**
     * Method to launch add list dialog
     */
    private void addList() {
        new AddListDialog().show(fm, null);
    }


    /**
     * Implementation of {@link ListsFragment.OnTodoListClick#onListClick()}
     * Loads the list items fragment
     */
    @Override
    public void onListClick() {
        TodoListDialogFragment todoListFrag = new TodoListDialogFragment();

        fm.beginTransaction()
                .add(android.R.id.content, todoListFrag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Implementation of {@link ListsFragment.OnTodoListClick#onListLongClick()}
     * Loads the edit list dialog
     */
    @Override
    public void onListLongClick() {
        EditListDialog dialog = new EditListDialog();
        dialog.show(fm, null);
    }

    /**
     * Implementation of {@link TodoListDialogFragment.OnTodoClick#onListRemove()}
     * Snackbar to undo removing a user
     */
    @Override
    public void onListRemove() {
        Snackbar.make((View) findViewById(R.id.coordinatorLayout), R.string.list_deleted_snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_label, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Firestore.reAddMember(viewModel.selectedList.getDocumentID());
                    }
                }).show();
    }
}
