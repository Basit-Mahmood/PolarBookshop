package pk.training.basit.polarbookshop.catalogservice.web.controller.rest.advice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * To handle errors for a REST API, we can use the standard Java exceptions and rely on a
 * @RestControllerAdvice class to define what to do when a given exception is thrown. It’s a
 * centralized approach that allows us to decouple the exception handling from the code throwing
 * the exception.
 */
@RestControllerAdvice // Marks the class as a centralized exception handler
public class BookControllerAdvice {

    // Defines the exception for which the handler must be executed
    @ExceptionHandler(BookNotFoundException.class)

    // Defines the status code for the HTTP response created when the exception is thrown
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String bookNotFoundHandler(BookNotFoundException ex) {
        // The message that will be included in the HTTP response body
        return ex.getMessage();
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String bookAlreadyExistsHandler(BookAlreadyExistsException ex) {
        return ex.getMessage();
    }

    // Handles the exception thrown when the Book validation fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            /**
             * Collects meaningful error messages about which Book fields were invalid instead of
             * returning an empty message
             */
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
