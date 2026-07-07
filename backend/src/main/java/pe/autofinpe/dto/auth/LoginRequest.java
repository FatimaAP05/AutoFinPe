package pe.autofinpe.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El login es obligatorio")
    @Size(max = 20, message = "El login no debe superar 20 caracteres")
    private String login;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 8, max = 100, message = "La clave debe tener entre 8 y 100 caracteres")
    private String clave;

    public LoginRequest() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
