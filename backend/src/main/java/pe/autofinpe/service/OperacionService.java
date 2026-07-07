package pe.autofinpe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.autofinpe.dto.operacion.CronogramaResponse;
import pe.autofinpe.dto.operacion.IndicadorResponse;
import pe.autofinpe.dto.operacion.OperacionRequest;
import pe.autofinpe.dto.operacion.OperacionResponse;
import pe.autofinpe.dto.simulador.SimuladorRequest;
import pe.autofinpe.dto.simulador.SimuladorResponse;
import pe.autofinpe.dto.simulador.CronogramaSimulacionResponse;
import pe.autofinpe.dto.simulador.IndicadoresSimulacionResponse;
import pe.autofinpe.exception.ResourceNotFoundException;
import pe.autofinpe.model.entity.Cliente;
import pe.autofinpe.model.entity.Configuracion;
import pe.autofinpe.model.entity.Cronograma;
import pe.autofinpe.model.entity.Indicador;
import pe.autofinpe.model.entity.Operacion;
import pe.autofinpe.model.entity.Usuario;
import pe.autofinpe.model.entity.Vehiculo;
import pe.autofinpe.model.entity.type.OperacionEstado;
import pe.autofinpe.repository.ClienteRepository;
import pe.autofinpe.repository.ConfiguracionRepository;
import pe.autofinpe.repository.CronogramaRepository;
import pe.autofinpe.repository.IndicadorRepository;
import pe.autofinpe.repository.OperacionRepository;
import pe.autofinpe.repository.UsuarioRepository;
import pe.autofinpe.repository.VehiculoRepository;

@Service
@Transactional
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CronogramaRepository cronogramaRepository;
    private final IndicadorRepository indicadorRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConfiguracionRepository configuracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SimuladorService simuladorService;

    public OperacionService(
            OperacionRepository operacionRepository,
            CronogramaRepository cronogramaRepository,
            IndicadorRepository indicadorRepository,
            ClienteRepository clienteRepository,
            VehiculoRepository vehiculoRepository,
            ConfiguracionRepository configuracionRepository,
            UsuarioRepository usuarioRepository,
            SimuladorService simuladorService
    ) {
        this.operacionRepository = operacionRepository;
        this.cronogramaRepository = cronogramaRepository;
        this.indicadorRepository = indicadorRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.configuracionRepository = configuracionRepository;
        this.usuarioRepository = usuarioRepository;
        this.simuladorService = simuladorService;
    }

    /**
     * Crear operación guardando simulación, cronograma e indicadores
     */
    public OperacionResponse crearOperacion(OperacionRequest request) {
        // Validar que existan cliente, vehículo, configuración
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Vehiculo vehiculo = vehiculoRepository.findById(request.getIdVehiculo())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        Configuracion configuracion = configuracionRepository.findById(request.getIdConfiguracion())
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Preparar request para simulador
        SimuladorRequest simRequest = new SimuladorRequest();
        simRequest.setPrecioVehiculo(
                Objects.equals(request.getMoneda(), "PEN") ? vehiculo.getPrecioPen() : vehiculo.getPrecioUsd()
        );

        simRequest.setMoneda(request.getMoneda());
        simRequest.setPlazoMeses(request.getPlazoMeses());
        simRequest.setCuotaInicialPct(request.getCuotaInicialPct());
        simRequest.setCuotaBalonPct(request.getCuotaBalonPct());
        simRequest.setValorTasaPct(request.getValorTasaPct());
        simRequest.setTipoTasa(request.getTipoTasa());
        simRequest.setCapitalizacion(configuracion.getCapitalizacion());
        simRequest.setTipoGracia(request.getTipoGracia());
        simRequest.setMesesGracia(request.getMesesGracia());
        simRequest.setSeguroDesgravamenPctMensual(request.getSeguroDesgravamenPct());
        simRequest.setSeguroVehicularPctAnual(request.getSeguroVehicularPct());
        simRequest.setPortesMensuales(request.getPortesMensuales());
        simRequest.setCokAnualPct(request.getCokAnualPct());

        // Calcular simulación
        SimuladorResponse simResponse = simuladorService.calcular(simRequest);

        // Crear y guardar Operación
        Operacion operacion = new Operacion();
        operacion.setCliente(cliente);
        operacion.setVehiculo(vehiculo);
        operacion.setConfiguracion(configuracion);
        operacion.setUsuario(usuario);
        operacion.setFecha(LocalDateTime.now());
        operacion.setPlazo(request.getPlazoMeses());
        operacion.setCuotaInicialPct(request.getCuotaInicialPct());
        operacion.setCuotaBalonPct(request.getCuotaBalonPct());
        operacion.setValorTasa(request.getValorTasaPct());
        operacion.setEstado(OperacionEstado.REGISTRADA.name());

        Operacion operacionGuardada = operacionRepository.save(operacion);

        // Guardar cronogramas
        List<Cronograma> cronogramas = simResponse.getCronograma().stream()
                .map(cron -> {
                    Cronograma c = new Cronograma();
                    c.setOperacion(operacionGuardada);
                    c.setNroCuota(cron.getNroCuota());
                    c.setSaldoInicial(cron.getSaldoInicial());
                    c.setInteres(cron.getInteres());
                    c.setAmortizacion(cron.getAmortizacion());
                    c.setSeguroDesgrav(cron.getSeguroDesgravamen());
                    c.setCuotaCredito(cron.getCuotaCredito());
                    c.setSeguroVehic(cron.getSeguroVehicular());
                    c.setPortes(cron.getPortes());
                    c.setCuotaTotal(cron.getCuotaTotal());
                    c.setSaldoFinal(cron.getSaldoFinal());
                    return c;
                })
                .collect(Collectors.toList());
        cronogramaRepository.saveAll(cronogramas);

        // Guardar indicador
        Indicador indicador = new Indicador();
        indicador.setOperacion(operacionGuardada);
        IndicadoresSimulacionResponse indResponse = simResponse.getIndicadores();
        indicador.setTcea(indResponse.getTcea());
        indicador.setVan(indResponse.getVan());
        indicador.setTir(indResponse.getTirAnual());
        indicador.setTotalIntereses(indResponse.getTotalIntereses());
        indicador.setTotalAmortizacion(indResponse.getTotalAmortizacion());
        indicador.setTotalSeguros(indResponse.getTotalSeguros());
        indicador.setTotalPortes(indResponse.getTotalPortes());
        indicador.setTotalPagado(indResponse.getTotalPagado());
        indicadorRepository.save(indicador);

        // Mapear respuesta
        return mapToResponse(operacionGuardada, simResponse);
    }

    /**
     * Listar operaciones ordenadas de la mas reciente a la mas antigua.
     */
    @Transactional(readOnly = true)
    public List<OperacionResponse> listarOperaciones() {
        return operacionRepository.findAllWithDetailsOrderByFechaDesc().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener operación por ID con cronograma e indicadores
     */
    @Transactional(readOnly = true)
    public OperacionResponse obtenerOperacion(Integer idOperacion) {
        Operacion operacion = operacionRepository.findByIdWithDetails(idOperacion)
                .orElseThrow(() -> new ResourceNotFoundException("Operación no encontrada"));

        // Obtener cronogramas
        List<Cronograma> cronogramas = cronogramaRepository.findByOperacionOrderByNroCuotaAsc(operacion);
        List<CronogramaResponse> cronogramaResponses = cronogramas.stream()
                .map(c -> new CronogramaResponse(
                        c.getNroCuota(),
                        c.getSaldoInicial(),
                        c.getInteres(),
                        c.getAmortizacion(),
                        c.getSeguroDesgrav(),
                        c.getSeguroVehic(),
                        c.getPortes(),
                        c.getCuotaCredito(),
                        c.getCuotaTotal(),
                        c.getSaldoFinal()
                ))
                .collect(Collectors.toList());

        // Obtener indicador
        Indicador indicador = operacion.getIndicador();
        if (indicador == null) {
            throw new ResourceNotFoundException("Indicadores no encontrados");
        }

        IndicadorResponse indicadorResponse = new IndicadorResponse(
                indicador.getIdIndicador(),
                indicador.getTcea(),
                indicador.getVan(),
                indicador.getTir(),
                indicador.getTotalIntereses(),
                indicador.getTotalAmortizacion(),
                indicador.getTotalSeguros(),
                indicador.getTotalPortes(),
                indicador.getTotalPagado()
        );

        // Mapear respuesta
        OperacionResponse response = new OperacionResponse();
        response.setIdOperacion(operacion.getIdOperacion());
        response.setIdCliente(operacion.getCliente().getIdCliente());
        response.setClienteNombre(operacion.getCliente().getNombres() + " " + operacion.getCliente().getApellidos());
        response.setIdVehiculo(operacion.getVehiculo().getIdVehiculo());
        response.setVehiculoModelo(operacion.getVehiculo().getModelo());
        response.setIdConfiguracion(operacion.getConfiguracion().getIdConfig());
        response.setFecha(operacion.getFecha());
        response.setPlazo(operacion.getPlazo());
        response.setCuotaInicialPct(operacion.getCuotaInicialPct());
        response.setCuotaBalonPct(operacion.getCuotaBalonPct());
        response.setValorTasa(operacion.getValorTasa());
        response.setEstado(operacion.getEstado());
        response.setIndicador(indicadorResponse);
        response.setCronograma(cronogramaResponses);

        return response;
    }

    /**
     * Obtener cronograma de operación
     */
    @Transactional(readOnly = true)
    public List<CronogramaResponse> obtenerCronograma(Integer idOperacion) {
        Operacion operacion = operacionRepository.findById(idOperacion)
                .orElseThrow(() -> new ResourceNotFoundException("Operación no encontrada"));

        List<Cronograma> cronogramas = cronogramaRepository.findByOperacionOrderByNroCuotaAsc(operacion);
        return cronogramas.stream()
                .map(c -> new CronogramaResponse(
                        c.getNroCuota(),
                        c.getSaldoInicial(),
                        c.getInteres(),
                        c.getAmortizacion(),
                        c.getSeguroDesgrav(),
                        c.getSeguroVehic(),
                        c.getPortes(),
                        c.getCuotaCredito(),
                        c.getCuotaTotal(),
                        c.getSaldoFinal()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtener indicadores de operación
     */
    @Transactional(readOnly = true)
    public IndicadorResponse obtenerIndicadores(Integer idOperacion) {
        Operacion operacion = operacionRepository.findById(idOperacion)
                .orElseThrow(() -> new ResourceNotFoundException("Operación no encontrada"));

        Indicador indicador = indicadorRepository.findByOperacion(operacion)
                .orElseThrow(() -> new ResourceNotFoundException("Indicadores no encontrados"));

        return new IndicadorResponse(
                indicador.getIdIndicador(),
                indicador.getTcea(),
                indicador.getVan(),
                indicador.getTir(),
                indicador.getTotalIntereses(),
                indicador.getTotalAmortizacion(),
                indicador.getTotalSeguros(),
                indicador.getTotalPortes(),
                indicador.getTotalPagado()
        );
    }

    private OperacionResponse mapToSummaryResponse(Operacion operacion) {
        OperacionResponse response = new OperacionResponse();
        response.setIdOperacion(operacion.getIdOperacion());
        response.setIdCliente(operacion.getCliente().getIdCliente());
        response.setClienteNombre(operacion.getCliente().getNombres() + " " + operacion.getCliente().getApellidos());
        response.setIdVehiculo(operacion.getVehiculo().getIdVehiculo());
        response.setVehiculoModelo(operacion.getVehiculo().getModelo());
        response.setIdConfiguracion(operacion.getConfiguracion().getIdConfig());
        response.setFecha(operacion.getFecha());
        response.setPlazo(operacion.getPlazo());
        response.setCuotaInicialPct(operacion.getCuotaInicialPct());
        response.setCuotaBalonPct(operacion.getCuotaBalonPct());
        response.setValorTasa(operacion.getValorTasa());
        response.setEstado(operacion.getEstado());

        Indicador indicador = operacion.getIndicador();
        if (indicador != null) {
            response.setIndicador(new IndicadorResponse(
                    indicador.getIdIndicador(),
                    indicador.getTcea(),
                    indicador.getVan(),
                    indicador.getTir(),
                    indicador.getTotalIntereses(),
                    indicador.getTotalAmortizacion(),
                    indicador.getTotalSeguros(),
                    indicador.getTotalPortes(),
                    indicador.getTotalPagado()
            ));
        }

        response.setCronograma(List.of());
        return response;
    }

    private OperacionResponse mapToResponse(Operacion operacion, SimuladorResponse simResponse) {
        OperacionResponse response = new OperacionResponse();
        response.setIdOperacion(operacion.getIdOperacion());
        response.setIdCliente(operacion.getCliente().getIdCliente());
        response.setClienteNombre(operacion.getCliente().getNombres() + " " + operacion.getCliente().getApellidos());
        response.setIdVehiculo(operacion.getVehiculo().getIdVehiculo());
        response.setVehiculoModelo(operacion.getVehiculo().getModelo());
        response.setIdConfiguracion(operacion.getConfiguracion().getIdConfig());
        response.setFecha(operacion.getFecha());
        response.setPlazo(operacion.getPlazo());
        response.setCuotaInicialPct(operacion.getCuotaInicialPct());
        response.setCuotaBalonPct(operacion.getCuotaBalonPct());
        response.setValorTasa(operacion.getValorTasa());
        response.setEstado(operacion.getEstado());

        // Mapear indicador
        IndicadorResponse indicador = new IndicadorResponse(
                null,
                simResponse.getIndicadores().getTcea(),
                simResponse.getIndicadores().getVan(),
                simResponse.getIndicadores().getTirAnual(),
                simResponse.getIndicadores().getTotalIntereses(),
                simResponse.getIndicadores().getTotalAmortizacion(),
                simResponse.getIndicadores().getTotalSeguros(),
                simResponse.getIndicadores().getTotalPortes(),
                simResponse.getIndicadores().getTotalPagado()
        );
        response.setIndicador(indicador);

        // Mapear cronograma
        List<CronogramaResponse> cronograma = simResponse.getCronograma().stream()
                .map(c -> new CronogramaResponse(
                        c.getNroCuota(),
                        c.getSaldoInicial(),
                        c.getInteres(),
                        c.getAmortizacion(),
                        c.getSeguroDesgravamen(),
                        c.getSeguroVehicular(),
                        c.getPortes(),
                        c.getCuotaCredito(),
                        c.getCuotaTotal(),
                        c.getSaldoFinal()
                ))
                .collect(Collectors.toList());
        response.setCronograma(cronograma);

        return response;
    }
}
