package pe.autofinpe.dto.configuracion;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ConfiguracionRequest {

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "^(PEN|USD)$", message = "La moneda debe ser PEN o USD")
    private String moneda;

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
    private Byte mesesGracia;

    public ConfiguracionRequest() {
    }

    @AssertTrue(message = "La capitalizacion debe ser 1, 2, 4, 12 o 365")
    public boolean isCapitalizacionValida() {
        if (capitalizacion == null) {
            return true;
        }
        return capitalizacion == 1 || capitalizacion == 2 || capitalizacion == 4
                || capitalizacion == 12 || capitalizacion == 365;
    }

    @AssertTrue(message = "Si el tipo de gracia es S, los meses deben ser 0; si es T o P, deben estar entre 1 y 6")
    public boolean isReglaGraciaValida() {
        if (tipoGracia == null || mesesGracia == null) {
            return true;
        }
        if ("S".equals(tipoGracia)) {
            return mesesGracia == 0;
        }
        return ("T".equals(tipoGracia) || "P".equals(tipoGracia)) && mesesGracia >= 1 && mesesGracia <= 6;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
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

    public Byte getMesesGracia() {
        return mesesGracia;
    }

    public void setMesesGracia(Byte mesesGracia) {
        this.mesesGracia = mesesGracia;
    }
}
