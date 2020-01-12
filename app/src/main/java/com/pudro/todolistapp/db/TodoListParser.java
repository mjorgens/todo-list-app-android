package com.pudro.todolistapp.db;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

import com.pudro.todolistapp.models.TodoList;

/**
 * A simple {@link SnapshotParser} subclass.
 */
public class TodoListParser implements SnapshotParser<TodoList> {
    /**
     * Implementation for {@link SnapshotParser#parseSnapshot(Object)}
     *
     * @param snapshot The snapshot of the document
     * @return A {@link TodoList} object
     */
    @NonNull
    @Override
    public TodoList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        // Parse the snapshot to an object
        TodoList todoList = snapshot.toObject(TodoList.class);
        // Add the id to the object
        todoList.setDocumentID(snapshot.getId());
        return todoList;
    }
}
