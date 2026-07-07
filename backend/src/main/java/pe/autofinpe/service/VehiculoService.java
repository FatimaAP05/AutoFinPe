package pe.autofinpe.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.autofinpe.dto.vehiculo.VehiculoRequest;
import pe.autofinpe.dto.vehiculo.VehiculoResponse;
import pe.autofinpe.exception.BusinessException;
import pe.autofinpe.exception.ResourceNotFoundException;
import pe.autofinpe.model.entity.Vehiculo;
import pe.autofinpe.repository.OperacionRepository;
import pe.autofinpe.repository.VehiculoRepository;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final OperacionRepository operacionRepository;
    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");
    private static final String UPLOAD_SUB_DIR = "vehiculos";

    public VehiculoService(VehiculoRepository vehiculoRepository, OperacionRepository operacionRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.operacionRepository = operacionRepository;
        String uploadDir = "uploads";
        this.fileStorageLocation = Paths.get(uploadDir, UPLOAD_SUB_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para almacenar los archivos subidos.", ex);
        }
    }

    @Transactional
    public VehiculoResponse crear(VehiculoRequest request) {
        Vehiculo vehiculo = new Vehiculo();
        aplicarDatos(vehiculo, request);

        return toResponseWithUrl(vehiculoRepository.save(vehiculo));
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listar() {
        return vehiculoRepository.findAll(Sort.by("marca").ascending()
                        .and(Sort.by("modelo").ascending())
                        .and(Sort.by("anio").descending()))
                .stream()
                .map(this::toResponseWithUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehiculoResponse buscarPorId(Integer idVehiculo) {
        return toResponseWithUrl(obtenerEntidadPorId(idVehiculo));
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> buscarPorMarca(String marca) {
        return vehiculoRepository.findByMarcaIgnoreCase(marca.trim())
                .stream()
                .map(this::toResponseWithUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> buscarPorCategoria(String categoria) {
        return vehiculoRepository.findByCategoriaIgnoreCase(categoria.trim())
                .stream()
                .map(this::toResponseWithUrl)
                .toList();
    }

    @Transactional
    public VehiculoResponse actualizar(Integer idVehiculo, VehiculoRequest request) {
        Vehiculo vehiculo = obtenerEntidadPorId(idVehiculo);
        aplicarDatos(vehiculo, request);

        return toResponseWithUrl(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public void eliminar(Integer idVehiculo) {
        Vehiculo vehiculo = obtenerEntidadPorId(idVehiculo);
        String imagenAnterior = vehiculo.getImagenUrl();

        if (operacionRepository.existsByVehiculoIdVehiculo(idVehiculo)) {
            throw new BusinessException("No se puede eliminar el vehiculo porque tiene operaciones asociadas");
        }

        vehiculoRepository.delete(vehiculo);
        eliminarArchivoImagen(imagenAnterior);
    }

    @Transactional
    public VehiculoResponse guardarImagen(Integer id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo de imagen es obligatorio.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Tipo de archivo no permitido. Use JPG, PNG o WEBP.");
        }

        Vehiculo vehiculo = obtenerEntidadPorId(id);
        String imagenAnterior = vehiculo.getImagenUrl();

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        String newFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            vehiculo.setImagenUrl(newFileName);
            Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
            eliminarArchivoImagen(imagenAnterior);
            return toResponseWithUrl(vehiculoActualizado);

        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + newFileName + ". Por favor, intente de nuevo.", ex);
        }
    }

    private Vehiculo obtenerEntidadPorId(Integer idVehiculo) {
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado"));
    }

    private void aplicarDatos(Vehiculo vehiculo, VehiculoRequest request) {
        vehiculo.setMarca(normalizarTexto(request.getMarca()));
        vehiculo.setModelo(normalizarTexto(request.getModelo()));
        vehiculo.setAnio(request.getAnio());
        vehiculo.setPrecioPen(request.getPrecioPen());
        vehiculo.setPrecioUsd(request.getPrecioUsd());
        vehiculo.setCategoria(normalizarTexto(request.getCategoria()));
    }

    private String normalizarTexto(String value) {
        return value.trim();
    }

    private void eliminarArchivoImagen(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path imagePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (!imagePath.startsWith(this.fileStorageLocation)) {
                return;
            }
            Files.deleteIfExists(imagePath);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo eliminar la imagen anterior del vehiculo.", ex);
        }
    }

    private VehiculoResponse toResponseWithUrl(Vehiculo vehiculo) {
        String imagenUrl = null;
        if (vehiculo.getImagenUrl() != null && !vehiculo.getImagenUrl().isBlank()) {
            imagenUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/" + UPLOAD_SUB_DIR + "/")
                    .path(vehiculo.getImagenUrl())
                    .toUriString();
        }

        return new VehiculoResponse(
                vehiculo.getIdVehiculo(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getPrecioPen(),
                vehiculo.getPrecioUsd(),
                vehiculo.getCategoria(),
                imagenUrl
        );
    }
}
