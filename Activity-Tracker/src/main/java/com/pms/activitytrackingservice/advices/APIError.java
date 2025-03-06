package com.pms.activitytrackingservice.advices;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class APIError {
   private HttpStatus status;
   private String message ;
}
