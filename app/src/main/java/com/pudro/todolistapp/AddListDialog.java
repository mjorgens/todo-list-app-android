package com.pudro.todolistapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.pudro.todolistapp.db.Firestore;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class AddListDialog extends DialogFragment {

    /**
     * Implementation of {@link DialogFragment#onCreate(Bundle)}
     * @param savedInstanceState
     * @return The built Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_list, null);
        final TextInputEditText input = (TextInputEditText) view.findViewById(R.id.add_user_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_list_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputed = input.getText().toString();
                        if(!inputed.trim().isEmpty()){
                            Firestore.addTodoList(inputed);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_label, null);

        return builder.create();
    }
}
