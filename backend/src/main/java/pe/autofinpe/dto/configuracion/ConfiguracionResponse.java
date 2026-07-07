package pe.autofinpe.dto.configuracion;

public class ConfiguracionResponse {

    private Integer idConfig;
    private String moneda;
    private String tipoTasa;
    private Short capitalizacion;
    private String tipoGracia;
    private Byte mesesGracia;

    public ConfiguracionResponse() {
    }

    public ConfiguracionResponse(
            Integer idConfig,
            String moneda,
            String tipoTasa,
            Short capitalizacion,
            String tipoGracia,
            Byte mesesGracia
    ) {
        this.idConfig = idConfig;
        this.moneda = moneda;
        this.tipoTasa = tipoTasa;
        this.capitalizacion = capitalizacion;
        this.tipoGracia = tipoGracia;
        this.mesesGracia = mesesGracia;
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
}
