package com.pudro.todolistapp.db;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

import com.pudro.todolistapp.models.Todo;
import com.pudro.todolistapp.models.TodoList;

/**
 * Utility class for the firestore database queries.
 */
public final class Firestore {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Firestore() {

    }

    /**
     * Method to update the current user
     */
    public static void updateUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Method to add a list.
     *
     * @param title The title of the list
     */
    public static void addTodoList(String title) {
        db.collection("lists").add(new TodoList(title, Arrays.asList(user.getUid())));
    }

    /**
     * Method to add item to a list.
     *
     * @param listId      The list id
     * @param name        Name of the item
     * @param description Description of the item
     * @param priority    Priority of the item
     */
    public static void addItemToList(String listId, String name, String description, int priority) {
        addItemToList(listId, new Todo(name, description, priority, user.getUid()));
    }

    /**
     * Method to add item to a list.
     *
     * @param listId The list id
     * @param item   The {@link Todo} to add
     */
    public static void addItemToList(String listId, Todo item) {
        db.collection("lists").document(listId)
                .collection("todo")
                .add(item);
    }

    /**
     * Method to update item
     *
     * @param listId   List id
     * @param todoId   Item id
     * @param title    Updated title
     * @param desc     Updated description
     * @param priority Updated priority
     */
    public static void updateItem(String listId, String todoId, String title, String desc, int priority) {
        db.collection("lists").document(listId)
                .collection("todo")
                .document(todoId)
                .update("title", title,
                        "description", desc,
                        "priority", priority);
    }

    /**
     * Method to add a user to a list
     *
     * @param listId   List id
     * @param memberId User id
     */
    public static void addMember(String listId, String memberId) {
        db.collection("lists").document(listId)
                .update("members", FieldValue.arrayUnion(memberId));
    }

    /**
     * Method to re-add user to list
     *
     * @param listId List id
     */
    public static void reAddMember(String listId) {
        db.collection("lists").document(listId)
                .update("members", FieldValue.arrayUnion(user.getUid()));
    }

    /**
     * Method to remove user from list
     *
     * @param listId List id
     */
    public static void removeMember(final String listId) {
        db.collection("lists").document(listId).
                update("members", FieldValue.arrayRemove(user.getUid()));
    }

    /**
     * Method to update completed field of an item
     *
     * @param listID    List id
     * @param itemID    Item id
     * @param completed
     */
    public static void updateCompleted(String listID, String itemID, boolean completed) {
        db.collection("lists").document(listID)
                .collection("todo")
                .document(itemID).update("completed", completed);
    }

    /**
     * Method to query all of the lists.
     *
     * @return The query to query all lists
     */
    public static Query getTodoLists() {
        return db.collection("lists").whereArrayContains("members", user.getUid());
    }

    /**
     * Method to update the title for a list
     *
     * @param listID List id
     * @param title  Updated list title
     */
    public static void updateList(String listID, String title) {
        db.collection("lists").document(listID)
                .update("title", title);
    }

    /**
     * Method to query all the items of a list
     *
     * @param listID List id
     * @return The query to query all items of list
     */
    public static Query getTodoItems(String listID) {
        return db.collection("lists").document(listID)
                .collection("todo")
                .orderBy("completed")
                .orderBy("priority", Query.Direction.DESCENDING);
    }
}
