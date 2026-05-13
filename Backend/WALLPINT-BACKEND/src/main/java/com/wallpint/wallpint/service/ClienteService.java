package com.wallpint.wallpint.service;

import com.wallpint.wallpint.dto.RegisterRequest;
import com.wallpint.wallpint.model.Cliente;
import com.wallpint.wallpint.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Constructor para inyectar el repositorio
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Método para listar todos los clientes
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    // Método para guardar un nuevo cliente
    public Cliente guardarCliente(Cliente cliente) {

        // Encripta la contraseña antes de guardarla
        String passwordHash = passwordEncoder.encode(cliente.getPasswordHash());
        cliente.setPasswordHash(passwordHash); // Establece la contraseña encriptada

        cliente.setFechaRegistro(LocalDateTime.now()); // Establece la fecha de registro al momento actual
        return clienteRepository.save(cliente);
    }

    // Método para buscar un cliente por su email
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /** Edita los datos del cliente (admin). No toca contraseña ni rol. */
    public Cliente editarCliente(Long id, com.wallpint.wallpint.dto.EditarClienteRequest req) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Si está cambiando el email, comprobamos que no esté en uso por otro
        if (!c.getEmail().equalsIgnoreCase(req.getEmail())
                && clienteRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "Ese email ya está en uso por otro cliente"
            );
        }

        c.setNombre(req.getNombre());
        c.setApellidos(req.getApellidos());
        c.setEmail(req.getEmail());
        c.setTelefono(req.getTelefono());
        c.setDireccion(req.getDireccion());
        return clienteRepository.save(c);
    }

    public Cliente registrarDesdeRequest(RegisterRequest req) {
        Cliente c = new Cliente();
        c.setNombre(req.getNombre());
        c.setApellidos(req.getApellidos());
        c.setEmail(req.getEmail());
        c.setTelefono(req.getTelefono());
        c.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        c.setDireccion(req.getDireccion());
        return clienteRepository.save(c);
    }
}
