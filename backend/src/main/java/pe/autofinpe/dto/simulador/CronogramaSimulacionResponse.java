package pe.autofinpe.dto.simulador;

import java.math.BigDecimal;

public class CronogramaSimulacionResponse {

    private Integer nroCuota;
    private BigDecimal saldoInicial;
    private BigDecimal interes;
    private BigDecimal amortizacion;
    private BigDecimal seguroDesgravamen;
    private BigDecimal seguroVehicular;
    private BigDecimal portes;
    private BigDecimal cuotaCredito;
    private BigDecimal cuotaTotal;
    private BigDecimal saldoFinal;

    public CronogramaSimulacionResponse() {
    }

    public CronogramaSimulacionResponse(
            Integer nroCuota,
            BigDecimal saldoInicial,
            BigDecimal interes,
            BigDecimal amortizacion,
            BigDecimal seguroDesgravamen,
            BigDecimal seguroVehicular,
            BigDecimal portes,
            BigDecimal cuotaCredito,
            BigDecimal cuotaTotal,
            BigDecimal saldoFinal
    ) {
        this.nroCuota = nroCuota;
        this.saldoInicial = saldoInicial;
        this.interes = interes;
        this.amortizacion = amortizacion;
        this.seguroDesgravamen = seguroDesgravamen;
        this.seguroVehicular = seguroVehicular;
        this.portes = portes;
        this.cuotaCredito = cuotaCredito;
        this.cuotaTotal = cuotaTotal;
        this.saldoFinal = saldoFinal;
    }

    public Integer getNroCuota() {
        return nroCuota;
    }

    public void setNroCuota(Integer nroCuota) {
        this.nroCuota = nroCuota;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(BigDecimal amortizacion) {
        this.amortizacion = amortizacion;
    }

    public BigDecimal getSeguroDesgravamen() {
        return seguroDesgravamen;
    }

    public void setSeguroDesgravamen(BigDecimal seguroDesgravamen) {
        this.seguroDesgravamen = seguroDesgravamen;
    }

    public BigDecimal getSeguroVehicular() {
        return seguroVehicular;
    }

    public void setSeguroVehicular(BigDecimal seguroVehicular) {
        this.seguroVehicular = seguroVehicular;
    }

    public BigDecimal getPortes() {
        return portes;
    }

    public void setPortes(BigDecimal portes) {
        this.portes = portes;
    }

    public BigDecimal getCuotaCredito() {
        return cuotaCredito;
    }

    public void setCuotaCredito(BigDecimal cuotaCredito) {
        this.cuotaCredito = cuotaCredito;
    }

    public BigDecimal getCuotaTotal() {
        return cuotaTotal;
    }

    public void setCuotaTotal(BigDecimal cuotaTotal) {
        this.cuotaTotal = cuotaTotal;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}
