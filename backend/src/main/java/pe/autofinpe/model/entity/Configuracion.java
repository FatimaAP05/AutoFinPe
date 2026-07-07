package pe.autofinpe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;

    @Column(name = "moneda", nullable = false, columnDefinition = "CHAR(3)")
    private String moneda = "PEN";

    @Column(name = "tipo_tasa", nullable = false, columnDefinition = "CHAR(1)")
    private String tipoTasa = "E";

    @Column(name = "capitalizacion", nullable = false)
    private Short capitalizacion = (short) 12;

    @Column(name = "tipo_gracia", nullable = false, columnDefinition = "CHAR(1)")
    private String tipoGracia = "S";

    @Column(name = "meses_gracia", nullable = false)
    private Byte mesesGracia = (byte) 0;

    @OneToMany(mappedBy = "configuracion", fetch = FetchType.LAZY)
    private List<Operacion> operaciones = new ArrayList<>();

    public Configuracion() {
    }

    public Integer getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig = idConfig;
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

    public List<Operacion> getOperaciones() {
        return operaciones;
    }

    public void setOperaciones(List<Operacion> operaciones) {
        this.operaciones = operaciones;
    }
}
