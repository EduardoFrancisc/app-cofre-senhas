package br.edu.infnet.gerenciador_senhas_api.controller;

import br.edu.infnet.gerenciador_senhas_api.dto.SenhaRequestDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.SenhaResponseDTO;
import br.edu.infnet.gerenciador_senhas_api.service.SenhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/senhas")
@RequiredArgsConstructor
public class SenhaController {

    private final SenhaService senhaService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<SenhaResponseDTO> criarSenha(
            @PathVariable Long usuarioId,
            @Valid @RequestBody SenhaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(senhaService.criarSenha(usuarioId, dto));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SenhaResponseDTO>> listarSenhasDoUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(senhaService.listarSenhasDoUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SenhaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(senhaService.buscarSenhaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SenhaResponseDTO> atualizarSenha(
            @PathVariable Long id,
            @Valid @RequestBody SenhaRequestDTO dto) {
        return ResponseEntity.ok(senhaService.atualizarSenha(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSenha(@PathVariable Long id) {
        senhaService.deletarSenha(id);
        return ResponseEntity.noContent().build();
    }
}
