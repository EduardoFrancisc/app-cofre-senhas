package br.edu.infnet.gerenciador_senhas_api.dto;

import br.edu.infnet.gerenciador_senhas_api.model.Senha;

public record SenhaResponseDTO(
        Long id,
        String titulo,
        String login,
        String senha,
        String url,
        String observacoes,
        Long usuarioId
) {

    public static SenhaResponseDTO fromEntity(Senha senha) {
        return new SenhaResponseDTO(
                senha.getId(),
                senha.getTitulo(),
                senha.getLogin(),
                senha.getSenha(),
                senha.getUrl(),
                senha.getObservacoes(),
                senha.getUsuario().getId()
        );
    }
}
