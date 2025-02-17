package com.pms.TaskService.auth;

public class UserContextHolder {
    private static final ThreadLocal<String> currentUserId = new  ThreadLocal<>();

    public static String getCurrentUserId(){
        return currentUserId.get();
    }

    static void setCurrentUserId(String userId){
        currentUserId.set(userId);
    }

    static void clear(){
        currentUserId.remove();
    }
}

