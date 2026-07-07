package pe.autofinpe.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pe.autofinpe.dto.auth.AuthenticatedUserResponse;
import pe.autofinpe.dto.auth.LoginRequest;
import pe.autofinpe.dto.auth.LoginResponse;
import pe.autofinpe.security.AuthenticatedUser;
import pe.autofinpe.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getClave())
            );

            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

            if (!user.isEnabled()) {
                throw new DisabledException("Usuario inactivo");
            }

            String token = jwtService.generateToken(user);
            AuthenticatedUserResponse usuario = new AuthenticatedUserResponse(
                    user.getIdUsuario(),
                    user.getLogin(),
                    user.getNombres(),
                    user.getRol()
            );

            return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds(), usuario);
        } catch (DisabledException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
    }
}
