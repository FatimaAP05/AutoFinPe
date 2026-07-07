package pe.autofinpe.dto.simulador;

import java.math.BigDecimal;

public class IndicadoresSimulacionResponse {

    private BigDecimal van;
    private BigDecimal tirMensual;
    private BigDecimal tirAnual;
    private BigDecimal tcea;
    private BigDecimal totalIntereses;
    private BigDecimal totalAmortizacion;
    private BigDecimal totalSeguros;
    private BigDecimal totalPortes;
    private BigDecimal totalPagado;

    public IndicadoresSimulacionResponse() {
    }

    public IndicadoresSimulacionResponse(
            BigDecimal van,
            BigDecimal tirMensual,
            BigDecimal tirAnual,
            BigDecimal tcea,
            BigDecimal totalIntereses,
            BigDecimal totalAmortizacion,
            BigDecimal totalSeguros,
            BigDecimal totalPortes,
            BigDecimal totalPagado
    ) {
        this.van = van;
        this.tirMensual = tirMensual;
        this.tirAnual = tirAnual;
        this.tcea = tcea;
        this.totalIntereses = totalIntereses;
        this.totalAmortizacion = totalAmortizacion;
        this.totalSeguros = totalSeguros;
        this.totalPortes = totalPortes;
        this.totalPagado = totalPagado;
    }

    public BigDecimal getVan() {
        return van;
    }

    public void setVan(BigDecimal van) {
        this.van = van;
    }

    public BigDecimal getTirMensual() {
        return tirMensual;
    }

    public void setTirMensual(BigDecimal tirMensual) {
        this.tirMensual = tirMensual;
    }

    public BigDecimal getTirAnual() {
        return tirAnual;
    }

    public void setTirAnual(BigDecimal tirAnual) {
        this.tirAnual = tirAnual;
    }

    public BigDecimal getTcea() {
        return tcea;
    }

    public void setTcea(BigDecimal tcea) {
        this.tcea = tcea;
    }

    public BigDecimal getTotalIntereses() {
        return totalIntereses;
    }

    public void setTotalIntereses(BigDecimal totalIntereses) {
        this.totalIntereses = totalIntereses;
    }

    public BigDecimal getTotalAmortizacion() {
        return totalAmortizacion;
    }

    public void setTotalAmortizacion(BigDecimal totalAmortizacion) {
        this.totalAmortizacion = totalAmortizacion;
    }

    public BigDecimal getTotalSeguros() {
        return totalSeguros;
    }

    public void setTotalSeguros(BigDecimal totalSeguros) {
        this.totalSeguros = totalSeguros;
    }

    public BigDecimal getTotalPortes() {
        return totalPortes;
    }

    public void setTotalPortes(BigDecimal totalPortes) {
        this.totalPortes = totalPortes;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }
}
