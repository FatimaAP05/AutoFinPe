package pe.autofinpe.dto.exportacion;

public class ArchivoExportado {

    private final String filename;
    private final String contentType;
    private final byte[] content;

    public ArchivoExportado(String filename, String contentType, byte[] content) {
        this.filename = filename;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
