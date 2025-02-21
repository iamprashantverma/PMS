package com.pms.projectservice.advices;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class APIError {

    /* Store the HttpStatus code*/
    private HttpStatus status;
    /* Error message*/
    private String message ;
}
