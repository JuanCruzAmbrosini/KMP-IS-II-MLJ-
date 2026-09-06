package com.colmena.videojuegos;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final RepositorioCategoria repositorioCategoria;
    private final RepositorioEstudio repositorioEstudio;
    private final RepositorioVideojuego repositorioVideojuego;

    public DatabaseSeeder(
            RepositorioCategoria repositorioCategoria,
            RepositorioEstudio repositorioEstudio,
            RepositorioVideojuego repositorioVideojuego
    ) {
        this.repositorioCategoria = repositorioCategoria;
        this.repositorioEstudio = repositorioEstudio;
        this.repositorioVideojuego = repositorioVideojuego;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repositorioCategoria.count() == 0) {
            Categoria categoria = new Categoria();
            categoria.setNombre("Acción");
            categoria.setActivo(true);
            repositorioCategoria.save(categoria);
        }

        if (repositorioEstudio.count() == 0) {
            Estudio estudio = new Estudio();
            estudio.setNombre("Nintendo");
            estudio.setActivo(true);
            repositorioEstudio.save(estudio);
        }

        if (repositorioVideojuego.count() == 0) {
            Categoria categoria = repositorioCategoria.findAll().get(0);
            Estudio estudio = repositorioEstudio.findAll().get(0);

            Videojuego videojuego = new Videojuego();
            videojuego.setTitulo("Super Mario Demo");
            videojuego.setDescripcion("Juego de ejemplo cargado al iniciar la app");
            videojuego.setImagen("demo.jpg");
            videojuego.setPrecio(2999f);
            videojuego.setStock((short) 10);
            videojuego.setFechaLanzamiento(new Date());
            videojuego.setActivo(true);
            videojuego.setCategoria(categoria);
            videojuego.setEstudio(estudio);

            repositorioVideojuego.save(videojuego);
        }
    }
}
