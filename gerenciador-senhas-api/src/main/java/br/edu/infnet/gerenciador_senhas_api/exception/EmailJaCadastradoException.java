package br.edu.infnet.gerenciador_senhas_api.exception;

/**
 * Exceção de negócio lançada quando há uma tentativa de cadastrar
 * um e-mail que já está registrado no sistema.
 *
 * <p>Estende {@link RuntimeException} para ser uma exceção não-verificada (unchecked),
 * dispensando a necessidade de {@code throws} ou blocos {@code try-catch} nos chamadores,
 * o que mantém o código do Service mais limpo e legível.</p>
 */
public class EmailJaCadastradoException extends RuntimeException {

    /**
     * Constrói a exceção com uma mensagem descritiva.
     *
     * @param email o e-mail duplicado que causou o conflito.
     */
    public EmailJaCadastradoException(String email) {
        super("O e-mail informado já está cadastrado no sistema: " + email);
    }
}
