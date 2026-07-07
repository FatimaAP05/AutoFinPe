package pe.autofinpe.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.autofinpe.dto.exportacion.ArchivoExportado;
import pe.autofinpe.exception.BusinessException;
import pe.autofinpe.exception.ResourceNotFoundException;
import pe.autofinpe.model.entity.Configuracion;
import pe.autofinpe.model.entity.Cronograma;
import pe.autofinpe.model.entity.Indicador;
import pe.autofinpe.model.entity.Operacion;
import pe.autofinpe.model.entity.Vehiculo;
import pe.autofinpe.repository.CronogramaRepository;
import pe.autofinpe.repository.OperacionRepository;

@Service
@Transactional(readOnly = true)
public class OperacionExportService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final OperacionRepository operacionRepository;
    private final CronogramaRepository cronogramaRepository;

    public OperacionExportService(
            OperacionRepository operacionRepository,
            CronogramaRepository cronogramaRepository
    ) {
        this.operacionRepository = operacionRepository;
        this.cronogramaRepository = cronogramaRepository;
    }

    public ArchivoExportado exportarPdf(Integer idOperacion) {
        ExportData data = cargarDatos(idOperacion);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCompressionLevel(0);
            document.open();

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    18,
                    com.lowagie.text.Font.BOLD
            );
            com.lowagie.text.Font sectionFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    12,
                    com.lowagie.text.Font.BOLD
            );
            com.lowagie.text.Font normalFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    8,
                    com.lowagie.text.Font.BOLD
            );
            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8);

            Paragraph title = new Paragraph("AutoFinPe - Cronograma de pagos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            document.add(new Paragraph("Fecha de generacion: " + LocalDateTime.now().format(DATE_FORMAT), normalFont));
            document.add(new Paragraph("Operacion: #" + data.operacion().getIdOperacion(), sectionFont));
            document.add(new Paragraph("Cliente: " + nombreCliente(data.operacion()), normalFont));
            document.add(new Paragraph("Vehiculo: " + descripcionVehiculo(data.operacion().getVehiculo()), normalFont));
            document.add(new Paragraph("Moneda: " + data.operacion().getConfiguracion().getMoneda(), normalFont));
            document.add(new Paragraph("Plazo: " + data.operacion().getPlazo() + " meses", normalFont));
            document.add(new Paragraph("Tasa: " + formatPercent(data.operacion().getValorTasa()), normalFont));
            document.add(new Paragraph("Tipo de tasa: " + tipoTasa(data.operacion().getConfiguracion()), normalFont));
            document.add(new Paragraph("Capitalizacion: " + data.operacion().getConfiguracion().getCapitalizacion(), normalFont));
            document.add(new Paragraph("Gracia: " + tipoGracia(data.operacion().getConfiguracion()), normalFont));
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Indicadores", sectionFont));
            PdfPTable indicatorsTable = new PdfPTable(6);
            indicatorsTable.setWidthPercentage(100);
            addPdfHeader(indicatorsTable, headerFont, "VAN", "TIR", "TCEA", "Total intereses", "Total seguros", "Total pagado");
            Indicador indicador = data.indicador();
            addPdfRow(indicatorsTable, cellFont,
                    formatMoney(indicador.getVan()),
                    formatPercent(indicador.getTir()),
                    formatPercent(indicador.getTcea()),
                    formatMoney(indicador.getTotalIntereses()),
                    formatMoney(indicador.getTotalSeguros()),
                    formatMoney(indicador.getTotalPagado())
            );
            indicatorsTable.setSpacingAfter(12);
            document.add(indicatorsTable);

            document.add(new Paragraph("Cronograma", sectionFont));
            PdfPTable scheduleTable = new PdfPTable(10);
            scheduleTable.setWidthPercentage(100);
            scheduleTable.setWidths(new float[]{0.6f, 1.2f, 1.0f, 1.1f, 1.2f, 1.2f, 0.9f, 1.1f, 1.1f, 1.2f});
            addPdfHeader(scheduleTable, headerFont, "Nro", "Saldo inicial", "Interes", "Amortizacion",
                    "Seg. desgrav.", "Seg. vehic.", "Portes", "Cuota credito", "Cuota total", "Saldo final");

            for (Cronograma cuota : data.cronograma()) {
                addPdfRow(scheduleTable, cellFont,
                        String.valueOf(cuota.getNroCuota()),
                        formatMoney(cuota.getSaldoInicial()),
                        formatMoney(cuota.getInteres()),
                        formatMoney(cuota.getAmortizacion()),
                        formatMoney(cuota.getSeguroDesgrav()),
                        formatMoney(cuota.getSeguroVehic()),
                        formatMoney(cuota.getPortes()),
                        formatMoney(cuota.getCuotaCredito()),
                        formatMoney(cuota.getCuotaTotal()),
                        formatMoney(cuota.getSaldoFinal())
                );
            }

            document.add(scheduleTable);
            document.close();

            return new ArchivoExportado(
                    "autofinpe-operacion-" + idOperacion + ".pdf",
                    PDF_CONTENT_TYPE,
                    outputStream.toByteArray()
            );
        } catch (DocumentException | IOException exception) {
            throw new BusinessException("No se pudo generar el PDF de la operacion");
        }
    }

    public ArchivoExportado exportarExcel(Integer idOperacion) {
        ExportData data = cargarDatos(idOperacion);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            WorkbookStyles styles = createWorkbookStyles(workbook);
            crearHojaResumen(workbook, styles, data);
            crearHojaCronograma(workbook, styles, data.cronograma());

            workbook.write(outputStream);

            return new ArchivoExportado(
                    "autofinpe-operacion-" + idOperacion + ".xlsx",
                    EXCEL_CONTENT_TYPE,
                    outputStream.toByteArray()
            );
        } catch (IOException exception) {
            throw new BusinessException("No se pudo generar el Excel de la operacion");
        }
    }

    private ExportData cargarDatos(Integer idOperacion) {
        Operacion operacion = operacionRepository.findByIdWithDetails(idOperacion)
                .orElseThrow(() -> new ResourceNotFoundException("Operacion no encontrada"));
        Indicador indicador = operacion.getIndicador();

        if (indicador == null) {
            throw new ResourceNotFoundException("Indicadores no encontrados");
        }

        List<Cronograma> cronograma = cronogramaRepository.findByOperacionOrderByNroCuotaAsc(operacion);
        if (cronograma.isEmpty()) {
            throw new ResourceNotFoundException("Cronograma no encontrado");
        }

        return new ExportData(operacion, indicador, cronograma);
    }

    private void crearHojaResumen(Workbook workbook, WorkbookStyles styles, ExportData data) {
        Sheet sheet = workbook.createSheet("Resumen");
        int rowIndex = 0;

        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("AutoFinPe - Resumen de operacion");
        titleCell.setCellStyle(styles.title());

        rowIndex++;
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Fecha de generacion", LocalDateTime.now().format(DATE_FORMAT));
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Operacion", data.operacion().getIdOperacion());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Cliente", nombreCliente(data.operacion()));
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Vehiculo", descripcionVehiculo(data.operacion().getVehiculo()));
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Moneda", data.operacion().getConfiguracion().getMoneda());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Plazo", data.operacion().getPlazo());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Tasa", data.operacion().getValorTasa(), styles.percent());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Tipo de tasa", tipoTasa(data.operacion().getConfiguracion()));
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Capitalizacion", data.operacion().getConfiguracion().getCapitalizacion());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Gracia", tipoGracia(data.operacion().getConfiguracion()));

        rowIndex++;
        Row header = sheet.createRow(rowIndex++);
        addHeaderCell(header, 0, styles, "Indicador");
        addHeaderCell(header, 1, styles, "Valor");

        rowIndex = addSummaryRow(sheet, rowIndex, styles, "VAN", data.indicador().getVan(), styles.money());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "TIR", data.indicador().getTir(), styles.percent());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "TCEA", data.indicador().getTcea(), styles.percent());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Total intereses", data.indicador().getTotalIntereses(), styles.money());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Total amortizacion", data.indicador().getTotalAmortizacion(), styles.money());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Total seguros", data.indicador().getTotalSeguros(), styles.money());
        rowIndex = addSummaryRow(sheet, rowIndex, styles, "Total portes", data.indicador().getTotalPortes(), styles.money());
        addSummaryRow(sheet, rowIndex, styles, "Total pagado", data.indicador().getTotalPagado(), styles.money());

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void crearHojaCronograma(Workbook workbook, WorkbookStyles styles, List<Cronograma> cronograma) {
        Sheet sheet = workbook.createSheet("Cronograma");
        Row header = sheet.createRow(0);
        String[] headers = {
                "Nro", "Saldo inicial", "Interes", "Amortizacion", "Seguro desgravamen",
                "Seguro vehicular", "Portes", "Cuota credito", "Cuota total", "Saldo final"
        };

        for (int index = 0; index < headers.length; index++) {
            addHeaderCell(header, index, styles, headers[index]);
        }

        int rowIndex = 1;
        for (Cronograma cuota : cronograma) {
            Row row = sheet.createRow(rowIndex++);
            addNumericCell(row, 0, cuota.getNroCuota(), styles.integer());
            addNumericCell(row, 1, cuota.getSaldoInicial(), styles.money());
            addNumericCell(row, 2, cuota.getInteres(), styles.money());
            addNumericCell(row, 3, cuota.getAmortizacion(), styles.money());
            addNumericCell(row, 4, cuota.getSeguroDesgrav(), styles.money());
            addNumericCell(row, 5, cuota.getSeguroVehic(), styles.money());
            addNumericCell(row, 6, cuota.getPortes(), styles.money());
            addNumericCell(row, 7, cuota.getCuotaCredito(), styles.money());
            addNumericCell(row, 8, cuota.getCuotaTotal(), styles.money());
            addNumericCell(row, 9, cuota.getSaldoFinal(), styles.money());
        }

        for (int index = 0; index < headers.length; index++) {
            sheet.autoSizeColumn(index);
        }
    }

    private WorkbookStyles createWorkbookStyles(Workbook workbook) {
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        DataFormat dataFormat = workbook.createDataFormat();

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(dataFormat.getFormat("0.0000"));

        CellStyle integerStyle = workbook.createCellStyle();
        integerStyle.setDataFormat(dataFormat.getFormat("0"));

        return new WorkbookStyles(titleStyle, headerStyle, moneyStyle, percentStyle, integerStyle);
    }

    private int addSummaryRow(Sheet sheet, int rowIndex, WorkbookStyles styles, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        addHeaderCell(row, 0, styles, label);
        row.createCell(1).setCellValue(value);
        return rowIndex + 1;
    }

    private int addSummaryRow(Sheet sheet, int rowIndex, WorkbookStyles styles, String label, Number value) {
        Row row = sheet.createRow(rowIndex);
        addHeaderCell(row, 0, styles, label);
        addNumericCell(row, 1, value, styles.integer());
        return rowIndex + 1;
    }

    private int addSummaryRow(Sheet sheet, int rowIndex, WorkbookStyles styles, String label, BigDecimal value, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        addHeaderCell(row, 0, styles, label);
        addNumericCell(row, 1, value, style);
        return rowIndex + 1;
    }

    private void addHeaderCell(Row row, int index, WorkbookStyles styles, String value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(styles.header());
    }

    private void addNumericCell(Row row, int index, Number value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private void addPdfHeader(PdfPTable table, com.lowagie.text.Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addPdfRow(PdfPTable table, com.lowagie.text.Font font, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
        }
    }

    private String nombreCliente(Operacion operacion) {
        return operacion.getCliente().getNombres() + " " + operacion.getCliente().getApellidos()
                + " (DNI " + operacion.getCliente().getDni() + ")";
    }

    private String descripcionVehiculo(Vehiculo vehiculo) {
        return vehiculo.getMarca() + " " + vehiculo.getModelo() + " " + vehiculo.getAnio()
                + " - " + vehiculo.getCategoria();
    }

    private String tipoTasa(Configuracion configuracion) {
        return "E".equals(configuracion.getTipoTasa()) ? "Efectiva" : "Nominal";
    }

    private String tipoGracia(Configuracion configuracion) {
        return switch (configuracion.getTipoGracia()) {
            case "T" -> "Total (" + configuracion.getMesesGracia() + " meses)";
            case "P" -> "Parcial (" + configuracion.getMesesGracia() + " meses)";
            default -> "Sin gracia";
        };
    }

    private String formatMoney(BigDecimal value) {
        return String.format("%,.2f", value);
    }

    private String formatPercent(BigDecimal value) {
        return value + "%";
    }

    private record ExportData(Operacion operacion, Indicador indicador, List<Cronograma> cronograma) {
    }

    private record WorkbookStyles(
            CellStyle title,
            CellStyle header,
            CellStyle money,
            CellStyle percent,
            CellStyle integer
    ) {
    }
}
