package pe.autofinpe.dto.operacion;

import java.math.BigDecimal;

public class IndicadorResponse {

    private Integer idIndicador;
    private BigDecimal tcea;
    private BigDecimal van;
    private BigDecimal tir;
    private BigDecimal totalIntereses;
    private BigDecimal totalAmortizacion;
    private BigDecimal totalSeguros;
    private BigDecimal totalPortes;
    private BigDecimal totalPagado;

    public IndicadorResponse() {
    }

    public IndicadorResponse(
            Integer idIndicador,
            BigDecimal tcea,
            BigDecimal van,
            BigDecimal tir,
            BigDecimal totalIntereses,
            BigDecimal totalAmortizacion,
            BigDecimal totalSeguros,
            BigDecimal totalPortes,
            BigDecimal totalPagado
    ) {
        this.idIndicador = idIndicador;
        this.tcea = tcea;
        this.van = van;
        this.tir = tir;
        this.totalIntereses = totalIntereses;
        this.totalAmortizacion = totalAmortizacion;
        this.totalSeguros = totalSeguros;
        this.totalPortes = totalPortes;
        this.totalPagado = totalPagado;
    }

    public Integer getIdIndicador() {
        return idIndicador;
    }

    public void setIdIndicador(Integer idIndicador) {
        this.idIndicador = idIndicador;
    }

    public BigDecimal getTcea() {
        return tcea;
    }

    public void setTcea(BigDecimal tcea) {
        this.tcea = tcea;
    }

    public BigDecimal getVan() {
        return van;
    }

    public void setVan(BigDecimal van) {
        this.van = van;
    }

    public BigDecimal getTir() {
        return tir;
    }

    public void setTir(BigDecimal tir) {
        this.tir = tir;
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
