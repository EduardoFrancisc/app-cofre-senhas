package br.edu.infnet.gerenciador_senhas_api.service;

import br.edu.infnet.gerenciador_senhas_api.dto.SenhaRequestDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.SenhaResponseDTO;
import br.edu.infnet.gerenciador_senhas_api.exception.SenhaNaoEncontradaException;
import br.edu.infnet.gerenciador_senhas_api.exception.UsuarioNaoEncontradoException;
import br.edu.infnet.gerenciador_senhas_api.model.Senha;
import br.edu.infnet.gerenciador_senhas_api.model.Usuario;
import br.edu.infnet.gerenciador_senhas_api.repository.SenhaRepository;
import br.edu.infnet.gerenciador_senhas_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenhaService {

    private final SenhaRepository senhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AesEncryptionService aesEncryptionService;

    @Transactional
    public SenhaResponseDTO criarSenha(Long usuarioId, SenhaRequestDTO dto) {
        log.info("Criando senha '{}' para o usuário ID: {}", dto.titulo(), usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        Senha novaSenha = new Senha();
        novaSenha.setTitulo(dto.titulo());
        novaSenha.setLogin(dto.login());
        novaSenha.setSenha(aesEncryptionService.criptografar(dto.senha()));
        novaSenha.setUrl(dto.url());
        novaSenha.setObservacoes(dto.observacoes());
        novaSenha.setUsuario(usuario);

        Senha salva = senhaRepository.save(novaSenha);
        log.info("Senha criada. ID: {}", salva.getId());
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<SenhaResponseDTO> listarSenhasDoUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException(usuarioId);
        }
        return senhaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SenhaResponseDTO buscarSenhaPorId(Long senhaId) {
        return senhaRepository.findById(senhaId)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new SenhaNaoEncontradaException(senhaId));
    }

    @Transactional
    public SenhaResponseDTO atualizarSenha(Long senhaId, SenhaRequestDTO dto) {
        log.info("Atualizando senha ID: {}", senhaId);

        Senha senha = senhaRepository.findById(senhaId)
                .orElseThrow(() -> new SenhaNaoEncontradaException(senhaId));

        senha.setTitulo(dto.titulo());
        senha.setLogin(dto.login());
        senha.setSenha(aesEncryptionService.criptografar(dto.senha()));
        senha.setUrl(dto.url());
        senha.setObservacoes(dto.observacoes());

        return toResponseDTO(senhaRepository.save(senha));
    }

    @Transactional
    public void deletarSenha(Long senhaId) {
        if (!senhaRepository.existsById(senhaId)) {
            throw new SenhaNaoEncontradaException(senhaId);
        }
        senhaRepository.deleteById(senhaId);
        log.info("Senha ID {} removida.", senhaId);
    }

    private SenhaResponseDTO toResponseDTO(Senha senha) {
        return new SenhaResponseDTO(
                senha.getId(),
                senha.getTitulo(),
                senha.getLogin(),
                aesEncryptionService.descriptografar(senha.getSenha()),
                senha.getUrl(),
                senha.getObservacoes(),
                senha.getUsuario().getId()
        );
    }
}
