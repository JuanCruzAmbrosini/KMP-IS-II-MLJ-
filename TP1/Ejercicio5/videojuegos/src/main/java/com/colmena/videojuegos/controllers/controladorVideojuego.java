package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.services.ServicioCategoria;
import com.colmena.videojuegos.services.ServicioEstudio;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
public class controladorVideojuego {
    @Autowired
    private ServicioVideojuego svcVideojuego;
    @Autowired
    private ServicioCategoria svcCategoria;
    @Autowired
    private ServicioEstudio svcEstudio;

    private Path getUploadDirectory() {
        String uploadDir = System.getProperty("os.name").toLowerCase().contains("win")
                ? "C:/Videojuegos/imagenes"
                : System.getProperty("user.home") + "/Videojuegos/imagenes";
        Path path = Paths.get(uploadDir);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.err.println("Error creando directorio de imagenes: " + e.getMessage());
        }
        return path;
    }

    @GetMapping("/inicio")
    public String inicio(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findAllByActivo();
            model.addAttribute("videojuegos", videojuegos);

            return "views/inicio";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id") long id) {
        try {
            Videojuego videojuego = this.svcVideojuego.findByIdAndActivo(id);
            model.addAttribute("videojuego", videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/busqueda")
    public String busquedaVideojuego(Model model, @RequestParam(value = "query", required = false) String q) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findByTitle(q);
            model.addAttribute("videojuegos", videojuegos);
            model.addAttribute("resultado", q);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crud")
    public String crudVideojuego(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findAll();
            model.addAttribute("videojuegos", videojuegos);
            return "views/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());
            if (id == 0) {
                model.addAttribute("videojuego", new Videojuego());
            } else {
                model.addAttribute("videojuego", this.svcVideojuego.findById(id));
            }
            return "views/formulario/videojuego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @Valid @ModelAttribute("videojuego") Videojuego videojuego,
            BindingResult result,
            Model model, @PathVariable("id") long id
    ) {
        try {
            model.addAttribute("categorias", this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());
            if (result.hasErrors()) {
                return "views/formulario/videojuego";
            }

            Path uploadPath = this.getUploadDirectory();

            if (id == 0) {
                // Nuevo videojuego
                if (archivo == null || archivo.isEmpty()) {
                    model.addAttribute("errorImagenMsg", "La imagen es requerida");
                    return "views/formulario/videojuego";
                }
                if (!this.validarExtension(archivo)) {
                    model.addAttribute("errorImagenMsg", "La extension no es valida");
                    return "views/formulario/videojuego";
                }
                if (archivo.getSize() >= 15000000) {
                    model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                    return "views/formulario/videojuego";
                }

                String originalName = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "imagen.png";
                int index = originalName.lastIndexOf(".");
                String extension = (index != -1) ? originalName.substring(index) : ".png";
                String nombreFoto = Calendar.getInstance().getTimeInMillis() + extension;
                Path rutaAbsoluta = uploadPath.resolve(nombreFoto);

                Files.write(rutaAbsoluta, archivo.getBytes());
                videojuego.setImagen(nombreFoto);
                this.svcVideojuego.saveOne(videojuego);
            } else {
                // Edición de videojuego existente
                Videojuego existente = this.svcVideojuego.findById(id);
                if (archivo != null && !archivo.isEmpty()) {
                    if (!this.validarExtension(archivo)) {
                        model.addAttribute("errorImagenMsg", "La extension no es valida");
                        return "views/formulario/videojuego";
                    }
                    if (archivo.getSize() >= 15000000) {
                        model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                        return "views/formulario/videojuego";
                    }

                    String originalName = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "imagen.png";
                    int index = originalName.lastIndexOf(".");
                    String extension = (index != -1) ? originalName.substring(index) : ".png";
                    String nombreFoto = Calendar.getInstance().getTimeInMillis() + extension;
                    Path rutaAbsoluta = uploadPath.resolve(nombreFoto);

                    Files.write(rutaAbsoluta, archivo.getBytes());
                    videojuego.setImagen(nombreFoto);
                } else {
                    // Mantener la imagen previa si no se subió una nueva
                    if (existente != null && (videojuego.getImagen() == null || videojuego.getImagen().isEmpty())) {
                        videojuego.setImagen(existente.getImagen());
                    }
                }
                this.svcVideojuego.updateOne(videojuego, id);
            }
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("videojuego", this.svcVideojuego.findById(id));
            return "views/formulario/eliminar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            this.svcVideojuego.deleteById(id);
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    public boolean validarExtension(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(archivo.getInputStream());
            return img != null;
        } catch (Exception e) {
            return false;
        }
    }
}
