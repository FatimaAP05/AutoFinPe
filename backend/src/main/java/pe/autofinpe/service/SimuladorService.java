package pe.autofinpe.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import pe.autofinpe.dto.simulador.CronogramaSimulacionResponse;
import pe.autofinpe.dto.simulador.IndicadoresSimulacionResponse;
import pe.autofinpe.dto.simulador.SimuladorRequest;
import pe.autofinpe.dto.simulador.SimuladorResponse;
import pe.autofinpe.exception.BusinessException;

@Service
public class SimuladorService {

    private static final int MONEY_SCALE = 2;
    private static final int RATE_SCALE = 4;
    private static final double EPSILON = 0.0000000001;

    public SimuladorResponse calcular(SimuladorRequest request) {
        validarReglasFinancieras(request);

        String moneda = request.getMoneda().trim().toUpperCase();
        String tipoTasa = request.getTipoTasa().trim().toUpperCase();
        String tipoGracia = request.getTipoGracia().trim().toUpperCase();

        double precioVehiculo = toDouble(request.getPrecioVehiculo());
        int plazoMeses = request.getPlazoMeses();
        int mesesGracia = request.getMesesGracia();
        double cuotaInicialPct = porcentaje(request.getCuotaInicialPct());
        double cuotaBalonPct = porcentaje(request.getCuotaBalonPct());
        double seguroDesgravamenPct = porcentaje(request.getSeguroDesgravamenPctMensual());
        double seguroVehicularPct = porcentaje(request.getSeguroVehicularPctAnual()) / 12.0;  // Convertir tasa anual a factor mensual
        double portesMensuales = toDouble(request.getPortesMensuales());

        double cuotaInicial = precioVehiculo * cuotaInicialPct;
        double saldoFinanciado = precioVehiculo - cuotaInicial;
        double tea = calcularTea(tipoTasa, request.getValorTasaPct(), request.getCapitalizacion());
        double tepMensual = Math.pow(1 + tea, 1.0 / 12.0) - 1;
        double cuotaBalon = precioVehiculo * cuotaBalonPct;

        double saldoDespuesGracia = calcularSaldoDespuesGracia(
                saldoFinanciado,
                tepMensual,
                tipoGracia,
                mesesGracia
        );
        // N = número de cuotas (cada mes del plazo total, incluyendo gracia)
        int cuotasRegulares = plazoMeses - mesesGracia;

        if (cuotaBalon >= saldoDespuesGracia) {
            throw new BusinessException("La cuota balon debe ser menor al saldo a amortizar");
        }

        double cuotaUniforme = calcularCuotaUniforme(saldoDespuesGracia, cuotaBalon, tepMensual, cuotasRegulares);
        List<CronogramaSimulacionResponse> cronograma = generarCronograma(
                plazoMeses,
                mesesGracia,
                tipoGracia,
                precioVehiculo,
                saldoFinanciado,
                tepMensual,
                cuotaBalon,
                cuotaUniforme,
                seguroDesgravamenPct,
                seguroVehicularPct,
                portesMensuales
        );

        IndicadoresSimulacionResponse indicadores = calcularIndicadores(
                saldoFinanciado,
                cronograma,
                request.getCokAnualPct()
        );

        return new SimuladorResponse(
                moneda,
                plazoMeses,
                money(precioVehiculo),
                money(cuotaInicial),
                money(saldoFinanciado),
                rate(tea * 100),
                rate(tepMensual * 100),
                money(cuotaBalon),
                money(cuotaUniforme),
                indicadores,
                cronograma
        );
    }

    private void validarReglasFinancieras(SimuladorRequest request) {
        if (!esCapitalizacionValida(request.getCapitalizacion())) {
            throw new BusinessException("La capitalizacion debe ser 1, 2, 4, 12 o 365");
        }

        String tipoGracia = request.getTipoGracia() == null ? "" : request.getTipoGracia().trim().toUpperCase();
        int mesesGracia = request.getMesesGracia() == null ? -1 : request.getMesesGracia();
        int plazoMeses = request.getPlazoMeses() == null ? 0 : request.getPlazoMeses();

        if ("S".equals(tipoGracia) && mesesGracia != 0) {
            throw new BusinessException("Si el tipo de gracia es S, los meses de gracia deben ser 0");
        }
        if (("T".equals(tipoGracia) || "P".equals(tipoGracia)) && (mesesGracia < 1 || mesesGracia > 6)) {
            throw new BusinessException("Si el tipo de gracia es T o P, los meses deben estar entre 1 y 6");
        }
        if (mesesGracia >= plazoMeses) {
            throw new BusinessException("Los meses de gracia deben ser menores al plazo total");
        }
    }

    private boolean esCapitalizacionValida(Short capitalizacion) {
        if (capitalizacion == null) {
            return false;
        }
        return capitalizacion == 1 || capitalizacion == 2 || capitalizacion == 4
                || capitalizacion == 12 || capitalizacion == 365;
    }

    private double calcularTea(String tipoTasa, BigDecimal valorTasaPct, Short capitalizacion) {
        double tasa = porcentaje(valorTasaPct);
        if ("E".equals(tipoTasa)) {
            return tasa;
        }
        return Math.pow(1 + tasa / capitalizacion, capitalizacion) - 1;
    }

    private double calcularSaldoDespuesGracia(
            double saldoFinanciado,
            double tepMensual,
            String tipoGracia,
            int mesesGracia
    ) {
        if (!"T".equals(tipoGracia)) {
            return saldoFinanciado;
        }
        return saldoFinanciado * Math.pow(1 + tepMensual, mesesGracia);
    }

    private double calcularCuotaUniforme(double saldo, double cuotaBalon, double tasaMensual, int meses) {
        if (Math.abs(tasaMensual) < EPSILON) {
            return (saldo - cuotaBalon) / meses;
        }
        double factorDescuento = Math.pow(1 + tasaMensual, -meses);
        return (saldo - cuotaBalon * factorDescuento) * tasaMensual / (1 - factorDescuento);
    }

    private List<CronogramaSimulacionResponse> generarCronograma(
            int plazoMeses,
            int mesesGracia,
            String tipoGracia,
            double precioVehiculo,
            double saldoFinanciado,
            double tepMensual,
            double cuotaBalon,
            double cuotaUniforme,
            double seguroDesgravamenPct,
            double seguroVehicularPct,
            double portesMensuales
    ) {
        List<CronogramaSimulacionResponse> cronograma = new ArrayList<>();
        double saldo = saldoFinanciado;

        for (int nroCuota = 1; nroCuota <= plazoMeses; nroCuota++) {
            double saldoInicial = saldo;
            double interes = saldoInicial * tepMensual;
            double amortizacion;
            double cuotaCredito;
            boolean esPeriodoGracia = nroCuota <= mesesGracia;
            boolean esUltimaCuota = nroCuota == plazoMeses;

            if (esPeriodoGracia && "T".equals(tipoGracia)) {
                // Gracia total: solo intereses, se capitalizan
                amortizacion = 0;
                cuotaCredito = 0;
                saldo = saldoInicial + interes;
            } else if (esPeriodoGracia && "P".equals(tipoGracia)) {
                // Gracia parcial: paga solo intereses, saldo no cambia
                amortizacion = 0;
                cuotaCredito = interes;
                saldo = saldoInicial;
            } else if (esUltimaCuota) {
                // Última cuota: amortización regular + balón se paga luego
                // La amortización deja saldo = VFR (que se paga como parte de cuota_total)
                amortizacion = saldoInicial - cuotaBalon;
                cuotaCredito = interes + amortizacion;
                saldo = 0;  // Todo se paga con balón incluido
            } else {
                // Cuota regular: cuota uniforme
                cuotaCredito = cuotaUniforme;
                amortizacion = cuotaCredito - interes;
                amortizacion = Math.max(amortizacion, 0);
                saldo = Math.max(saldoInicial - amortizacion, 0);
            }

            double seguroDesgravamen = saldoInicial * seguroDesgravamenPct;
            double seguroVehicular = precioVehiculo * seguroVehicularPct;
            // En última cuota, incluir el balón en cuota_total
            double cuotaBalonEnCuota = esUltimaCuota ? cuotaBalon : 0;
            double cuotaTotal = cuotaCredito + seguroDesgravamen + seguroVehicular + portesMensuales + cuotaBalonEnCuota;

            cronograma.add(new CronogramaSimulacionResponse(
                    nroCuota,
                    money(saldoInicial),
                    money(interes),
                    money(amortizacion),
                    money(seguroDesgravamen),
                    money(seguroVehicular),
                    money(portesMensuales),
                    money(cuotaCredito),
                    money(cuotaTotal),
                    money(saldo)
            ));
        }

        return cronograma;
    }

    private IndicadoresSimulacionResponse calcularIndicadores(
            double saldoFinanciado,
            List<CronogramaSimulacionResponse> cronograma,
            BigDecimal cokAnualPct
    ) {
        double[] flujos = new double[cronograma.size() + 1];
        flujos[0] = saldoFinanciado;

        double totalIntereses = 0;
        double totalAmortizacion = 0;
        double totalSeguros = 0;
        double totalPortes = 0;
        double totalPagado = 0;

        for (int index = 0; index < cronograma.size(); index++) {
            CronogramaSimulacionResponse cuota = cronograma.get(index);
            double cuotaTotal = toDouble(cuota.getCuotaTotal());

            flujos[index + 1] = -cuotaTotal;
            totalIntereses += toDouble(cuota.getInteres());
            totalAmortizacion += toDouble(cuota.getAmortizacion());
            totalSeguros += toDouble(cuota.getSeguroDesgravamen()) + toDouble(cuota.getSeguroVehicular());
            totalPortes += toDouble(cuota.getPortes());
            totalPagado += cuotaTotal;
        }
        
        // Flujos para TCEA (costo del crédito para el cliente)
        double[] flujosCredito = new double[cronograma.size() + 1];
        flujosCredito[0] = saldoFinanciado;
        for (int i = 0; i < cronograma.size(); i++) {
            flujosCredito[i + 1] = -toDouble(cronograma.get(i).getCuotaTotal());
        }
        
        double cokMensual = Math.pow(1 + porcentaje(cokAnualPct), 1.0 / 12.0) - 1;
        double van = calcularVan(flujos, cokMensual);
        double tirMensual = calcularTir(flujos);
        double tirAnual = Math.pow(1 + tirMensual, 12) - 1;
        double tceaMensual = calcularTir(flujosCredito);
        double tceaAnual = Math.pow(1 + tceaMensual, 12) - 1;
        
        return new IndicadoresSimulacionResponse(
                money(van),
                rate(tirMensual * 100),
                rate(tirAnual * 100),
                rate(tceaAnual * 100),
                money(totalIntereses),
                money(totalAmortizacion),
                money(totalSeguros),
                money(totalPortes),
                money(totalPagado)
        );
    }

    private double calcularVan(double[] flujos, double tasaDescuento) {
        double van = 0;
        for (int index = 0; index < flujos.length; index++) {
            van += flujos[index] / Math.pow(1 + tasaDescuento, index);
        }
        return van;
    }

    private double calcularTir(double[] flujos) {
        double minimo = -0.999999;
        double maximo = 10.0;
        double valorMinimo = calcularVan(flujos, minimo);
        double valorMaximo = calcularVan(flujos, maximo);

        if (valorMinimo * valorMaximo > 0) {
            return 0;
        }

        for (int iteracion = 0; iteracion < 200; iteracion++) {
            double medio = (minimo + maximo) / 2.0;
            double valorMedio = calcularVan(flujos, medio);

            if (Math.abs(valorMedio) < EPSILON) {
                return medio;
            }
            if (valorMinimo * valorMedio < 0) {
                maximo = medio;
                valorMaximo = valorMedio;
            } else {
                minimo = medio;
                valorMinimo = valorMedio;
            }
        }

        return (minimo + maximo) / 2.0;
    }

    private double porcentaje(BigDecimal value) {
        return toDouble(value) / 100.0;
    }

    private double toDouble(BigDecimal value) {
        return value.doubleValue();
    }

    private BigDecimal money(double value) {
        return BigDecimal.valueOf(value).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal rate(double value) {
        return BigDecimal.valueOf(value).setScale(RATE_SCALE, RoundingMode.HALF_UP);
    }
}
