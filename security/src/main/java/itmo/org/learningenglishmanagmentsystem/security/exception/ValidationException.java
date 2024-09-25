package itmo.org.learningenglishmanagmentsystem.security.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
