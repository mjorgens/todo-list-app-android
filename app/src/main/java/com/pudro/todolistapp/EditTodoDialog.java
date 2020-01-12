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
import android.widget.NumberPicker;

import com.pudro.todolistapp.db.Firestore;
import com.pudro.todolistapp.models.AppViewModel;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class EditTodoDialog extends DialogFragment {
    private AppViewModel viewModel;

    /**
     * Implementation of {@link DialogFragment#onCreate(Bundle)}
     * @param savedInstanceState
     * @return The built Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get the ViewModel for the current list and item info
        viewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class );

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        final TextInputEditText title = (TextInputEditText) view.findViewById(R.id.todo_title_add);
        final TextInputEditText desc = (TextInputEditText) view.findViewById(R.id.todo_description_add);

        final NumberPicker np = (NumberPicker)  view.findViewById(R.id.todo_priority_add_picker);
        np.setMaxValue(5);
        np.setMinValue(1);

        title.setText(viewModel.selectedItem.getTitle());
        desc.setText(viewModel.selectedItem.getDescription());
        np.setValue(viewModel.selectedItem.getPriority());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.edit_item_dialog__title)
                .setView(view)
                .setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputedTile = title.getText().toString();
                        String inputedDesc = desc.getText().toString();
                        if(!inputedTile.trim().isEmpty() || !inputedDesc.trim().isEmpty()){
                            // Update item info
                            Firestore.updateItem(viewModel.selectedList.getDocumentID(),
                                    viewModel.selectedItem.getDocumentID(),
                                    inputedTile,
                                    inputedDesc,
                                    np.getValue());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_label, null);

        return builder.create();
    }

}
