package pe.autofinpe.dto.operacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OperacionResponse {

    private Integer idOperacion;
    private Integer idCliente;
    private String clienteNombre;
    private Integer idVehiculo;
    private String vehiculoModelo;
    private Integer idConfiguracion;
    private LocalDateTime fecha;
    private Integer plazo;
    private BigDecimal cuotaInicialPct;
    private BigDecimal cuotaBalonPct;
    private BigDecimal valorTasa;
    private String estado;
    private IndicadorResponse indicador;
    private List<CronogramaResponse> cronograma;

    public OperacionResponse() {
    }

    public Integer getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(Integer idOperacion) {
        this.idOperacion = idOperacion;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getVehiculoModelo() {
        return vehiculoModelo;
    }

    public void setVehiculoModelo(String vehiculoModelo) {
        this.vehiculoModelo = vehiculoModelo;
    }

    public Integer getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Integer idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }

    public BigDecimal getCuotaInicialPct() {
        return cuotaInicialPct;
    }

    public void setCuotaInicialPct(BigDecimal cuotaInicialPct) {
        this.cuotaInicialPct = cuotaInicialPct;
    }

    public BigDecimal getCuotaBalonPct() {
        return cuotaBalonPct;
    }

    public void setCuotaBalonPct(BigDecimal cuotaBalonPct) {
        this.cuotaBalonPct = cuotaBalonPct;
    }

    public BigDecimal getValorTasa() {
        return valorTasa;
    }

    public void setValorTasa(BigDecimal valorTasa) {
        this.valorTasa = valorTasa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public IndicadorResponse getIndicador() {
        return indicador;
    }

    public void setIndicador(IndicadorResponse indicador) {
        this.indicador = indicador;
    }

    public List<CronogramaResponse> getCronograma() {
        return cronograma;
    }

    public void setCronograma(List<CronogramaResponse> cronograma) {
        this.cronograma = cronograma;
    }
}
