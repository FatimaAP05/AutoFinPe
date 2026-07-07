package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.autofinpe.dto.common.StandardApiResponse;
import pe.autofinpe.dto.configuracion.ConfiguracionRequest;
import pe.autofinpe.dto.configuracion.ConfiguracionResponse;
import pe.autofinpe.exception.ApiStatusCode;
import pe.autofinpe.service.ConfiguracionService;

@Tag(name = "Configuraciones", description = "CRUD de parametros financieros para operaciones de credito vehicular")
@SecurityRequirement(name = "bearer-jwt")
@Validated
@RestController
@RequestMapping("/configuraciones")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @Operation(summary = "Crear configuracion", description = "Registra una configuracion financiera unica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Configuracion creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "409", description = "Configuracion duplicada")
    })
    @PostMapping
    public ResponseEntity<StandardApiResponse<ConfiguracionResponse>> crear(
            @Valid @RequestBody ConfiguracionRequest request
    ) {
        ConfiguracionResponse response = configuracionService.crear(request);
        return ResponseEntity
                .status(ApiStatusCode.CREATED.getHttpStatus())
                .location(URI.create("/api/configuraciones/" + response.getIdConfig()))
                .body(StandardApiResponse.success("Configuracion creada correctamente", response));
    }

    @Operation(summary = "Listar configuraciones", description = "Obtiene todas las configuraciones financieras")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<ConfiguracionResponse>>> listar() {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Configuraciones obtenidas correctamente",
                        configuracionService.listar()
                ));
    }

    @Operation(summary = "Buscar configuracion por ID", description = "Obtiene una configuracion usando su identificador interno")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuracion encontrada"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Configuracion no encontrada")
    })
    @GetMapping("/{idConfig}")
    public ResponseEntity<StandardApiResponse<ConfiguracionResponse>> buscarPorId(
            @Parameter(description = "Identificador interno de la configuracion", example = "1")
            @PathVariable Integer idConfig
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Configuracion encontrada",
                        configuracionService.buscarPorId(idConfig)
                ));
    }

    @Operation(summary = "Actualizar configuracion", description = "Actualiza una configuracion financiera existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuracion actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Configuracion no encontrada"),
            @ApiResponse(responseCode = "409", description = "Configuracion duplicada")
    })
    @PutMapping("/{idConfig}")
    public ResponseEntity<StandardApiResponse<ConfiguracionResponse>> actualizar(
            @Parameter(description = "Identificador interno de la configuracion", example = "1")
            @PathVariable Integer idConfig,
            @Valid @RequestBody ConfiguracionRequest request
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Configuracion actualizada correctamente",
                        configuracionService.actualizar(idConfig, request)
                ));
    }

    @Operation(summary = "Eliminar configuracion", description = "Elimina una configuracion si no tiene operaciones asociadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuracion eliminada"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Configuracion no encontrada"),
            @ApiResponse(responseCode = "409", description = "Configuracion con operaciones asociadas")
    })
    @DeleteMapping("/{idConfig}")
    public ResponseEntity<StandardApiResponse<Void>> eliminar(
            @Parameter(description = "Identificador interno de la configuracion", example = "1")
            @PathVariable Integer idConfig
    ) {
        configuracionService.eliminar(idConfig);
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Configuracion eliminada correctamente"));
    }
}
