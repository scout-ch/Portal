package ch.itds.pbs.portal.controller.api.v1;

import ch.itds.pbs.portal.dto.error.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(code = NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            Exception ex) {
        return buildResponseEntity(ex, NOT_FOUND);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    @ResponseStatus(code = FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            Exception ex) {
        return buildResponseEntity(ex, FORBIDDEN);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(code = BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            Exception ex) {
        return buildResponseEntity(ex, BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder().code(status.value()).message(ex.getMessage()).build(), new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> fallbackExceptionHandler(
            Exception ex) {
        logger.error("unexpected internal error", ex);
        return buildResponseEntity(ex, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(Exception e, HttpStatus status) {
        return new ResponseEntity<>(ErrorResponse.builder().code(status.value()).message(e.getMessage()).build(), new HttpHeaders(), status);
    }
}

