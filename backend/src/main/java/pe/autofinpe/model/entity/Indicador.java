package pe.autofinpe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "indicador")
public class Indicador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicador")
    private Integer idIndicador;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operacion", nullable = false, unique = true)
    private Operacion operacion;

    @Column(name = "tcea", nullable = false, precision = 6, scale = 4)
    private BigDecimal tcea;

    @Column(name = "van", nullable = false, precision = 12, scale = 2)
    private BigDecimal van;

    @Column(name = "tir", nullable = false, precision = 6, scale = 4)
    private BigDecimal tir;

    @Column(name = "total_intereses", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalIntereses;

    @Column(name = "total_amortizacion", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmortizacion;

    @Column(name = "total_seguros", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSeguros;

    @Column(name = "total_portes", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPortes;

    @Column(name = "total_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPagado;

    public Indicador() {
    }

    public Integer getIdIndicador() {
        return idIndicador;
    }

    public void setIdIndicador(Integer idIndicador) {
        this.idIndicador = idIndicador;
    }

    public Operacion getOperacion() {
        return operacion;
    }

    public void setOperacion(Operacion operacion) {
        this.operacion = operacion;
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

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
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
}
