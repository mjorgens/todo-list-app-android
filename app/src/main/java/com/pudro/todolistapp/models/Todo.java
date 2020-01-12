package com.pudro.todolistapp.models;

import com.google.firebase.firestore.Exclude;

public class Todo {
    private String documentID;
    private String title;
    private String description;
    private int priority;
    private String createdBy;
    private boolean completed;

    public Todo(){
    }

    public Todo(String title, String description, int priority, String createdBy){
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.createdBy = createdBy;
        this.completed = false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
