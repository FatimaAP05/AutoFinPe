package pe.autofinpe.dto.vehiculo;

import java.math.BigDecimal;

public class VehiculoResponse {

    private Integer idVehiculo;
    private String marca;
    private String modelo;
    private Integer anio;
    private BigDecimal precioPen;
    private BigDecimal precioUsd;
    private String categoria;
    private String imagenUrl;

    public VehiculoResponse() {
    }

    public VehiculoResponse(
            Integer idVehiculo,
            String marca,
            String modelo,
            Integer anio,
            BigDecimal precioPen,
            BigDecimal precioUsd,
            String categoria,
            String imagenUrl
    ) {
        this.idVehiculo = idVehiculo;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioPen = precioPen;
        this.precioUsd = precioUsd;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
