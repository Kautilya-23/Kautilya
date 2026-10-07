package com.equilym.opd.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException e, HttpServletRequest r) { return build(HttpStatus.NOT_FOUND, e.getMessage(), r); }
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<Map<String, Object>> conflict(ConflictException e, HttpServletRequest r) { return build(HttpStatus.CONFLICT, e.getMessage(), r); }
    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<Map<String, Object>> bad(Exception e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, e instanceof BadRequestException ? e.getMessage() : "Malformed request", r); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e, HttpServletRequest r) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + " " + f.getDefaultMessage()).collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg, r); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException e, HttpServletRequest r) { return build(HttpStatus.CONFLICT, "Duplicate or invalid data", r); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> other(Exception e, HttpServletRequest r) {
        log.error("Unhandled error", e); return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.", r); }

    private ResponseEntity<Map<String, Object>> build(HttpStatus s, String m, HttpServletRequest r) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("timestamp", LocalDateTime.now().toString()); b.put("status", s.value()); b.put("message", m); b.put("path", r.getRequestURI());
        return ResponseEntity.status(s).body(b);
    }
}
