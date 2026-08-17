package br.edu.infnet.gerenciador_senhas_api.exception;

public class SenhaNaoEncontradaException extends RuntimeException {

    public SenhaNaoEncontradaException(Long id) {
        super("Senha não encontrada com o ID: " + id);
    }
}
