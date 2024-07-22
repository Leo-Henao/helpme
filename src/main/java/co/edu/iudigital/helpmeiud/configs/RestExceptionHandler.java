package co.edu.iudigital.helpmeiud.configs;

import co.edu.iudigital.helpmeiud.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralException(Exception e) {
        // Registrar el errror completo para una depuración detallada
        log.error("Ocurrió un error inesperado: ", e);

        // Crear un mensaje de error detrallado
        String errorMessage = "Ocurrió un error inesperado. Por favor, contacta con el soporte con los siguientes detalles: "
                + e.getClass().getName() + " - " + e.getMessage();

        // Construir el ErrorDto con información más detallada
        ErrorDto errorDto = ErrorDto.getErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorDto> handleInternalServerError(InternalServerErrorException e) {
        // Registrar el mensaje de error completo para depuración
        log.error("Error interno del servidor: ", e);
        // Retornar la respuesta de error con la información de la excepción
        return new ResponseEntity<>(e.getErrorDto(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(NotFoundException e) {
        // Registrar el mensaje de la excepción
        log.info("Recurso no encontrado: {}", e.getMessage());
        // Retornar la respuesta de error con la información de la excepción
        return new ResponseEntity<>(e.getErrorDto(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(BadRequestException e) {
        // Registrar el mensaje de la excepción
        log.info("Solicitud incorrecta: {}", e.getErrorDto().getMessage());
        // Retornar la respuesta de error con la información de la excepción
        return new ResponseEntity<>(e.getErrorDto(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleUnauthorized(UnauthorizedException e) {
        // Registrar el mensaje de la excepción
        log.info("Acceso no autorizado: {}", e.getErrorDto().getMessage());
        // Retornar la respuesta de error con la información de la excepción
        return new ResponseEntity<>(e.getErrorDto(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDto> handleForbidden(ForbiddenException e) {
        // Registrar el mensaje de la excepción
        log.info("Acceso prohibido: {}", e.getErrorDto().getMessage());
        // Retornar la respuesta de error con la información de la excepción
        return new ResponseEntity<>(e.getErrorDto(), HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        // Combina todos los mensajes de error en una sola lista
        List<String> errorMessages = fieldErrors.stream()
                .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        // Si no hay errores, proporciona un mensaje genérico
        if (errorMessages.isEmpty()) {
            errorMessages.add("Ocurrió un error al procesar la solicitud. Por favor verifique e intente de nuevo.");
        }

        ErrorDto errorInfo = ErrorDto.getErrorDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                String.join("; ", errorMessages),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
