package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.services.ServicioCategoria;
import com.colmena.videojuegos.services.ServicioEstudio;
import com.colmena.videojuegos.services.ServicioVideojuego;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
@Validated
public class controladorVideojuego {
    private final ServicioVideojuego svcVideojuego;
    private final ServicioCategoria svcCategoria;
    private final ServicioEstudio svcEstudio;
    private final Path directorioUpload;

    public controladorVideojuego(
            ServicioVideojuego svcVideojuego,
            ServicioCategoria svcCategoria,
            ServicioEstudio svcEstudio,
            @Value("${app.upload-dir}") String uploadDir) {
        this.svcVideojuego = svcVideojuego;
        this.svcCategoria = svcCategoria;
        this.svcEstudio = svcEstudio;
        this.directorioUpload = Paths.get(uploadDir).toAbsolutePath().normalize();
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
            model.addAttribute("videojuego",videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/busqueda")
    public String busquedaVideojuego(Model model, @RequestParam(value ="query",required = false)String q){
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findByTitle(q);
            model.addAttribute("videojuegos", videojuegos);
            model.addAttribute("resultado",q);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crud")
    public String crudVideojuego(Model model){
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findAll();
            model.addAttribute("videojuegos",videojuegos);
            return "views/crud";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model,@PathVariable("id")long id){
        try {
            model.addAttribute("categorias",this.svcCategoria.findAll());
            model.addAttribute("estudios",this.svcEstudio.findAll());
            if(id==0){
                model.addAttribute("videojuego",new Videojuego());
            }else{
                model.addAttribute("videojuego",this.svcVideojuego.findById(id));
            }
            return "views/formulario/videojuego";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @Valid @ModelAttribute("videojuego") Videojuego videojuego,
            BindingResult result,
            Model model,
            @PathVariable("id") long id
    ) {
        try {
            model.addAttribute("categorias", this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());

            if (videojuego == null) {
                model.addAttribute("error", "Datos del videojuego inválidos.");
                return "error";
            }
            if (videojuego.getTitulo() == null || videojuego.getTitulo().isBlank()) {
                result.rejectValue("titulo", "required", "El título es obligatorio.");
            }
            if (videojuego.getDescripcion() == null || videojuego.getDescripcion().isBlank()) {
                result.rejectValue("descripcion", "required", "La descripción es obligatoria.");
            }
            if (videojuego.getFechaLanzamiento() == null) {
                result.rejectValue("fechaLanzamiento", "required", "La fecha es obligatoria.");
            }
            if (videojuego.getCategoria() == null || videojuego.getCategoria().getId() <= 0) {
                result.rejectValue("categoria", "required", "Debe seleccionar una categoría válida.");
            }
            if (videojuego.getEstudio() == null || videojuego.getEstudio().getId() <= 0) {
                result.rejectValue("estudio", "required", "Debe seleccionar un estudio válido.");
            }
            if (videojuego.getPrecio() < 5 || videojuego.getPrecio() > 10000) {
                result.rejectValue("precio", "range", "El precio debe estar entre 5 y 10000.");
            }
            if (videojuego.getStock() < 1 || videojuego.getStock() > 10000) {
                result.rejectValue("stock", "range", "El stock debe estar entre 1 y 10000.");
            }

            if (result.hasErrors()) {
                return "views/formulario/videojuego";
            }

            Files.createDirectories(this.directorioUpload);

            if (id == 0) {
                if (archivo == null || archivo.isEmpty()) {
                    model.addAttribute("errorImagenMsg", "La imagen es requerida");
                    return "views/formulario/videojuego";
                }
                if (!this.validarExtension(archivo)) {
                    model.addAttribute("errorImagenMsg", "La extension no es valida");
                    return "views/formulario/videojuego";
                }
                if (archivo.getSize() >= 15_000_000) {
                    model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                    return "views/formulario/videojuego";
                }

                String extension = this.obtenerExtension(archivo.getOriginalFilename());
                String nombreFoto = this.generarNombreSeguro(archivo.getOriginalFilename(), extension);
                Path rutaAbsoluta = this.directorioUpload.resolve(nombreFoto).normalize();
                if (!rutaAbsoluta.startsWith(this.directorioUpload)) {
                    throw new SecurityException("Nombre de archivo no válido.");
                }
                Files.write(rutaAbsoluta, archivo.getBytes());
                videojuego.setImagen(nombreFoto);
                this.svcVideojuego.saveOne(videojuego);
            } else {
                if (archivo != null && !archivo.isEmpty()) {
                    if (!this.validarExtension(archivo)) {
                        model.addAttribute("errorImagenMsg", "La extension no es valida");
                        return "views/formulario/videojuego";
                    }
                    if (archivo.getSize() >= 15_000_000) {
                        model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                        return "views/formulario/videojuego";
                    }

                    String extension = this.obtenerExtension(archivo.getOriginalFilename());
                    String nombreFoto = this.generarNombreSeguro(archivo.getOriginalFilename(), extension);
                    Path rutaAbsoluta = this.directorioUpload.resolve(nombreFoto).normalize();
                    if (!rutaAbsoluta.startsWith(this.directorioUpload)) {
                        throw new SecurityException("Nombre de archivo no válido.");
                    }
                    Files.write(rutaAbsoluta, archivo.getBytes());
                    videojuego.setImagen(nombreFoto);
                }
                this.svcVideojuego.updateOne(videojuego, id);
            }
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo procesar la operación solicitada.");
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model,@PathVariable("id")long id){
        try {
            model.addAttribute("videojuego",this.svcVideojuego.findById(id));
            return "views/formulario/eliminar";
        }catch(Exception e){
            model.addAttribute("error", "No se pudo cargar la operación solicitada.");
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Identificador inválido.");
            }
            this.svcVideojuego.deleteById(id);
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo completar la eliminación solicitada.");
            return "error";
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".png";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.')).toLowerCase();
    }

    private String generarNombreSeguro(String nombreOriginal, String extension) {
        String nombreBase = nombreOriginal == null ? "upload" : nombreOriginal;
        nombreBase = nombreBase.replaceAll("[^a-zA-Z0-9._-]", "_");
        nombreBase = nombreBase.replace("..", ".");
        return Calendar.getInstance().getTimeInMillis() + "_" + nombreBase.substring(0, Math.min(nombreBase.length(), 40)) + extension;
    }

    public boolean validarExtension(MultipartFile archivo) {
        try {
            if (archivo == null || archivo.isEmpty()) {
                return false;
            }
            return archivo.getContentType() != null && archivo.getContentType().startsWith("image/")
                    && archivo.getInputStream() != null
                    && ImageIO.read(archivo.getInputStream()) != null;
        } catch (Exception e) {
            return false;
        }
    }
}