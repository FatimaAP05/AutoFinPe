package pe.autofinpe.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Operacion;
import pe.autofinpe.model.entity.Cronograma;

public interface CronogramaRepository extends JpaRepository<Cronograma, Integer> {

    List<Cronograma> findByOperacionOrderByNroCuotaAsc(Operacion operacion);
}
