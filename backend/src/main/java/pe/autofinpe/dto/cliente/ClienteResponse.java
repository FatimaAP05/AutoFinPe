package pe.autofinpe.dto.cliente;

import java.math.BigDecimal;

public class ClienteResponse {

    private Integer idCliente;
    private String dni;
    private String nombres;
    private String apellidos;
    private BigDecimal ingresoMensual;
    private String calificacion;
    private String telefono;
    private String email;

    public ClienteResponse() {
    }

    public ClienteResponse(
            Integer idCliente,
            String dni,
            String nombres,
            String apellidos,
            BigDecimal ingresoMensual,
            String calificacion,
            String telefono,
            String email
    ) {
        this.idCliente = idCliente;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.ingresoMensual = ingresoMensual;
        this.calificacion = calificacion;
        this.telefono = telefono;
        this.email = email;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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
