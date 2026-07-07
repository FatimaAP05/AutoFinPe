package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
import pe.autofinpe.dto.cliente.ClienteRequest;
import pe.autofinpe.dto.cliente.ClienteResponse;
import pe.autofinpe.exception.ApiStatusCode;
import pe.autofinpe.service.ClienteService;

@Tag(name = "Clientes", description = "CRUD de clientes para operaciones de credito vehicular")
@SecurityRequirement(name = "bearer-jwt")
@Validated
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente validando DNI unico y formato de email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "409", description = "DNI duplicado")
    })
    @PostMapping
    public ResponseEntity<StandardApiResponse<ClienteResponse>> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.crear(request);
        return ResponseEntity
                .status(ApiStatusCode.CREATED.getHttpStatus())
                .location(URI.create("/api/clientes/" + response.getIdCliente()))
                .body(StandardApiResponse.success("Cliente creado correctamente", response));
    }

    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes registrados ordenados por apellidos y nombres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<ClienteResponse>>> listar() {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Clientes obtenidos correctamente", clienteService.listar()));
    }

    @Operation(summary = "Buscar cliente por ID", description = "Obtiene un cliente usando su identificador interno")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{idCliente}")
    public ResponseEntity<StandardApiResponse<ClienteResponse>> buscarPorId(
            @Parameter(description = "Identificador interno del cliente", example = "1")
            @PathVariable Integer idCliente
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Cliente encontrado", clienteService.buscarPorId(idCliente)));
    }

    @Operation(summary = "Buscar cliente por DNI", description = "Obtiene un cliente usando su DNI de 8 digitos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "400", description = "DNI invalido"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/dni/{dni}")
    public ResponseEntity<StandardApiResponse<ClienteResponse>> buscarPorDni(
            @Parameter(description = "DNI peruano de 8 digitos", example = "12345678")
            @PathVariable
            @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 digitos")
            String dni
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Cliente encontrado", clienteService.buscarPorDni(dni)));
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente validando DNI unico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "DNI duplicado")
    })
    @PutMapping("/{idCliente}")
    public ResponseEntity<StandardApiResponse<ClienteResponse>> actualizar(
            @Parameter(description = "Identificador interno del cliente", example = "1")
            @PathVariable Integer idCliente,
            @Valid @RequestBody ClienteRequest request
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Cliente actualizado correctamente",
                        clienteService.actualizar(idCliente, request)
                ));
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente si no tiene operaciones asociadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Cliente con operaciones asociadas")
    })
    @DeleteMapping("/{idCliente}")
    public ResponseEntity<StandardApiResponse<Void>> eliminar(
            @Parameter(description = "Identificador interno del cliente", example = "1")
            @PathVariable Integer idCliente
    ) {
        clienteService.eliminar(idCliente);
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Cliente eliminado correctamente"));
    }
}
