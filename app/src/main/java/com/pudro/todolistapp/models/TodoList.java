package com.pudro.todolistapp.models;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class TodoList {
    private String documentID;
    private String title;
    private List<String> members;

    public TodoList(){

    }

    public TodoList(String title, List<String> members) {
        this.title = title;
        this.members = members;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
