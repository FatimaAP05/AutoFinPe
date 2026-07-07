package pe.autofinpe.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.autofinpe.dto.configuracion.ConfiguracionRequest;
import pe.autofinpe.dto.configuracion.ConfiguracionResponse;
import pe.autofinpe.exception.BusinessException;
import pe.autofinpe.exception.DuplicateResourceException;
import pe.autofinpe.exception.ResourceNotFoundException;
import pe.autofinpe.model.entity.Configuracion;
import pe.autofinpe.repository.ConfiguracionRepository;
import pe.autofinpe.repository.OperacionRepository;

@Service
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final OperacionRepository operacionRepository;

    public ConfiguracionService(
            ConfiguracionRepository configuracionRepository,
            OperacionRepository operacionRepository
    ) {
        this.configuracionRepository = configuracionRepository;
        this.operacionRepository = operacionRepository;
    }

    @Transactional
    public ConfiguracionResponse crear(ConfiguracionRequest request) {
        validarConfiguracionNoDuplicada(request, null);

        Configuracion configuracion = new Configuracion();
        aplicarDatos(configuracion, request);

        return toResponse(configuracionRepository.save(configuracion));
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionResponse> listar() {
        return configuracionRepository.findAll(Sort.by("moneda").ascending()
                        .and(Sort.by("tipoTasa").ascending())
                        .and(Sort.by("capitalizacion").ascending())
                        .and(Sort.by("tipoGracia").ascending())
                        .and(Sort.by("mesesGracia").ascending()))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracionResponse buscarPorId(Integer idConfig) {
        return toResponse(obtenerEntidadPorId(idConfig));
    }

    @Transactional
    public ConfiguracionResponse actualizar(Integer idConfig, ConfiguracionRequest request) {
        Configuracion configuracion = obtenerEntidadPorId(idConfig);
        validarConfiguracionNoDuplicada(request, idConfig);

        aplicarDatos(configuracion, request);
        return toResponse(configuracionRepository.save(configuracion));
    }

    @Transactional
    public void eliminar(Integer idConfig) {
        Configuracion configuracion = obtenerEntidadPorId(idConfig);

        if (operacionRepository.existsByConfiguracionIdConfig(idConfig)) {
            throw new BusinessException("No se puede eliminar la configuracion porque tiene operaciones asociadas");
        }

        configuracionRepository.delete(configuracion);
    }

    private Configuracion obtenerEntidadPorId(Integer idConfig) {
        return configuracionRepository.findById(idConfig)
                .orElseThrow(() -> new ResourceNotFoundException("Configuracion no encontrada"));
    }

    private void validarConfiguracionNoDuplicada(ConfiguracionRequest request, Integer idConfigActual) {
        configuracionRepository.findByMonedaAndTipoTasaAndCapitalizacionAndTipoGraciaAndMesesGracia(
                        normalizarTexto(request.getMoneda()),
                        normalizarTexto(request.getTipoTasa()),
                        request.getCapitalizacion(),
                        normalizarTexto(request.getTipoGracia()),
                        request.getMesesGracia()
                )
                .filter(configuracion -> !configuracion.getIdConfig().equals(idConfigActual))
                .ifPresent(configuracion -> {
                    throw new DuplicateResourceException("Ya existe una configuracion con los parametros indicados");
                });
    }

    private void aplicarDatos(Configuracion configuracion, ConfiguracionRequest request) {
        configuracion.setMoneda(normalizarTexto(request.getMoneda()));
        configuracion.setTipoTasa(normalizarTexto(request.getTipoTasa()));
        configuracion.setCapitalizacion(request.getCapitalizacion());
        configuracion.setTipoGracia(normalizarTexto(request.getTipoGracia()));
        configuracion.setMesesGracia(request.getMesesGracia());
    }

    private String normalizarTexto(String value) {
        return value.trim().toUpperCase();
    }

    private ConfiguracionResponse toResponse(Configuracion configuracion) {
        return new ConfiguracionResponse(
                configuracion.getIdConfig(),
                configuracion.getMoneda(),
                configuracion.getTipoTasa(),
                configuracion.getCapitalizacion(),
                configuracion.getTipoGracia(),
                configuracion.getMesesGracia()
        );
    }
}
