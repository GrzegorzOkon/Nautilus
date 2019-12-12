package okon.Nautilus;

public class AppException extends RuntimeException {
    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message) {
        super(message);
    }
}
