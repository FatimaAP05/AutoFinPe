package pe.autofinpe.dto.simulador;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class SimuladorRequest {

    @NotNull(message = "El precio del vehiculo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio del vehiculo debe ser mayor a cero")
    private BigDecimal precioVehiculo;

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "^(PEN|USD)$", message = "La moneda debe ser PEN o USD")
    private String moneda;

    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 12, message = "El plazo debe ser mayor o igual a 12 meses")
    @Max(value = 84, message = "El plazo no debe superar 84 meses")
    private Integer plazoMeses;

    @NotNull(message = "La cuota inicial es obligatoria")
    @DecimalMin(value = "0.00", message = "La cuota inicial no puede ser negativa")
    @DecimalMax(value = "90.00", message = "La cuota inicial no puede superar 90%")
    private BigDecimal cuotaInicialPct;

    @NotNull(message = "La cuota balon es obligatoria")
    @DecimalMin(value = "0.00", message = "La cuota balon no puede ser negativa")
    @DecimalMax(value = "50.00", message = "La cuota balon no puede superar 50%")
    private BigDecimal cuotaBalonPct;

    @NotNull(message = "El valor de la tasa es obligatorio")
    @DecimalMin(value = "0.00", message = "La tasa no puede ser negativa")
    @DecimalMax(value = "100.00", message = "La tasa no puede superar 100%")
    private BigDecimal valorTasaPct;

    @NotBlank(message = "El tipo de tasa es obligatorio")
    @Pattern(regexp = "^(N|E)$", message = "El tipo de tasa debe ser N o E")
    private String tipoTasa;

    @NotNull(message = "La capitalizacion es obligatoria")
    private Short capitalizacion;

    @NotBlank(message = "El tipo de gracia es obligatorio")
    @Pattern(regexp = "^(S|T|P)$", message = "El tipo de gracia debe ser S, T o P")
    private String tipoGracia;

    @NotNull(message = "Los meses de gracia son obligatorios")
    @Min(value = 0, message = "Los meses de gracia no pueden ser menores a 0")
    @Max(value = 6, message = "Los meses de gracia no pueden ser mayores a 6")
    private Integer mesesGracia;

    @NotNull(message = "El seguro de desgravamen mensual es obligatorio")
    @DecimalMin(value = "0.00", message = "El seguro de desgravamen no puede ser negativo")
    @DecimalMax(value = "10.00", message = "El seguro de desgravamen no puede superar 10% mensual")
    private BigDecimal seguroDesgravamenPctMensual;

    @NotNull(message = "El seguro vehicular anual es obligatorio")
    @DecimalMin(value = "0.00", message = "El seguro vehicular no puede ser negativo")
    @DecimalMax(value = "20.00", message = "El seguro vehicular no puede superar 20% anual")
    private BigDecimal seguroVehicularPctAnual;

    @NotNull(message = "Los portes mensuales son obligatorios")
    @DecimalMin(value = "0.00", message = "Los portes no pueden ser negativos")
    private BigDecimal portesMensuales;

    @NotNull(message = "El COK anual es obligatorio")
    @DecimalMin(value = "0.00", message = "El COK anual no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El COK anual no puede superar 100%")
    private BigDecimal cokAnualPct;

    public SimuladorRequest() {
    }

    public BigDecimal getPrecioVehiculo() {
        return precioVehiculo;
    }

    public void setPrecioVehiculo(BigDecimal precioVehiculo) {
        this.precioVehiculo = precioVehiculo;
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

    public BigDecimal getValorTasaPct() {
        return valorTasaPct;
    }

    public void setValorTasaPct(BigDecimal valorTasaPct) {
        this.valorTasaPct = valorTasaPct;
    }

    public String getTipoTasa() {
        return tipoTasa;
    }

    public void setTipoTasa(String tipoTasa) {
        this.tipoTasa = tipoTasa;
    }

    public Short getCapitalizacion() {
        return capitalizacion;
    }

    public void setCapitalizacion(Short capitalizacion) {
        this.capitalizacion = capitalizacion;
    }

    public String getTipoGracia() {
        return tipoGracia;
    }

    public void setTipoGracia(String tipoGracia) {
        this.tipoGracia = tipoGracia;
    }

    public Integer getMesesGracia() {
        return mesesGracia;
    }

    public void setMesesGracia(Integer mesesGracia) {
        this.mesesGracia = mesesGracia;
    }

    public BigDecimal getSeguroDesgravamenPctMensual() {
        return seguroDesgravamenPctMensual;
    }

    public void setSeguroDesgravamenPctMensual(BigDecimal seguroDesgravamenPctMensual) {
        this.seguroDesgravamenPctMensual = seguroDesgravamenPctMensual;
    }

    public BigDecimal getSeguroVehicularPctAnual() {
        return seguroVehicularPctAnual;
    }

    public void setSeguroVehicularPctAnual(BigDecimal seguroVehicularPctAnual) {
        this.seguroVehicularPctAnual = seguroVehicularPctAnual;
    }

    public BigDecimal getPortesMensuales() {
        return portesMensuales;
    }

    public void setPortesMensuales(BigDecimal portesMensuales) {
        this.portesMensuales = portesMensuales;
    }

    public BigDecimal getCokAnualPct() {
        return cokAnualPct;
    }

    public void setCokAnualPct(BigDecimal cokAnualPct) {
        this.cokAnualPct = cokAnualPct;
    }
}
