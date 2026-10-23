package br.edu.infnet.gerenciador_senhas_api.controller;

import br.edu.infnet.gerenciador_senhas_api.dto.LoginRequestDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.TokenResponseDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.UsuarioResponseDTO;
import br.edu.infnet.gerenciador_senhas_api.model.Usuario;
import br.edu.infnet.gerenciador_senhas_api.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, perfil e geração de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Realizar login", description = "Autentica com e-mail e senha. Retorna o token JWT e dados do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                    content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Tentativa de login: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = tokenService.gerarToken(userDetails);

        UsuarioResponseDTO usuarioDTO = null;
        if (userDetails instanceof Usuario usuario) {
            usuarioDTO = UsuarioResponseDTO.fromEntity(usuario);
        }

        log.info("Login bem-sucedido: {}", loginRequest.email());
        return ResponseEntity.ok(TokenResponseDTO.of(token, tokenService.extrairExpiracao(token), usuarioDTO));
    }

    @Operation(summary = "Obter dados do usuário logado", description = "Retorna os dados do perfil do usuário autenticado pelo token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil recuperado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getMe(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
    }
}

