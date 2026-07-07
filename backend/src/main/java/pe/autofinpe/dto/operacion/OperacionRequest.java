package pe.autofinpe.dto.operacion;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class OperacionRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer idCliente;

    @NotNull(message = "El ID del vehiculo es obligatorio")
    private Integer idVehiculo;

    @NotNull(message = "El ID de la configuracion es obligatorio")
    private Integer idConfiguracion;

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

    @NotBlank(message = "El tipo de gracia es obligatorio")
    @Pattern(regexp = "^(S|T|P)$", message = "El tipo de gracia debe ser S, T o P")
    private String tipoGracia;

    @NotNull(message = "Los meses de gracia son obligatorios")
    @Min(value = 0, message = "Los meses de gracia no pueden ser menores a 0")
    @Max(value = 6, message = "Los meses de gracia no pueden ser mayores a 6")
    private Integer mesesGracia;

    @NotNull(message = "El seguro de desgravamen es obligatorio")
    @DecimalMin(value = "0.00", message = "El seguro de desgravamen no puede ser negativo")
    private BigDecimal seguroDesgravamenPct;

    @NotNull(message = "El seguro vehicular es obligatorio")
    @DecimalMin(value = "0.00", message = "El seguro vehicular no puede ser negativo")
    private BigDecimal seguroVehicularPct;

    @NotNull(message = "Los portes mensuales son obligatorios")
    @DecimalMin(value = "0.00", message = "Los portes no pueden ser negativos")
    private BigDecimal portesMensuales;

    @NotNull(message = "El COK anual es obligatorio")
    @DecimalMin(value = "0.00", message = "El COK anual no puede ser negativo")
    private BigDecimal cokAnualPct;

    public Integer getIdCliente() {
        return idCliente;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public Integer getIdConfiguracion() {
        return idConfiguracion;
    }

    public String getMoneda() {
        return moneda;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public BigDecimal getCuotaInicialPct() {
        return cuotaInicialPct;
    }

    public BigDecimal getCuotaBalonPct() {
        return cuotaBalonPct;
    }

    public BigDecimal getValorTasaPct() {
        return valorTasaPct;
    }

    public String getTipoTasa() {
        return tipoTasa;
    }

    public String getTipoGracia() {
        return tipoGracia;
    }

    public Integer getMesesGracia() {
        return mesesGracia;
    }

    public BigDecimal getSeguroDesgravamenPct() {
        return seguroDesgravamenPct;
    }

    public BigDecimal getSeguroVehicularPct() {
        return seguroVehicularPct;
    }

    public BigDecimal getPortesMensuales() {
        return portesMensuales;
    }

    public BigDecimal getCokAnualPct() {
        return cokAnualPct;
    }
}
