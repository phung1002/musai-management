package musai.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import musai.app.DTO.MessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFoundException(NotFoundException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageResponse> handleBadRequestException(BadRequestException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponse);
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<MessageResponse> handleForbiddenException(ForbiddenException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(messageResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGeneralException(Exception ex) {
        MessageResponse messageResponse = new MessageResponse("An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponse> handleAccessDeniedException(AccessDeniedException ex) {
        MessageResponse messageResponse = new MessageResponse("You do not have permission to access this resource.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageResponse);
    }
}
