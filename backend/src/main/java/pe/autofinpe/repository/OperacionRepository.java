package pe.autofinpe.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.autofinpe.model.entity.Operacion;

public interface OperacionRepository extends JpaRepository<Operacion, Integer> {

    @Query("""
            SELECT o
            FROM Operacion o
            JOIN FETCH o.cliente
            JOIN FETCH o.vehiculo
            JOIN FETCH o.configuracion
            JOIN FETCH o.usuario
            LEFT JOIN FETCH o.indicador
            WHERE o.idOperacion = :idOperacion
            """)
    Optional<Operacion> findByIdWithDetails(@Param("idOperacion") Integer idOperacion);

    @Query("""
            SELECT o
            FROM Operacion o
            JOIN FETCH o.cliente
            JOIN FETCH o.vehiculo
            JOIN FETCH o.configuracion
            JOIN FETCH o.usuario
            LEFT JOIN FETCH o.indicador
            ORDER BY o.fecha DESC
            """)
    List<Operacion> findAllWithDetailsOrderByFechaDesc();

    List<Operacion> findByClienteIdCliente(Integer idCliente);

    boolean existsByVehiculoIdVehiculo(Integer idVehiculo);

    boolean existsByConfiguracionIdConfig(Integer idConfig);

    List<Operacion> findByUsuarioIdUsuario(Integer idUsuario);

    List<Operacion> findByEstado(String estado);
}
