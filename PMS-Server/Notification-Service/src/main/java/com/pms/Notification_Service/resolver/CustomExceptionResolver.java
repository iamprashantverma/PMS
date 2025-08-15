package com.pms.Notification_Service.resolver;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Slf4j
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

        if (ex instanceof DataAccessException || ex.getCause() instanceof SQLException) {
            log.error("Database Error: {}", ex.getMessage(), ex);
            return buildError(env, ErrorType.INTERNAL_ERROR, "A database error occurred. Please try again later.");
        }
        else if (ex instanceof MessagingException) {
            log.error("Email Sending Error: {}", ex.getMessage(), ex);
            return buildError(env, ErrorType.INTERNAL_ERROR, "Failed to send email notification. Please try again later.");
        }
        else if (ex instanceof RuntimeException) {
            log.error("Runtime Exception: {}", ex.getMessage(), ex);
            return buildError(env, ErrorType.INTERNAL_ERROR, "An unexpected runtime error occurred.");
        }
        else {
            log.error("Unexpected Error: {}", ex.getMessage(), ex);
            return buildError(env, ErrorType.INTERNAL_ERROR, "Internal Server Error: " + ex.getMessage());
        }
    }

    private GraphQLError buildError(DataFetchingEnvironment env, ErrorType errorType, String message) {
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(message)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

}
