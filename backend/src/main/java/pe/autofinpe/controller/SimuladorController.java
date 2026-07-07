package pe.autofinpe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.autofinpe.dto.common.StandardApiResponse;
import pe.autofinpe.dto.simulador.SimuladorRequest;
import pe.autofinpe.dto.simulador.SimuladorResponse;
import pe.autofinpe.exception.ApiStatusCode;
import pe.autofinpe.service.SimuladorService;

@Tag(name = "Simulador", description = "Calculo financiero de credito vehicular sin persistencia")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/simulador")
public class SimuladorController {

    private final SimuladorService simuladorService;

    public SimuladorController(SimuladorService simuladorService) {
        this.simuladorService = simuladorService;
    }

    @Operation(
            summary = "Calcular simulacion financiera",
            description = "Genera indicadores y cronograma completo usando metodo frances, cuota balon y gracia"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulacion calculada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "JWT ausente o invalido"),
            @ApiResponse(responseCode = "409", description = "Regla financiera no satisfecha")
    })
    @PostMapping("/calcular")
    public ResponseEntity<StandardApiResponse<SimuladorResponse>> calcular(
            @Valid @RequestBody SimuladorRequest request
    ) {
        return ResponseEntity
                .status(ApiStatusCode.OK.getHttpStatus())
                .body(StandardApiResponse.success(
                        "Simulacion calculada correctamente",
                        simuladorService.calcular(request)
                ));
    }
}
