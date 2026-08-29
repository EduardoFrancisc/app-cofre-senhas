package br.edu.infnet.gerenciador_senhas_api.exception;

public class AesEncryptionException extends RuntimeException {

    public AesEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
