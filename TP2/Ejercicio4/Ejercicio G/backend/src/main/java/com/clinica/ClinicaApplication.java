package com.clinica;

import com.clinica.model.Medico;
import com.clinica.model.Usuario;
import com.clinica.repository.MedicoRepository;
import com.clinica.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClinicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicaApplication.class, args);
    }

    /**
     * Semilla inicial de datos para pruebas inmediatas de Médicos y Usuario operador
     */
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepo, MedicoRepository medicoRepo) {
        return args -> {
            if (usuarioRepo.count() == 0) {
                usuarioRepo.save(new Usuario("admin_clinica", "SecretPass2026!"));
            }
            if (medicoRepo.count() == 0) {
                medicoRepo.save(new Medico("Gregory", "House", "MED-1001"));
                medicoRepo.save(new Medico("James", "Wilson", "MED-1002"));
                medicoRepo.save(new Medico("Lisa", "Cuddy", "MED-1003"));
            }
        };
    }
}
