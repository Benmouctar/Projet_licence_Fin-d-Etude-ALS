package dz.edu.univconstantine2.ntic.als.dto;

/**
 * Data Transfer Object (DTO) wrapping structural response data returned for Error queries.
 */
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;

    public ErrorResponse(int status, String message) {
        this.timestamp = java.time.Instant.now().toString();
        this.status = status;
        this.message = message;
    }

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
}
