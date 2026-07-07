package pe.autofinpe.dto.vehiculo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class VehiculoRequest {

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 30, message = "La marca no debe superar 30 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .'\\-]+$",
            message = "La marca solo puede contener letras, numeros, espacios, guiones y apostrofes"
    )
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 30, message = "El modelo no debe superar 30 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .'\\-]+$",
            message = "El modelo solo puede contener letras, numeros, espacios, guiones y apostrofes"
    )
    private String modelo;

    @NotNull(message = "El anio es obligatorio")
    @Min(value = 2000, message = "El anio debe ser mayor o igual a 2000")
    @Max(value = 2155, message = "El anio debe ser menor o igual a 2155")
    private Integer anio;

    @NotNull(message = "El precio en soles es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio en soles debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "El precio en soles debe tener hasta 10 enteros y 2 decimales")
    private BigDecimal precioPen;

    @NotNull(message = "El precio en dolares es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio en dolares debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "El precio en dolares debe tener hasta 10 enteros y 2 decimales")
    private BigDecimal precioUsd;

    @NotBlank(message = "La categoria es obligatoria")
    @Size(max = 30, message = "La categoria no debe superar 30 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .'\\-]+$",
            message = "La categoria solo puede contener letras, numeros, espacios, guiones y apostrofes"
    )
    private String categoria;

    public VehiculoRequest() {
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getPrecioPen() {
        return precioPen;
    }

    public void setPrecioPen(BigDecimal precioPen) {
        this.precioPen = precioPen;
    }

    public BigDecimal getPrecioUsd() {
        return precioUsd;
    }

    public void setPrecioUsd(BigDecimal precioUsd) {
        this.precioUsd = precioUsd;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
