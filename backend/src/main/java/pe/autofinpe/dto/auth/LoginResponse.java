package pe.autofinpe.dto.auth;

public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private AuthenticatedUserResponse usuario;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String tokenType,
            long expiresInSeconds,
            AuthenticatedUserResponse usuario
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public AuthenticatedUserResponse getUsuario() {
        return usuario;
    }

    public void setUsuario(AuthenticatedUserResponse usuario) {
        this.usuario = usuario;
    }
}
