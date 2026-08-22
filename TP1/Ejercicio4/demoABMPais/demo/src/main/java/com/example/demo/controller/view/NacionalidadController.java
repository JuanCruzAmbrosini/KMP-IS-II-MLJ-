package com.example.demo.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.business.domain.entity.Nacionalidad;
import com.example.demo.business.domain.entity.Pais;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.NacionalidadService;

/*
 * @Controller: Indica a Spring que esta clase manejará las solicitudes HTTP entrantes
 * y actuará como intermediario entre el cliente y la lógica de negocio.
 */
@Controller

/*
 * @RequestMapping: Define la ruta base para todas las solicitudes manejadas por este controlador.
 * En este caso, todas las rutas comenzarán con "/nacionalidad".
 */
@RequestMapping("/nacionalidad")
public class NacionalidadController {

	/*
	 * @Autowired: Indica que Spring debe inyectar automáticamente una instancia del servicio NacionalidadService en esta clase.
	 * Esto permite que la clase NacionalidadController pueda interactuar con la lógica de negocio relacionada con nacionalidades
	 * sin necesidad de crear manualmente una instancia de este servicio.
	 */
	@Autowired
   	private NacionalidadService nacionalidadService;

	/*
	 * Definición de las vistas utilizadas por el controlador.
	 * viewList: Vista para listar nacionalidades.
	 * redirectList: Redirección a la lista de nacionalidades después de ciertas acciones.
	 * viewEdit: Vista para editar o crear una nacionalidad.
	 */
	private String viewList="view/nacionalidad/lNacionalidad.html";
	private String redirectList= "redirect:/nacionalidad/listNacionalidad";
	private String viewEdit="view/nacionalidad/eNacionalidad.html";
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////// VIEW: lNacionalidad /////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////


	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/nacionalidad/listNacionalidad", se ejecutará el método listarNacionalidad.
	 * Este método obtiene la lista de nacionalidades activas y las agrega al modelo para ser mostradas en la vista correspondiente.
	 */
	@GetMapping("/listNacionalidad")
	public String listarNacionalidad(Model model) {
		try {
			  
		  List<Nacionalidad> listaNacionalidad = nacionalidadService.listarNacionalidadActivo();
		  model.addAttribute("listaNacionalidad", listaNacionalidad);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList; //"redirect:/nacionalidad/listNacionalidad"
	}

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/nacionalidad/altaNacionalidad", se ejecutará el método alta.
	 * Este método prepara el modelo para la creación de una nueva nacionalidad y establece el atributo "isDisabled" en false para permitir la edición
	 */
	@GetMapping("/altaNacionalidad")
	public String alta(Nacionalidad nacionalidad, Model model) {
		model.addAttribute("isDisabled", false);
		return viewEdit; //"view/nacionalidad/eNacionalidad.html"
	}
	
	@GetMapping("/consultar")
	public String consultar(@RequestParam(value="id") String idNacionalidad, Model model) {
		
		try {
			
		  Nacionalidad nacionalidad = nacionalidadService.buscarNacionalidad(idNacionalidad);		
		  model.addAttribute("nacionalidad", nacionalidad);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit; //"view/nacionalidad/eNacionalidad.html"
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList; //"redirect:/nacionalidad/listNacionalidad"
		}		  
	}

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/nacionalidad/modificar", se ejecutará el método modificar.
	 * Este método busca la nacionalidad correspondiente al ID proporcionado, la agrega al modelo y establece
	 * el atributo "isDisabled" en false para permitir la edición.
	 */
	@GetMapping("/modificar")
	public String modificar(@RequestParam(value="id") String idNacionalidad, Model model) {
		
		try {
			
		  Nacionalidad nacionalidad = nacionalidadService.buscarNacionalidad(idNacionalidad);		
		  model.addAttribute("nacionalidad", nacionalidad);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit; //"view/nacionalidad/eNacionalidad.html"
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList; //"redirect:/nacionalidad/listNacionalidad"
		}		  
	}

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/nacionalidad/baja", se ejecutará el método baja.
	 * Este método intenta eliminar la nacionalidad correspondiente al ID proporcionado y redirige a la lista
	 * de nacionalidades con un mensaje de éxito o error según corresponda.
	 */
	@GetMapping("/baja")
	public String baja(@RequestParam(value="id") String idNacionalidad, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  nacionalidadService.eliminarNacionalidad(idNacionalidad);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList; //"redirect:/nacionalidad/listNacionalidad"
		  
		}catch(ErrorServiceException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList; //"redirect:/nacionalidad/listNacionalidad"
		} 
	}
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	//////////// VIEW: eNacionalidad ////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////


	/*
	 * @PostMapping: Se utiliza para asignar solicitudes HTTP POST a métodos específicos de un controlador.
	 * En este caso, cuando se envía un formulario a la ruta "/nacionalidad/aceptarEditNacionalidad", se ejecutará el método aceptarEdit.
	 * Este método maneja tanto la creación como la modificación de una nacionalidad según si el ID proporcionado es nulo o no.
	 * Si el ID es nulo, se crea una nueva nacionalidad; si no, se modifica la existente. En ambos casos, se redirige a la lista de nacionalidades
	 * con un mensaje de éxito o error según corresponda.
	 */
	@PostMapping("/aceptarEditNacionalidad")
	public String aceptarEdit(@RequestParam(value="id") String idNacionalidad, @RequestParam(value="nombre") String nombreNacionalidad, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (idNacionalidad == null || idNacionalidad.trim().isEmpty())
		   nacionalidadService.crearNacionalidad(nombreNacionalidad);
		  else 
		   nacionalidadService.modificarNacionalidad(idNacionalidad, nombreNacionalidad);
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList; //"redirect:/nacionalidad/listNacionalidad"
		  
		}catch(ErrorServiceException e) {
			  return error (e.getMessage(), model, idNacionalidad, nombreNacionalidad);
		}catch(Exception e) {
			  return error ("Error de Sistema", model, idNacionalidad, nombreNacionalidad);
		}
		
	}


	/*
	 * Método privado para manejar errores y preparar el modelo para la vista de edición.
	 * Este método agrega un mensaje de error al modelo y, dependiendo de si se proporciona un ID,
	 * busca la nacionalidad correspondiente o crea una nueva instancia con el nombre proporcionado.
	 * Finalmente, devuelve la vista de edición.
	 */
	private String error (String mensaje, Model model, String id, String nombre) {
		try {
			
			model.addAttribute("msgError", mensaje);
			if (id != null && !id.trim().isEmpty()) {
			 model.addAttribute("nacionalidad", nacionalidadService.buscarNacionalidad(id));
			}else {
			  Nacionalidad nacionalidad = new Nacionalidad();
			  nacionalidad.setId("");
			  nacionalidad.setNombre(nombre);
			  model.addAttribute("nacionalidad",nacionalidad);	
			}
			
		}catch(Exception e) {}
		return viewEdit;  //"view/nacionalidad/eNacionalidad.html"
	}

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/nacionalidad/cancelarEditNacionalidad", se ejecutará el método cancelarEdit.
	 * Este método simplemente redirige a la lista de nacionalidades, permitiendo al usuario cancelar la operación de edición o creación.
	 */
	@GetMapping("/cancelarEditNacionalidad")
	public String cancelarEdit() {
		return redirectList; //"redirect:/nacionalidad/listNacionalidad"
	}
	

}
