package pe.autofinpe.dto.auth;

public class AuthenticatedUserResponse {

    private Integer idUsuario;
    private String login;
    private String nombres;
    private String rol;

    public AuthenticatedUserResponse() {
    }

    public AuthenticatedUserResponse(Integer idUsuario, String login, String nombres, String rol) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.nombres = nombres;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
