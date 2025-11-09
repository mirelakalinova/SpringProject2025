package com.example.mkalinova.app.user.data.entity;

public enum UsersRole {
    ADMIN("Администратор"),
    EDITOR("Редактор");

    public final String label;

    private UsersRole(String label) {
        this.label = label;
    }

    public static UsersRole findRole(String label) {
        for (UsersRole role : UsersRole.values()) {
            if (role.label.equalsIgnoreCase(label)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Няма роля с етикет: " + label);
    }
}
