package br.edu.infnet.gerenciador_senhas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SenhaRequestDTO(

        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
        String titulo,

        @NotBlank(message = "O login é obrigatório.")
        @Size(max = 150, message = "O login deve ter no máximo 150 caracteres.")
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        String senha,

        String url,

        String observacoes
) {
}
