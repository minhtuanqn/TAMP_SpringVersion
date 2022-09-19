package com.tamp_backend.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utils class for current user
 */
public class UserUtils {

    /**
     * Find username of current user
     * @return username
     */
    public static String getCurUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * Find role of current user
     * @return role
     */
    public static String getCurRole() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getAuthorities().stream().findFirst().get().toString();
    }
}
