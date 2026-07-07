package pe.autofinpe.dto.cliente;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ClienteRequest {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 digitos")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 60, message = "Los nombres no deben superar 60 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 60, message = "Los apellidos no deben superar 60 caracteres")
    private String apellidos;

    @NotNull(message = "El ingreso mensual es obligatorio")
    @DecimalMin(value = "0.00", message = "El ingreso mensual no puede ser negativo")
    private BigDecimal ingresoMensual;

    @NotBlank(message = "La calificacion es obligatoria")
    @Pattern(
            regexp = "^(A|B|C|D|E|SIN_CALIFICAR)$",
            message = "La calificacion debe ser A, B, C, D, E o SIN_CALIFICAR"
    )
    private String calificacion;

    @Size(max = 20, message = "El telefono no debe superar 20 caracteres")
    private String telefono;

    @Email(message = "El email no tiene un formato valido")
    @Size(max = 100, message = "El email no debe superar 100 caracteres")
    private String email;

    public ClienteRequest() {
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public BigDecimal getIngresoMensual() {
        return ingresoMensual;
    }

    public void setIngresoMensual(BigDecimal ingresoMensual) {
        this.ingresoMensual = ingresoMensual;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
