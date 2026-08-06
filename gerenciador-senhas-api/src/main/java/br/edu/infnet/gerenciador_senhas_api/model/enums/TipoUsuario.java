package br.edu.infnet.gerenciador_senhas_api.model.enums;

/**
 * Enum que representa os tipos de perfil de um usuário no sistema.
 */
public enum TipoUsuario {

    /** Administrador com acesso total ao sistema. */
    ADMIN,

    /** Usuário comum com acesso restrito às suas próprias senhas. */
    USUARIO_LEIGO
}
