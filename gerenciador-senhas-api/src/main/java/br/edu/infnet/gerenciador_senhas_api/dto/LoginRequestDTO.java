package br.edu.infnet.gerenciador_senhas_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail no formato válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}
