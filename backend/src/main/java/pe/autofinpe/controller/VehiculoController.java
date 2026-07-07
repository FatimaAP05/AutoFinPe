package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.autofinpe.dto.common.StandardApiResponse;
import pe.autofinpe.dto.vehiculo.VehiculoRequest;
import pe.autofinpe.dto.vehiculo.VehiculoResponse;
import pe.autofinpe.exception.ApiStatusCode;
import pe.autofinpe.service.VehiculoService;

@Tag(name = "Vehiculos", description = "CRUD de vehiculos disponibles para simulaciones de credito")
@SecurityRequirement(name = "bearer-jwt")
@Validated
@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Operation(summary = "Crear vehiculo", description = "Registra un vehiculo validando anio, marca, modelo, precios y categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehiculo creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @PostMapping
    public ResponseEntity<StandardApiResponse<VehiculoResponse>> crear(@Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse response = vehiculoService.crear(request);
        return ResponseEntity
                .status(ApiStatusCode.CREATED.getHttpStatus())
                .location(URI.create("/api/vehiculos/" + response.getIdVehiculo()))
                .body(StandardApiResponse.success("Vehiculo creado correctamente", response));
    }

    @Operation(summary = "Listar vehiculos", description = "Obtiene todos los vehiculos ordenados por marca, modelo y anio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<VehiculoResponse>>> listar() {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Vehiculos obtenidos correctamente", vehiculoService.listar()));
    }

    @Operation(summary = "Buscar vehiculo por ID", description = "Obtiene un vehiculo usando su identificador interno")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehiculo encontrado"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Vehiculo no encontrado")
    })
    @GetMapping("/{idVehiculo}")
    public ResponseEntity<StandardApiResponse<VehiculoResponse>> buscarPorId(
            @Parameter(description = "Identificador interno del vehiculo", example = "1")
            @PathVariable Integer idVehiculo
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Vehiculo encontrado", vehiculoService.buscarPorId(idVehiculo)));
    }

    @Operation(summary = "Buscar vehiculos por marca", description = "Obtiene vehiculos filtrados por marca exacta sin distinguir mayusculas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busqueda completada"),
            @ApiResponse(responseCode = "400", description = "Marca invalida"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @GetMapping("/marca/{marca}")
    public ResponseEntity<StandardApiResponse<List<VehiculoResponse>>> buscarPorMarca(
            @Parameter(description = "Marca del vehiculo", example = "Toyota")
            @PathVariable
            @NotBlank(message = "La marca es obligatoria")
            @Size(max = 30, message = "La marca no debe superar 30 caracteres")
            @Pattern(
                    regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .'-]+$",
                    message = "La marca solo puede contener letras, numeros, espacios, guiones y apostrofes"
            )
            String marca
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Vehiculos obtenidos correctamente",
                        vehiculoService.buscarPorMarca(marca)
                ));
    }

    @Operation(summary = "Buscar vehiculos por categoria", description = "Obtiene vehiculos filtrados por categoria exacta sin distinguir mayusculas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busqueda completada"),
            @ApiResponse(responseCode = "400", description = "Categoria invalida"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<StandardApiResponse<List<VehiculoResponse>>> buscarPorCategoria(
            @Parameter(description = "Categoria del vehiculo", example = "SUV")
            @PathVariable
            @NotBlank(message = "La categoria es obligatoria")
            @Size(max = 30, message = "La categoria no debe superar 30 caracteres")
            @Pattern(
                    regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .'-]+$",
                    message = "La categoria solo puede contener letras, numeros, espacios, guiones y apostrofes"
            )
            String categoria
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Vehiculos obtenidos correctamente",
                        vehiculoService.buscarPorCategoria(categoria)
                ));
    }

    @Operation(summary = "Actualizar vehiculo", description = "Actualiza los datos de un vehiculo existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehiculo actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Vehiculo no encontrado")
    })
    @PutMapping("/{idVehiculo}")
    public ResponseEntity<StandardApiResponse<VehiculoResponse>> actualizar(
            @Parameter(description = "Identificador interno del vehiculo", example = "1")
            @PathVariable Integer idVehiculo,
            @Valid @RequestBody VehiculoRequest request
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Vehiculo actualizado correctamente",
                        vehiculoService.actualizar(idVehiculo, request)
                ));
    }

    @Operation(summary = "Eliminar vehiculo", description = "Elimina un vehiculo si no tiene operaciones asociadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehiculo eliminado"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Vehiculo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Vehiculo con operaciones asociadas")
    })
    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<StandardApiResponse<Void>> eliminar(
            @Parameter(description = "Identificador interno del vehiculo", example = "1")
            @PathVariable Integer idVehiculo
    ) {
        vehiculoService.eliminar(idVehiculo);
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success("Vehiculo eliminado correctamente"));
    }

    @Operation(summary = "Subir imagen de vehiculo", description = "Sube una imagen para un vehiculo existente y actualiza su URL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen subida correctamente"),
            @ApiResponse(responseCode = "400", description = "Archivo invalido o ausente"),
            @ApiResponse(responseCode = "404", description = "Vehiculo no encontrado")
    })
    @PostMapping("/{idVehiculo}/imagen")
    public ResponseEntity<StandardApiResponse<VehiculoResponse>> subirImagen(
            @Parameter(description = "Identificador interno del vehiculo", example = "1")
            @PathVariable Integer idVehiculo,
            @Parameter(description = "Archivo de imagen (jpg, jpeg, png, webp)")
            @RequestParam("imagen") MultipartFile imagen
    ) {
        VehiculoResponse response = vehiculoService.guardarImagen(idVehiculo, imagen);
        return ResponseEntity.ok(StandardApiResponse.success("Imagen del vehiculo actualizada", response));
    }
}
