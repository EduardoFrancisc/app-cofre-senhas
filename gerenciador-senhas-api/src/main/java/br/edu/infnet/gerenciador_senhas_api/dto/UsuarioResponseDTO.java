package br.edu.infnet.gerenciador_senhas_api.dto;

import br.edu.infnet.gerenciador_senhas_api.model.Usuario;
import br.edu.infnet.gerenciador_senhas_api.model.enums.TipoUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }
}
