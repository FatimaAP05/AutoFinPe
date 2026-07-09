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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
    private static final int EXCEL_STYLE_TITLE = 1;
    private static final int EXCEL_STYLE_HEADER = 2;
    private static final int EXCEL_STYLE_MONEY = 3;
    private static final int EXCEL_STYLE_PERCENT = 4;
    private static final int EXCEL_STYLE_INTEGER = 5;

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

        try {
            return new ArchivoExportado(
                    "autofinpe-operacion-" + idOperacion + ".xlsx",
                    EXCEL_CONTENT_TYPE,
                    crearExcelXlsx(data)
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

    private byte[] crearExcelXlsx(ExportData data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            addZipEntry(zip, "[Content_Types].xml", contentTypesXml());
            addZipEntry(zip, "_rels/.rels", rootRelsXml());
            addZipEntry(zip, "xl/workbook.xml", workbookXml());
            addZipEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml());
            addZipEntry(zip, "xl/styles.xml", stylesXml());
            addZipEntry(zip, "xl/worksheets/sheet1.xml", crearHojaResumenXml(data));
            addZipEntry(zip, "xl/worksheets/sheet2.xml", crearHojaCronogramaXml(data.cronograma()));
            zip.finish();
            return outputStream.toByteArray();
        }
    }

    private String crearHojaResumenXml(ExportData data) {
        StringBuilder sheet = new StringBuilder();
        appendSheetStart(sheet, 28, 42);

        int rowIndex = 1;
        appendTextRow(sheet, rowIndex++, EXCEL_STYLE_TITLE, "AutoFinPe - Resumen de operacion");
        rowIndex++;
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Fecha de generacion", LocalDateTime.now().format(DATE_FORMAT));
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Operacion", data.operacion().getIdOperacion(), EXCEL_STYLE_INTEGER);
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Cliente", nombreCliente(data.operacion()));
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Vehiculo", descripcionVehiculo(data.operacion().getVehiculo()));
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Moneda", data.operacion().getConfiguracion().getMoneda());
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Plazo", data.operacion().getPlazo(), EXCEL_STYLE_INTEGER);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Tasa", data.operacion().getValorTasa(), EXCEL_STYLE_PERCENT);
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Tipo de tasa", tipoTasa(data.operacion().getConfiguracion()));
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Capitalizacion", data.operacion().getConfiguracion().getCapitalizacion(), EXCEL_STYLE_INTEGER);
        rowIndex = appendSummaryTextRow(sheet, rowIndex, "Gracia", tipoGracia(data.operacion().getConfiguracion()));

        rowIndex++;
        appendTextRow(sheet, rowIndex++, EXCEL_STYLE_HEADER, "Indicador", "Valor");

        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "VAN", data.indicador().getVan(), EXCEL_STYLE_MONEY);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "TIR", data.indicador().getTir(), EXCEL_STYLE_PERCENT);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "TCEA", data.indicador().getTcea(), EXCEL_STYLE_PERCENT);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Total intereses", data.indicador().getTotalIntereses(), EXCEL_STYLE_MONEY);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Total amortizacion", data.indicador().getTotalAmortizacion(), EXCEL_STYLE_MONEY);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Total seguros", data.indicador().getTotalSeguros(), EXCEL_STYLE_MONEY);
        rowIndex = appendSummaryNumberRow(sheet, rowIndex, "Total portes", data.indicador().getTotalPortes(), EXCEL_STYLE_MONEY);
        appendSummaryNumberRow(sheet, rowIndex, "Total pagado", data.indicador().getTotalPagado(), EXCEL_STYLE_MONEY);

        appendSheetEnd(sheet);
        return sheet.toString();
    }

    private String crearHojaCronogramaXml(List<Cronograma> cronograma) {
        StringBuilder sheet = new StringBuilder();
        appendSheetStart(sheet, 10, 18, 14, 16, 22, 20, 12, 16, 16, 16);
        appendTextRow(sheet, 1, EXCEL_STYLE_HEADER,
                "Nro", "Saldo inicial", "Interes", "Amortizacion", "Seguro desgravamen",
                "Seguro vehicular", "Portes", "Cuota credito", "Cuota total", "Saldo final"
        );

        int rowIndex = 2;
        for (Cronograma cuota : cronograma) {
            sheet.append("<row r=\"").append(rowIndex).append("\">");
            appendNumberCell(sheet, 0, rowIndex, cuota.getNroCuota(), EXCEL_STYLE_INTEGER);
            appendNumberCell(sheet, 1, rowIndex, cuota.getSaldoInicial(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 2, rowIndex, cuota.getInteres(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 3, rowIndex, cuota.getAmortizacion(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 4, rowIndex, cuota.getSeguroDesgrav(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 5, rowIndex, cuota.getSeguroVehic(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 6, rowIndex, cuota.getPortes(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 7, rowIndex, cuota.getCuotaCredito(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 8, rowIndex, cuota.getCuotaTotal(), EXCEL_STYLE_MONEY);
            appendNumberCell(sheet, 9, rowIndex, cuota.getSaldoFinal(), EXCEL_STYLE_MONEY);
            sheet.append("</row>");
            rowIndex++;
        }

        appendSheetEnd(sheet);
        return sheet.toString();
    }

    private int appendSummaryTextRow(StringBuilder sheet, int rowIndex, String label, String value) {
        sheet.append("<row r=\"").append(rowIndex).append("\">");
        appendTextCell(sheet, 0, rowIndex, label, EXCEL_STYLE_HEADER);
        appendTextCell(sheet, 1, rowIndex, value, 0);
        sheet.append("</row>");
        return rowIndex + 1;
    }

    private int appendSummaryNumberRow(StringBuilder sheet, int rowIndex, String label, Number value, int styleIndex) {
        sheet.append("<row r=\"").append(rowIndex).append("\">");
        appendTextCell(sheet, 0, rowIndex, label, EXCEL_STYLE_HEADER);
        appendNumberCell(sheet, 1, rowIndex, value, styleIndex);
        sheet.append("</row>");
        return rowIndex + 1;
    }

    private void appendTextRow(StringBuilder sheet, int rowIndex, int styleIndex, String... values) {
        sheet.append("<row r=\"").append(rowIndex).append("\">");
        for (int index = 0; index < values.length; index++) {
            appendTextCell(sheet, index, rowIndex, values[index], styleIndex);
        }
        sheet.append("</row>");
    }

    private void appendTextCell(StringBuilder sheet, int columnIndex, int rowIndex, String value, int styleIndex) {
        sheet.append("<c r=\"").append(cellReference(columnIndex, rowIndex)).append("\"");
        if (styleIndex > 0) {
            sheet.append(" s=\"").append(styleIndex).append("\"");
        }
        sheet.append(" t=\"inlineStr\"><is><t>").append(escapeXml(value)).append("</t></is></c>");
    }

    private void appendNumberCell(StringBuilder sheet, int columnIndex, int rowIndex, Number value, int styleIndex) {
        sheet.append("<c r=\"").append(cellReference(columnIndex, rowIndex)).append("\" s=\"")
                .append(styleIndex).append("\"><v>").append(numberValue(value)).append("</v></c>");
    }

    private void appendSheetStart(StringBuilder sheet, int... columnWidths) {
        sheet.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
                .append("<cols>");
        for (int index = 0; index < columnWidths.length; index++) {
            int column = index + 1;
            sheet.append("<col min=\"").append(column).append("\" max=\"").append(column)
                    .append("\" width=\"").append(columnWidths[index]).append("\" customWidth=\"1\"/>");
        }
        sheet.append("</cols><sheetData>");
    }

    private void appendSheetEnd(StringBuilder sheet) {
        sheet.append("</sheetData></worksheet>");
    }

    private void addZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String cellReference(int columnIndex, int rowIndex) {
        return columnName(columnIndex) + rowIndex;
    }

    private String columnName(int columnIndex) {
        StringBuilder name = new StringBuilder();
        int value = columnIndex + 1;
        while (value > 0) {
            int remainder = (value - 1) % 26;
            name.insert(0, (char) ('A' + remainder));
            value = (value - 1) / 26;
        }
        return name.toString();
    }

    private String numberValue(Number value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
        return value.toString();
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String contentTypesXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                </Types>
                """;
    }

    private String rootRelsXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                </Relationships>
                """;
    }

    private String workbookXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheets>
                    <sheet name="Resumen" sheetId="1" r:id="rId1"/>
                    <sheet name="Cronograma" sheetId="2" r:id="rId2"/>
                  </sheets>
                </workbook>
                """;
    }

    private String workbookRelsXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
                  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """;
    }

    private String stylesXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <numFmts count="2">
                    <numFmt numFmtId="164" formatCode="#,##0.00"/>
                    <numFmt numFmtId="165" formatCode="0.0000"/>
                  </numFmts>
                  <fonts count="2">
                    <font><sz val="11"/><name val="Calibri"/></font>
                    <font><b/><sz val="11"/><name val="Calibri"/></font>
                  </fonts>
                  <fills count="2">
                    <fill><patternFill patternType="none"/></fill>
                    <fill><patternFill patternType="gray125"/></fill>
                  </fills>
                  <borders count="1"><border/></borders>
                  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
                  <cellXfs count="6">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
                    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
                    <xf numFmtId="164" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                    <xf numFmtId="165" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                    <xf numFmtId="1" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                  </cellXfs>
                  <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
                </styleSheet>
                """;
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
}
