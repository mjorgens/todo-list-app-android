package com.pudro.todolistapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.AppViewModel;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class AddUserDialog extends DialogFragment {
    private AppViewModel viewModel;

    /**
     * Implementation of {@link DialogFragment#onCreate(Bundle)}
     * @param savedInstanceState
     * @return The built Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get the ViewModel for the current list info
        viewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        final TextInputEditText input = (TextInputEditText) view.findViewById(R.id.add_user_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_user_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputed = input.getText().toString();
                        if (!inputed.trim().isEmpty()) {
                            // Add member to the list
                            Firestore.addMember(viewModel.selectedList.getDocumentID(), inputed);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_label, null);

        return builder.create();
    }
}
