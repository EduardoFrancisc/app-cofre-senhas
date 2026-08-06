package br.edu.infnet.gerenciador_senhas_api.dto;

import br.edu.infnet.gerenciador_senhas_api.model.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail no formato válido (ex: usuario@dominio.com).")
        String email,

        // Opcional: null ou em branco mantém a senha atual
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String senha,

        @NotNull(message = "O tipo de usuário é obrigatório.")
        TipoUsuario tipoUsuario
) {
}
