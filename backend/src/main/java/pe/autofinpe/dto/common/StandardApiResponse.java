package pe.autofinpe.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private Instant timestamp;

    public StandardApiResponse() {
    }

    private StandardApiResponse(boolean success, String message, T data, List<String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    public static <T> StandardApiResponse<T> success(String message, T data) {
        return new StandardApiResponse<>(true, message, data, null);
    }

    public static StandardApiResponse<Void> success(String message) {
        return new StandardApiResponse<>(true, message, null, null);
    }

    public static StandardApiResponse<Void> error(String message, List<String> errors) {
        return new StandardApiResponse<>(false, message, null, errors);
    }

    public static StandardApiResponse<Void> error(String message, String error) {
        return error(message, List.of(error));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
