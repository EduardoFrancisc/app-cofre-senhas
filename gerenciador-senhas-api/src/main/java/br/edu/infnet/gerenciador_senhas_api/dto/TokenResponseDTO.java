package br.edu.infnet.gerenciador_senhas_api.dto;

public record TokenResponseDTO(
        String token,
        String tipo,
        long expiracaoEm,
        UsuarioResponseDTO usuario
) {
    public static TokenResponseDTO of(String token, long expiracaoEm, UsuarioResponseDTO usuario) {
        return new TokenResponseDTO(token, "Bearer", expiracaoEm, usuario);
    }

    public static TokenResponseDTO of(String token, long expiracaoEm) {
        return new TokenResponseDTO(token, "Bearer", expiracaoEm, null);
    }
}


