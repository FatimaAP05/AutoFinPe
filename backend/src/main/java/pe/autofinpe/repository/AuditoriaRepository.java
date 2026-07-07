package pe.autofinpe.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByUsuarioIdUsuarioOrderByFechaHoraDesc(Integer idUsuario);

    List<Auditoria> findByTablaAfectadaOrderByFechaHoraDesc(String tablaAfectada);
}
