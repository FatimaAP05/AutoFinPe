package pe.autofinpe.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operacion")
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operacion")
    private Integer idOperacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_config", nullable = false)
    private Configuracion configuracion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "plazo", nullable = false)
    private Integer plazo;

    @Column(name = "cuota_inicial_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal cuotaInicialPct;

    @Column(name = "cuota_balon_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal cuotaBalonPct;

    @Column(name = "valor_tasa", nullable = false, precision = 6, scale = 4)
    private BigDecimal valorTasa;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "REGISTRADA";

    @OneToMany(mappedBy = "operacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cronograma> cronogramas = new ArrayList<>();

    @OneToOne(mappedBy = "operacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Indicador indicador;

    public Operacion() {
    }

    public Integer getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(Integer idOperacion) {
        this.idOperacion = idOperacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
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

    public BigDecimal getValorTasa() {
        return valorTasa;
    }

    public void setValorTasa(BigDecimal valorTasa) {
        this.valorTasa = valorTasa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Cronograma> getCronogramas() {
        return cronogramas;
    }

    public void setCronogramas(List<Cronograma> cronogramas) {
        this.cronogramas = cronogramas;
    }

    public Indicador getIndicador() {
        return indicador;
    }

    public void setIndicador(Indicador indicador) {
        this.indicador = indicador;
    }
}
