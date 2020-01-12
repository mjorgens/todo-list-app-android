package com.pudro.todolistapp.db;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

import com.pudro.todolistapp.models.Todo;

/**
 * A simple {@link SnapshotParser} subclass.
 */
public class TodoParser implements SnapshotParser<Todo> {

    /**
     * Implementation for {@link SnapshotParser#parseSnapshot(Object)}
     *
     * @param snapshot The snapshot of the document
     * @return A {@link Todo} object
     */
    @NonNull
    @Override
    public Todo parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        // Parse the snapshot to an object
        Todo todo = snapshot.toObject(Todo.class);
        // Add the id to the object
        todo.setDocumentID(snapshot.getId());
        return todo;
    }
}
