package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.autofinpe.dto.common.StandardApiResponse;
import pe.autofinpe.dto.operacion.CronogramaResponse;
import pe.autofinpe.dto.operacion.IndicadorResponse;
import pe.autofinpe.dto.operacion.OperacionRequest;
import pe.autofinpe.dto.operacion.OperacionResponse;
import pe.autofinpe.dto.exportacion.ArchivoExportado;
import pe.autofinpe.service.OperacionExportService;
import pe.autofinpe.service.OperacionService;

@RestController
@RequestMapping("/operaciones")
@Tag(name = "Operaciones", description = "Gestión de operaciones de crédito vehicular")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
public class OperacionController {

    private final OperacionService operacionService;
    private final OperacionExportService operacionExportService;

    public OperacionController(
            OperacionService operacionService,
            OperacionExportService operacionExportService
    ) {
        this.operacionService = operacionService;
        this.operacionExportService = operacionExportService;
    }

    @GetMapping
    @Operation(summary = "Listar operaciones", description = "Lista las operaciones registradas ordenadas por fecha descendente.")
    public ResponseEntity<StandardApiResponse<List<OperacionResponse>>> listarOperaciones() {
        List<OperacionResponse> operaciones = operacionService.listarOperaciones();
        return new ResponseEntity<>(
                StandardApiResponse.success("Operaciones obtenidas exitosamente.", operaciones),
                HttpStatus.OK
        );
    }

    @PostMapping
    @Operation(summary = "Crear una nueva operación crediticia", description = "Crea una nueva operación a partir de una simulación y la guarda en la base de datos.")
    public ResponseEntity<StandardApiResponse<OperacionResponse>> crearOperacion(
            @Valid @RequestBody OperacionRequest request
    ) {
        OperacionResponse operacion = operacionService.crearOperacion(request);
        return new ResponseEntity<>(
                StandardApiResponse.success("Operación creada exitosamente.", operacion),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una operación por ID", description = "Obtiene los detalles completos de una operación, incluyendo su cronograma e indicadores.")
    public ResponseEntity<StandardApiResponse<OperacionResponse>> obtenerOperacion(
            @PathVariable Integer id
    ) {
        OperacionResponse operacion = operacionService.obtenerOperacion(id);
        return new ResponseEntity<>(
                StandardApiResponse.success("Operación obtenida exitosamente.", operacion),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/cronograma")
    @Operation(summary = "Obtener el cronograma de una operación", description = "Obtiene la lista de cuotas del cronograma de pagos para una operación específica.")
    public ResponseEntity<StandardApiResponse<List<CronogramaResponse>>> obtenerCronograma(
            @PathVariable Integer id
    ) {
        List<CronogramaResponse> cronograma = operacionService.obtenerCronograma(id);
        return new ResponseEntity<>(
                StandardApiResponse.success("Cronograma obtenido exitosamente.", cronograma),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/indicadores")
    @Operation(summary = "Obtener los indicadores de una operación", description = "Obtiene los indicadores financieros (TCEA, VAN, TIR, etc.) para una operación específica.")
    public ResponseEntity<StandardApiResponse<IndicadorResponse>> obtenerIndicadores(
            @PathVariable Integer id
    ) {
        IndicadorResponse indicadores = operacionService.obtenerIndicadores(id);
        return new ResponseEntity<>(
                StandardApiResponse.success("Indicadores obtenidos exitosamente.", indicadores),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/export/pdf")
    @Operation(summary = "Exportar operación en PDF", description = "Genera un archivo PDF con datos generales, indicadores y cronograma.")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Integer id) {
        return buildDownloadResponse(operacionExportService.exportarPdf(id));
    }

    @GetMapping("/{id}/export/excel")
    @Operation(summary = "Exportar operación en Excel", description = "Genera un archivo Excel con hoja de resumen y cronograma.")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Integer id) {
        return buildDownloadResponse(operacionExportService.exportarExcel(id));
    }

    private ResponseEntity<byte[]> buildDownloadResponse(ArchivoExportado archivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getContentType()))
                .contentLength(archivo.getContent().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo.getFilename()).build().toString()
                )
                .body(archivo.getContent());
    }
}
