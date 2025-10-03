package com.example.mkalinova.app.user.data.entity;

public enum UsersRole {
    ADMIN("Администратор"),
    EDITOR("Редактор");

    public final String label;

    private UsersRole(String label) {
        this.label = label;
    }
}
