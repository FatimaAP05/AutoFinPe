package pe.autofinpe.dto.simulador;

import java.math.BigDecimal;
import java.util.List;

public class SimuladorResponse {

    private String moneda;
    private Integer plazoMeses;
    private BigDecimal precioVehiculo;
    private BigDecimal cuotaInicial;
    private BigDecimal saldoFinanciado;
    private BigDecimal tea;
    private BigDecimal tepMensual;
    private BigDecimal cuotaBalon;
    private BigDecimal cuotaUniforme;
    private IndicadoresSimulacionResponse indicadores;
    private List<CronogramaSimulacionResponse> cronograma;

    public SimuladorResponse() {
    }

    public SimuladorResponse(
            String moneda,
            Integer plazoMeses,
            BigDecimal precioVehiculo,
            BigDecimal cuotaInicial,
            BigDecimal saldoFinanciado,
            BigDecimal tea,
            BigDecimal tepMensual,
            BigDecimal cuotaBalon,
            BigDecimal cuotaUniforme,
            IndicadoresSimulacionResponse indicadores,
            List<CronogramaSimulacionResponse> cronograma
    ) {
        this.moneda = moneda;
        this.plazoMeses = plazoMeses;
        this.precioVehiculo = precioVehiculo;
        this.cuotaInicial = cuotaInicial;
        this.saldoFinanciado = saldoFinanciado;
        this.tea = tea;
        this.tepMensual = tepMensual;
        this.cuotaBalon = cuotaBalon;
        this.cuotaUniforme = cuotaUniforme;
        this.indicadores = indicadores;
        this.cronograma = cronograma;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public BigDecimal getPrecioVehiculo() {
        return precioVehiculo;
    }

    public void setPrecioVehiculo(BigDecimal precioVehiculo) {
        this.precioVehiculo = precioVehiculo;
    }

    public BigDecimal getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(BigDecimal cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public BigDecimal getSaldoFinanciado() {
        return saldoFinanciado;
    }

    public void setSaldoFinanciado(BigDecimal saldoFinanciado) {
        this.saldoFinanciado = saldoFinanciado;
    }

    public BigDecimal getTea() {
        return tea;
    }

    public void setTea(BigDecimal tea) {
        this.tea = tea;
    }

    public BigDecimal getTepMensual() {
        return tepMensual;
    }

    public void setTepMensual(BigDecimal tepMensual) {
        this.tepMensual = tepMensual;
    }

    public BigDecimal getCuotaBalon() {
        return cuotaBalon;
    }

    public void setCuotaBalon(BigDecimal cuotaBalon) {
        this.cuotaBalon = cuotaBalon;
    }

    public BigDecimal getCuotaUniforme() {
        return cuotaUniforme;
    }

    public void setCuotaUniforme(BigDecimal cuotaUniforme) {
        this.cuotaUniforme = cuotaUniforme;
    }

    public IndicadoresSimulacionResponse getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(IndicadoresSimulacionResponse indicadores) {
        this.indicadores = indicadores;
    }

    public List<CronogramaSimulacionResponse> getCronograma() {
        return cronograma;
    }

    public void setCronograma(List<CronogramaSimulacionResponse> cronograma) {
        this.cronograma = cronograma;
    }
}
