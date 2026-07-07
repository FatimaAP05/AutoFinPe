package pe.autofinpe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Operacion;
import pe.autofinpe.model.entity.Indicador;

public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {

    Optional<Indicador> findByOperacion(Operacion operacion);
}
