package com.pms.TaskService.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.TaskService.exceptions.ResourceNotFound;
import feign.FeignException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.sql.SQLException;

@Component
@Slf4j
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ResourceNotFound) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof FeignException.NotFound) {
            log.warn("Feign Client Error (404): {}", ex.getMessage());
            String errorMessage = extractFeignErrorMessage((FeignException) ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(errorMessage)
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof FeignException) {
            log.error("Feign Client Exception: {}", ex.getMessage(), ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("External service error: " + ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof DataAccessException || (ex.getCause() instanceof SQLException)) {
            log.error("Database Error: {}", ex.getMessage(), ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("A database error occurred. Please try again later.")
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else {
            log.error("Unexpected Error: {}", ex.getMessage(), ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("Internal Server Error: " + ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
    }

    private String extractFeignErrorMessage(FeignException ex) {
        try {
            String responseBody = ex.contentUTF8();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown error from external service";
        } catch (IOException e) {
            log.error("Failed to parse Feign error response: {}", e.getMessage(), e);
            return "Error communicating with external service";
        }
    }
}