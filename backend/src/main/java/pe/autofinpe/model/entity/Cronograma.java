package pe.autofinpe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cronograma")
public class Cronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cronograma")
    private Integer idCronograma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operacion", nullable = false)
    private Operacion operacion;

    @Column(name = "nro_cuota", nullable = false)
    private Integer nroCuota;

    @Column(name = "saldo_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "interes", nullable = false, precision = 12, scale = 2)
    private BigDecimal interes;

    @Column(name = "amortizacion", nullable = false, precision = 12, scale = 2)
    private BigDecimal amortizacion;

    @Column(name = "seguro_desgrav", nullable = false, precision = 12, scale = 2)
    private BigDecimal seguroDesgrav;

    @Column(name = "seguro_vehic", nullable = false, precision = 12, scale = 2)
    private BigDecimal seguroVehic;

    @Column(name = "portes", nullable = false, precision = 12, scale = 2)
    private BigDecimal portes;

    @Column(name = "cuota_credito", nullable = false, precision = 12, scale = 2)
    private BigDecimal cuotaCredito;

    @Column(name = "cuota_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal cuotaTotal;

    @Column(name = "saldo_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoFinal;

    public Cronograma() {
    }

    public Integer getIdCronograma() {
        return idCronograma;
    }

    public void setIdCronograma(Integer idCronograma) {
        this.idCronograma = idCronograma;
    }

    public Operacion getOperacion() {
        return operacion;
    }

    public void setOperacion(Operacion operacion) {
        this.operacion = operacion;
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
