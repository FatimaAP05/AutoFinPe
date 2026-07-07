package pe.autofinpe.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.autofinpe.dto.common.StandardApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException exception) {
        return buildError(ApiStatusCode.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleDuplicateResource(DuplicateResourceException exception) {
        return buildError(ApiStatusCode.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleBusinessException(BusinessException exception) {
        return buildError(ApiStatusCode.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildError(ApiStatusCode.BAD_REQUEST, "Datos de entrada invalidos", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return buildError(ApiStatusCode.BAD_REQUEST, "Datos de entrada invalidos", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException exception) {
        return buildError(ApiStatusCode.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato valido");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleDisabledUser(DisabledException exception) {
        return buildError(ApiStatusCode.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<StandardApiResponse<Void>> handleAuthentication(AuthenticationException exception) {
        return buildError(ApiStatusCode.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException exception) {
        return buildError(ApiStatusCode.CONFLICT, "La operacion viola una restriccion de datos");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardApiResponse<Void>> handleUnexpected(Exception exception) {
        return buildError(ApiStatusCode.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<StandardApiResponse<Void>> buildError(ApiStatusCode statusCode, String message) {
        return buildError(statusCode, message, List.of(message));
    }

    private ResponseEntity<StandardApiResponse<Void>> buildError(
            ApiStatusCode statusCode,
            String message,
            List<String> errors
    ) {
        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(StandardApiResponse.error(message, errors));
    }
}
