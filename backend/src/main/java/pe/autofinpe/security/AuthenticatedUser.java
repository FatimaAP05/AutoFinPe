package pe.autofinpe.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pe.autofinpe.model.entity.Usuario;

public class AuthenticatedUser implements UserDetails {

    private final Integer idUsuario;
    private final String login;
    private final String claveHash;
    private final String nombres;
    private final String rol;
    private final String estado;

    public AuthenticatedUser(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.login = usuario.getLogin();
        this.claveHash = usuario.getClaveHash();
        this.nombres = usuario.getNombres();
        this.rol = usuario.getRol();
        this.estado = usuario.getEstado();
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public String getNombres() {
        return nombres;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return claveHash;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "A".equals(estado);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "A".equals(estado);
    }
}
