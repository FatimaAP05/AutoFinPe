package pe.autofinpe.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.autofinpe.dto.cliente.ClienteRequest;
import pe.autofinpe.dto.cliente.ClienteResponse;
import pe.autofinpe.exception.BusinessException;
import pe.autofinpe.exception.DuplicateResourceException;
import pe.autofinpe.exception.ResourceNotFoundException;
import pe.autofinpe.model.entity.Cliente;
import pe.autofinpe.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        validarDniDisponible(request.getDni());

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, request);

        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll(Sort.by("apellidos").ascending().and(Sort.by("nombres").ascending()))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Integer idCliente) {
        return toResponse(obtenerEntidadPorId(idCliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    @Transactional
    public ClienteResponse actualizar(Integer idCliente, ClienteRequest request) {
        Cliente cliente = obtenerEntidadPorId(idCliente);

        if (clienteRepository.existsByDniAndIdClienteNot(request.getDni(), idCliente)) {
            throw new DuplicateResourceException("Ya existe un cliente con el DNI indicado");
        }

        aplicarDatos(cliente, request);
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Integer idCliente) {
        Cliente cliente = obtenerEntidadPorId(idCliente);

        try {
            clienteRepository.delete(cliente);
            clienteRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene operaciones asociadas");
        }
    }

    private Cliente obtenerEntidadPorId(Integer idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private void validarDniDisponible(String dni) {
        if (clienteRepository.existsByDni(dni)) {
            throw new DuplicateResourceException("Ya existe un cliente con el DNI indicado");
        }
    }

    private void aplicarDatos(Cliente cliente, ClienteRequest request) {
        cliente.setDni(request.getDni());
        cliente.setNombres(request.getNombres().trim());
        cliente.setApellidos(request.getApellidos().trim());
        cliente.setIngresoMensual(request.getIngresoMensual());
        cliente.setCalificacion(request.getCalificacion());
        cliente.setTelefono(normalizarTextoOpcional(request.getTelefono()));
        cliente.setEmail(normalizarTextoOpcional(request.getEmail()));
    }

    private String normalizarTextoOpcional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getDni(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getIngresoMensual(),
                cliente.getCalificacion(),
                cliente.getTelefono(),
                cliente.getEmail()
        );
    }
}
