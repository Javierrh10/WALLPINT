package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.dto.AuthResponse;
import com.wallpint.wallpint.dto.CambiarPasswordRequest;
import com.wallpint.wallpint.dto.EditarPerfilRequest;
import com.wallpint.wallpint.dto.LoginRequest;
import com.wallpint.wallpint.dto.RegisterRequest;
import com.wallpint.wallpint.dto.UsuarioDTO;
import com.wallpint.wallpint.model.Cliente;
import com.wallpint.wallpint.model.Pintor;
import com.wallpint.wallpint.model.Usuario;
import com.wallpint.wallpint.repository.UsuarioRepository;
import com.wallpint.wallpint.security.JwtUtil;
import com.wallpint.wallpint.service.ClienteService;
import com.wallpint.wallpint.service.PintorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Esta clase se encarga de manejar las solicitudes de autenticación y registro de usuarios.
 * Contiene rutas para que los clientes y pintores puedan registrarse, así como una ruta
 * para que ambos tipos de usuarios puedan iniciar sesión y obtener un token JWT para autenticarse
 * en futuras solicitudes a la API.
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PintorService pintorService;

    // ================== RUTA DE LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(req.getEmail());
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
                String token = jwtUtil.generateToken(u.getEmail(), u.getRol().name());
                return ResponseEntity.ok(new AuthResponse(token, u.getRol().name(), u.getNombre(), u.getId()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    // ================== RUTAS DE REGISTRO ==================

    @PostMapping("/registro/cliente")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("No se ha podido completar el registro");
        }
        clienteService.registrarDesdeRequest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cliente registrado");
    }

    @PostMapping("/registro/pintor")
    public ResponseEntity<?> registrarPintor(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("No se ha podido completar el registro");
        }
        pintorService.registrarDesdeRequest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pintor registrado");
    }

    // ================== RUTA DE PERFIL ==================
    @GetMapping("/me")
    public ResponseEntity<?> obtenerMiPerfil(Authentication authentication) {
        // Spring Security inyecta automáticamente el 'authentication' gracias al Token JWT
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        // authentication.getName() saca el email que guardaste dentro del JWT al hacer login
        String email = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Empaquetamos los datos en el DTO (¡Ojo! Asumo que tu clase Usuario tiene getNombre() y getApellidos())
            UsuarioDTO dto = new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    usuario.getEmail(),
                    usuario.getPasswordHash(),
                    usuario.getTelefono(),
                    usuario.getRol().name()
            );
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    // ================== EDITAR PERFIL ==================
    @PutMapping("/me")
    public ResponseEntity<?> editarPerfil(
            @Valid @RequestBody EditarPerfilRequest req,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        Usuario u = usuarioOpt.get();
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setTelefono(req.getTelefono());
        usuarioRepository.save(u);

        UsuarioDTO dto = new UsuarioDTO(
                u.getId(), u.getNombre(), u.getApellidos(),
                u.getEmail(), u.getPasswordHash(), u.getTelefono(), u.getRol().name()
        );
        return ResponseEntity.ok(dto);
    }

    // ================== CAMBIAR CONTRASEÑA ==================
    @PutMapping("/me/password")
    public ResponseEntity<?> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest req,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        Usuario u = usuarioOpt.get();
        if (!passwordEncoder.matches(req.getPasswordActual(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña actual no es correcta");
        }
        u.setPasswordHash(passwordEncoder.encode(req.getPasswordNueva()));
        usuarioRepository.save(u);
        return ResponseEntity.ok().body("Contraseña actualizada");
    }
}
