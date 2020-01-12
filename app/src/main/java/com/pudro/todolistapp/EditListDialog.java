package com.pudro.todolistapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.AppViewModel;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class EditListDialog extends DialogFragment {
    private AppViewModel viewModel;

    /**
     * Implementation of {@link DialogFragment#onCreate(Bundle)}
     * @param savedInstanceState
     * @return The built Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the ViewModel for the current list info
        viewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_list, null);
        final TextInputEditText input = (TextInputEditText) view.findViewById(R.id.edit_list_text);

        input.setText(viewModel.selectedList.getTitle());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.update_list_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.update_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String imputed = input.getText().toString();
                        if (!imputed.trim().isEmpty()) {
                            // Update list title
                            Firestore.updateList(viewModel.selectedList.getDocumentID(), imputed);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_label, null);

        return builder.create();
    }
}
