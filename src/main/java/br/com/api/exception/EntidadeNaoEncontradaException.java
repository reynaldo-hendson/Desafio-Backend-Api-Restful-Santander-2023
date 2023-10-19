package br.com.api.exception;

public class EntidadeNaoEncontradaException extends NegocioException{
    private static final long serialVersionUID = 1L;
    public EntidadeNaoEncontradaException(String message) {
        super("Resource not found.");
    }
}

