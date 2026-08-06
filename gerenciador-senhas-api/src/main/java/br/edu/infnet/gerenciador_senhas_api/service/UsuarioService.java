package br.edu.infnet.gerenciador_senhas_api.service;

import br.edu.infnet.gerenciador_senhas_api.dto.UsuarioRequestDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.UsuarioResponseDTO;
import br.edu.infnet.gerenciador_senhas_api.dto.UsuarioUpdateDTO;
import br.edu.infnet.gerenciador_senhas_api.exception.EmailJaCadastradoException;
import br.edu.infnet.gerenciador_senhas_api.exception.UsuarioNaoEncontradoException;
import br.edu.infnet.gerenciador_senhas_api.model.Usuario;
import br.edu.infnet.gerenciador_senhas_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        log.info("Criando usuário com e-mail: {}", dto.email());

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        // TODO: substituir dto.senha() por passwordEncoder.encode(dto.senha()) ao integrar Spring Security
        Usuario novoUsuario = new Usuario(null, dto.nome(), dto.email(), dto.senha(), dto.tipoUsuario());
        Usuario salvo = usuarioRepository.save(novoUsuario);

        log.info("Usuário criado. ID: {}", salvo.getId());
        return UsuarioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponseDTO::fromEntity)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO dto) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        boolean emailAlterado = !usuario.getEmail().equalsIgnoreCase(dto.email());
        if (emailAlterado && usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTipoUsuario(dto.tipoUsuario());

        // TODO: substituir por passwordEncoder.encode(dto.senha()) ao integrar Spring Security
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(dto.senha());
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuário ID {} removido.", id);
    }
}
