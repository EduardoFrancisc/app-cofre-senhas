package br.edu.infnet.gerenciador_senhas_api.repository;

import br.edu.infnet.gerenciador_senhas_api.model.Senha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SenhaRepository extends JpaRepository<Senha, Long> {

    List<Senha> findByUsuarioId(Long usuarioId);
}
