package com.wallpint.wallpint.config;

import com.wallpint.wallpint.model.Rol;
import com.wallpint.wallpint.model.Usuario;
import com.wallpint.wallpint.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UsuarioRepository r, PasswordEncoder p) {
        this.usuarioRepository = r;
        this.passwordEncoder = p;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@wallpint.com";
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellidos("WallPint");
            admin.setEmail(adminEmail);
            admin.setTelefono("000000000");
            admin.setPasswordHash(passwordEncoder.encode("1234"));
            admin.setActivo(true);
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Admin creado: " + adminEmail + " / 1234");
        }
    }
}