package pe.autofinpe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Configuracion;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {

    Optional<Configuracion> findByMonedaAndTipoTasaAndCapitalizacionAndTipoGraciaAndMesesGracia(
            String moneda,
            String tipoTasa,
            Short capitalizacion,
            String tipoGracia,
            Byte mesesGracia
    );
}
