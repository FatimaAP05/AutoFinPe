package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.autofinpe.dto.common.StandardApiResponse;
import pe.autofinpe.dto.auth.LoginRequest;
import pe.autofinpe.dto.auth.LoginResponse;
import pe.autofinpe.exception.ApiStatusCode;
import pe.autofinpe.service.AuthService;

@Tag(name = "Autenticacion", description = "Operacion publica para obtener el token JWT")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesion", description = "Autentica un usuario activo y devuelve un token Bearer JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas"),
            @ApiResponse(responseCode = "403", description = "Usuario inactivo")
    })
    @PostMapping("/login")
    public ResponseEntity<StandardApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Login exitoso", authService.login(request)));
    }
}
