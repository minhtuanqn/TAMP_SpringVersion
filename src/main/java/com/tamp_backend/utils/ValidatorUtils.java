package com.tamp_backend.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Validation class
 */
public class ValidatorUtils {
    /**
     * check not negative number
     * @param number
     * @param numberError
     * @param fieldName
     * @return number object
     */
    public static Integer checkNotNegativeInt(String number, Map<String, String> numberError, String fieldName) {
        Integer checkedNum = null;
        if(!number.matches("[0-9]{1,}")) {
            numberError.put(fieldName, "must be a number and must not be negative");
            return null;
        } else {
            return Integer.parseInt(number);
        }
    }


    /**
     * check one positive number or not
     * @param number
     * @param numberError
     * @param fieldName
     * @return positive or not
     */
    public static boolean checkPositive(Integer number, Map<String, String> numberError, String fieldName) {
        if(number <= 0) {
            numberError.put(fieldName, "must be positive");
            return false;
        }
        return true;
    }

    /**
     * check one field is exist in a class or not
     * @param checkedClass
     * @param fieldName
     * @return
     */
    public static boolean checkExistFieldOfClass(Class<? extends Object> checkedClass, String fieldName) {
        try {
            Field field = checkedClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Map<String, String> notExistField = new HashMap<>();
            notExistField.put(fieldName, "does not exist");
            return false;
        }
        return true;
    }
}
