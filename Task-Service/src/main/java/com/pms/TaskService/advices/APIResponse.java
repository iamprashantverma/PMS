package com.pms.TaskService.advices;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class APIResponse <T> {
    private LocalDateTime timeStamp;
    private T data;
    private APIError error;

    /* Non Parameterized Constructor to initialize the timeStamp */
    APIResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    /* initialize the Generic Data  */
    APIResponse(T data) {
        /* call the Non Parametrized Constructor */
        this();
        this.data = data;
    }

    /* initialize the APIError field */
    APIResponse(APIError error) {
        this();
        this.error= error;
    }

}
