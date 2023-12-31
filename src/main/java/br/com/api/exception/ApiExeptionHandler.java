package br.com.api.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ControllerAdvice
public class ApiExeptionHandler extends ResponseEntityExceptionHandler {
    private MessageSource messageSource;

    // Identifica qual campo não foi prenchido e lança uma exception.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        //Lista os campos com erro.
        List<ErrorApi.Campo> campos = new ArrayList<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()){
            String nome = ((FieldError) error).getField();
            String mensagem = messageSource.getMessage(error, LocaleContextHolder.getLocale());

            campos.add(new ErrorApi.Campo(nome, mensagem));
        }

        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(status.value());
        errorApi.setDataHora(LocalDateTime.now());
        errorApi.setTitulo("Um ou mais campos estão vazios, não pode haver campos em branco.");
        errorApi.setCampos(campos);

        return handleExceptionInternal(ex,errorApi,headers,status,request);
    }

    //Erro 400.
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Object> handleNegocio(NegocioException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(status.value());
        errorApi.setDataHora(LocalDateTime.now());
        errorApi.setTitulo(ex.getMessage());
        return handleExceptionInternal(ex, errorApi, new HttpHeaders(), status,request);
    }
    //Erro 404.
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Object> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorApi errorApi = new ErrorApi();
        errorApi.setStatus(status.value());
        errorApi.setDataHora(LocalDateTime.now());
        errorApi.setTitulo(ex.getMessage());
        return handleExceptionInternal(ex, errorApi, new HttpHeaders(), status,request);
    }


}
