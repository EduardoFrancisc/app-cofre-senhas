package br.edu.infnet.gerenciador_senhas_api.dto;

/**
 * DTO de resposta retornado após um login bem-sucedido.
 *
 * @param token       o token JWT gerado
 * @param tipo        tipo do token (sempre {@code "Bearer"})
 * @param expiracaoEm timestamp Unix em milissegundos indicando quando o token expira
 */
public record TokenResponseDTO(
        String token,
        String tipo,
        long expiracaoEm
) {
    /**
     * Factory method para construir a resposta a partir do token gerado.
     *
     * @param token      JWT gerado pelo {@code JwtService}
     * @param expiracaoEm timestamp de expiração em ms (Unix epoch)
     * @return instância de {@link TokenResponseDTO}
     */
    public static TokenResponseDTO of(String token, long expiracaoEm) {
        return new TokenResponseDTO(token, "Bearer", expiracaoEm);
    }
}
