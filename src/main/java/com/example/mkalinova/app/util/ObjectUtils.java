package com.example.mkalinova.app.util;



import java.lang.reflect.Field;

public class ObjectUtils {


        public static boolean isAllFieldsNullOrEmpty(Object obj) {
            if (obj == null) return true;

            try {
                for (Field field : obj.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (value != null) {
                        if (value instanceof String) {
                            if (!((String) value).isBlank()) {
                                return false; // има непразно String поле
                            }
                        } else if (value instanceof Number) {
                            // Проверка за числови типове
                            if (((Number) value).doubleValue() != 0) {
                                return false; // числово поле != 0
                            }
                        } else if (!field.getType().isPrimitive()) {
                            // За всички други обекти
                            return false; // поле не е null
                        } else if (field.getType().isPrimitive()) {
                            // Примитиви като boolean, char и др.
                            if (field.getType() == boolean.class && (boolean) value) {
                                return false; // boolean true
                            }
                            if (field.getType() == char.class && (char) value != '\u0000') {
                                return false; // char различен от default
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
}