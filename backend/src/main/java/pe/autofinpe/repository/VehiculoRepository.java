package pe.autofinpe.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.autofinpe.model.entity.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    List<Vehiculo> findByMarcaIgnoreCase(String marca);

    List<Vehiculo> findByCategoriaIgnoreCase(String categoria);
}
