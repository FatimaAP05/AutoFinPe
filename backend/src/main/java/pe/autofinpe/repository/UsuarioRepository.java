package pe.autofinpe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);
}
