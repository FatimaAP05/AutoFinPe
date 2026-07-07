package pe.autofinpe.dto.operacion;

import java.math.BigDecimal;

public class CronogramaResponse {

    private Integer nroCuota;
    private BigDecimal saldoInicial;
    private BigDecimal interes;
    private BigDecimal amortizacion;
    private BigDecimal seguroDesgrav;
    private BigDecimal seguroVehic;
    private BigDecimal portes;
    private BigDecimal cuotaCredito;
    private BigDecimal cuotaTotal;
    private BigDecimal saldoFinal;

    public CronogramaResponse() {
    }

    public CronogramaResponse(
            Integer nroCuota,
            BigDecimal saldoInicial,
            BigDecimal interes,
            BigDecimal amortizacion,
            BigDecimal seguroDesgrav,
            BigDecimal seguroVehic,
            BigDecimal portes,
            BigDecimal cuotaCredito,
            BigDecimal cuotaTotal,
            BigDecimal saldoFinal
    ) {
        this.nroCuota = nroCuota;
        this.saldoInicial = saldoInicial;
        this.interes = interes;
        this.amortizacion = amortizacion;
        this.seguroDesgrav = seguroDesgrav;
        this.seguroVehic = seguroVehic;
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

    public BigDecimal getSeguroDesgrav() {
        return seguroDesgrav;
    }

    public void setSeguroDesgrav(BigDecimal seguroDesgrav) {
        this.seguroDesgrav = seguroDesgrav;
    }

    public BigDecimal getSeguroVehic() {
        return seguroVehic;
    }

    public void setSeguroVehic(BigDecimal seguroVehic) {
        this.seguroVehic = seguroVehic;
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
