package com.example.demo.controller.view;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class GenericCRUDWebController <T extends Object>{

	//Atributos
   	private String nameClass;
   	protected boolean campoDesactivado;
    protected Object object;
    
    //Vistas de rotorno para navegabilidad
	private String viewList;
	private String redirectList;
	private String viewEdit; 
   	
	//Constructor
    public GenericCRUDWebController(T object){
    	nameClass= getNameObject(object);
    	viewList= "view/l"+ nameClass +".html";
    	redirectList= "redirect:/list"+ nameClass;
    	viewEdit= "view/e"+ nameClass +".html";
    }

	//Métodos

	/* Método para obtener el nombre de la clase del objeto genérico.
	 *  El método retorna el nombre simple de la clase del objeto genérico T, que se utiliza para construir las rutas de las vistas y redirecciones.
	 */
	private String getNameObject(T object){
        return ((((T) object).getClass()).getSimpleName());
    }

	//Método para obtener el nombre de la clase del objeto genérico.
    private String getNameClass() {
        return nameClass;
    }

	/*Método para obtener el valor del campo "id" de un objeto genérico.
	 * El método utiliza reflexión para acceder al campo "id" del objeto genérico T.
	 * Si el campo no se encuentra en la clase del objeto, se busca en la superclase. El valor del campo "id" se devuelve como una cadena.
	 */
    private String getValueIdFieldObject(T object) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	Field field = null;
        try {
            field = object.getClass().getDeclaredField("id");
        } catch (Exception e) {
            field = object.getClass().getSuperclass().getDeclaredField("id");
        }
        field.setAccessible(true);
        String id= (String) field.get(object);
        return id;
    }
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////////// VIEW: Lista ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/list", se ejecutará el método listTemplateMethod.
	 * Este método obtiene la lista de objetos genéricos y las agrega al modelo para ser mostradas en la vista correspondiente.
	 */
	@GetMapping("/list")
	public String listTemplateMethod(Model model) {
		try {
			  
		  List<T> list = listObject();
		  model.addAttribute("list"+ getNameClass(), list);
 
		}catch(Exception e) {
		  model.addAttribute("msgError", e.getMessage());  
		}
		return viewList; //"view/l"+ nameClass +".html"
	}

	//Método abstracto que debe ser implementado por las subclases para obtener la lista de objetos genéricos.
	protected abstract List<T> listObject();
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	////////////// VIEW: NAVEGACION /////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/edit", se ejecutará el método browsePageEdit.
	 * Este método prepara el modelo para la edición de un objeto genérico y establece el atributo "isDisabled" en false para permitir la edición.
	 */
	@GetMapping("/edit")
	public String browsePageEdit(T object, Model model) {
		model.addAttribute("isDisabled", false);
		return viewEdit; //"view/e"+ nameClass +".html"
	}

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/consultar/{id}", se ejecutará el método editTemplateMethod.
	 * Este método busca el objeto genérico correspondiente al ID proporcionado, lo agrega al modelo y establece
	 * el atributo "isDisabled" en true para deshabilitar la edición.
	 *
	 * @PathVariable: Se utiliza para vincular un valor de la ruta de la URL a un parámetro del método.
	 * En este caso, el valor del segmento {id} de la URL se vincula al parámetro "id" del método editTemplateMethod.
	 */
	@GetMapping("")
	public String editTemplateMethod(@PathVariable("id") String id, Model model) {
		
		try {
			
		  T object = getObjectById(id);		
		  model.addAttribute("object"+ nameClass, object);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit; //"view/e"+ nameClass +".html"
		 
		}catch(Exception e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList; //"redirect:/list"+ nameClass
		}		  
	}

	//Método abstracto que debe ser implementado por las subclases para obtener un objeto genérico por su ID.
	protected abstract T getObjectById(String id);

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/modificar/{id}", se ejecutará el método browsePageEditTemplateMethod.
	 * Este método busca el objeto genérico correspondiente al ID proporcionado, lo agrega al modelo y establece
	 * el atributo "isDisabled" en false para permitir la edición.
	 *
	 * @PathVariable: Se utiliza para vincular un valor de la ruta de la URL a un parámetro del método.
	 * En este caso, el valor del segmento {id} de la URL se vincula al parámetro "id" del método browsePageEditTemplateMethod.
	 */
	@GetMapping("")
	public String browsePageEditTemplateMethod(@PathVariable("id") String id, Model model) {
		
		try {
			
		  T object = getObjectById(id);		
	      model.addAttribute("object"+ nameClass, object);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit; //"view/e"+ nameClass +".html"
		 
		}catch(Exception e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList; //"redirect:/list"+ nameClass
		}		  
	}

	/*
	 *  @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 *  En este caso, cuando se accede a la ruta "/eliminar/{id}", se ejecutará el método eliminateTemplateMethod.
	 *  Este método elimina el objeto genérico correspondiente al ID proporcionado y redirige
	 *  a la lista de objetos genéricos. En caso de error, se agrega un mensaje de error al modelo y se redirige a la lista.
	 *
	 * @PathVariable: Se utiliza para vincular un valor de la ruta de la URL a un parámetro del método.
	 * En este caso, el valor del segmento {id} de la URL se vincula al parámetro "id" del método eliminateTemplateMethod.
	 */
	@GetMapping("")
	public String eliminateTemplateMethod(@PathVariable("id") String id, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  eliminate(id);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList; //"redirect:/list"+ nameClass
		  
		}catch(Exception e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList; //"redirect:/list"+ nameClass
		} 
	}

	//Método abstracto que debe ser implementado por las subclases para eliminar un objeto genérico por su ID.
	protected abstract void eliminate(String id);
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////////// VIEW: Edit ////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	/*
	 * @PostMapping: Se utiliza para asignar solicitudes HTTP POST a métodos específicos de un controlador.
	 * En este caso, cuando se envía un formulario de edición, se ejecutará el método acceptEditTemplateMethod.
	 * Este método valida los datos del objeto genérico, ejecuta el caso de uso correspondiente y redirige a la lista de objetos genéricos.
	 * En caso de error, se agrega un mensaje de error al modelo y se retorna a la vista de edición.
	 */
	@PostMapping("")
	public String acceptEditTemplateMethod(T object, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit; //"view/e"+ nameClass +".html"
		  }
		 
		  executeUseCase(object);
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList; //"redirect:/list"+ nameClass
		  
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
			  return viewEdit; //"view/e"+ nameClass +".html"
		}
		
	}

	//Método abstracto que debe ser implementado por las subclases para ejecutar el caso de uso correspondiente al objeto genérico.
	protected abstract void executeUseCase(T object);

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, cuando se accede a la ruta "/cancelEdit", se ejecutará el método cancelEdit.
	 * Este método redirige a la lista de objetos genéricos.
	 */
	@GetMapping("/cancelEdit")
	public String cancelEdit() {
		return redirectList; //"redirect:/nacionalidad/listNacionalidad"
	}
	

}
