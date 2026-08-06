package br.edu.infnet.gerenciador_senhas_api.exception;

/**
 * Exceção de negócio lançada quando um usuário não é encontrado
 * no sistema pelo identificador informado.
 *
 * <p>Estende {@link RuntimeException} para ser uma exceção não-verificada (unchecked),
 * mantendo o código dos chamadores (Service, Controller) limpo e sem ruído sintático.</p>
 */
public class UsuarioNaoEncontradoException extends RuntimeException {

    /**
     * Constrói a exceção com uma mensagem descritiva contendo o ID pesquisado.
     *
     * @param id o identificador do usuário que não foi encontrado.
     */
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado com o ID: " + id);
    }
}
