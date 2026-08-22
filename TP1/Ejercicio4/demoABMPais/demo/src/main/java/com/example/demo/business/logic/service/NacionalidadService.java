package com.example.demo.business.logic.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.business.domain.entity.Nacionalidad;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persitence.repository.NacionalidadRepository;

import jakarta.persistence.NoResultException;

/* @Service: Indica que esta clase es un componente de servicio de Spring,
 * lo que permite la inyección de dependencias y otras funcionalidades proporcionadas
 * por el contenedor de Spring.
 */
@Service

public class NacionalidadService {

    /*
     * @Autowired: Indica que Spring debe inyectar automáticamente una instancia del repositorio NacionalidadRepository en esta clase.
     * Esto permite que la clase NacionalidadService pueda interactuar con la base de datos a través del repositorio sin necesidad
     * de crear manualmente una instancia de este.
     */
	@Autowired

    /*
     * El método validar se encarga de verificar que el nombre de la nacionalidad no sea nulo ni vacío.
     */
	private NacionalidadRepository repository; 
    
    public void validar(String nombre)throws ErrorServiceException {
        
        try{
            
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    /* @Transactional: Indica que el método debe ejecutarse dentro de una transacción.
     * Esto significa que todas las operaciones de base de datos dentro del método
     * serán tratadas como una única unidad de trabajo, y pueden ser confirmadas (commit) o revertidas (rollback) juntas.
     * El método crearNacionalidad asegura que la creación de una nacionalidad se realice de manera atómica, recibe un nombre como parámetro,
     * lo valida y lanza una excepción personalizada ErrorServiceException en caso de errores.
     */
	@Transactional
    public void crearNacionalidad(String nombre) throws ErrorServiceException {

        try {
            
            validar(nombre);

            try {
            	Nacionalidad nacionalidad = repository.buscarNacionalidadPorNombre(nombre);
            	if (nacionalidad != null && !nacionalidad.isEliminado()) {
                 throw new ErrorServiceException("Existe una nacionalidad con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Nacionalidad nacionalidad = new Nacionalidad();
            nacionalidad.setId(UUID.randomUUID().toString());
            nacionalidad.setNombre(nombre);
            nacionalidad.setEliminado(false);

            repository.save(nacionalidad);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }


    /* @Transactional: Indica que el método debe ejecutarse dentro de una transacción.
     * Esto significa que todas las operaciones de base de datos dentro del método
     * serán tratadas como una única unidad de trabajo, y pueden ser confirmadas (commit) o revertidas (rollback) juntas.
     * El método modificarNacionalidad asegura que la modificación de una nacionalidad se realice de manera atómica, recibe un id y un nombre como parámetros,
     * lo valida y lanza una excepción personalizada ErrorServiceException en caso de errores.
     */
	@Transactional
    public void modificarNacionalidad(String idNacionalidad, String nombre) throws ErrorServiceException {

        try {

            Nacionalidad nacionalidad = buscarNacionalidad(idNacionalidad);

            validar(nombre);

            try{
                Nacionalidad NacionalidadExsitente = repository.buscarNacionalidadPorNombre(nombre);
                if (NacionalidadExsitente != null && !NacionalidadExsitente.getId().equals(idNacionalidad) && !NacionalidadExsitente.isEliminado()){
                  throw new ErrorServiceException("Existe una nacionalidad con el nombre indicado");  
                }
            } catch (NoResultException ex) {}

            nacionalidad.setNombre(nombre);
            nacionalidad.setEliminado(false);
            
            repository.save(nacionalidad);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    /* El método findById heredado de JpaRepository para buscar la entidad por su ID.
     * Este método devuelve un Optional<E>, que puede contener la entidad si se encuentra,
     * o estar vacío si no se encuentra.
     * ¿Qué es Optional?
     * Un Optional<E> es un contenedor que puede o no contener un valor no nulo de tipo E.
     * Los Optional se utilizan para evitar NullPointerException
     * y para expresar la ausencia de un valor de manera más clara.
     */
	public Nacionalidad buscarNacionalidad(String id) throws ErrorServiceException {

        try {
            
            if (id == null || id.isEmpty()) {
                throw new ErrorServiceException("Debe indicar la nacionalidad");
            }

            Optional<Nacionalidad> optional = repository.findById(id);
            Nacionalidad nacionalidad = null;
            if (optional.isPresent()) {
            	nacionalidad= optional.get();
    			if (nacionalidad.isEliminado()){
                    throw new ErrorServiceException("No se encuentra la nacionalidad indicada");
                }
    		}
            
            return nacionalidad;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    /* @Transactional: Indica que el método debe ejecutarse dentro de una transacción.
     * Esto significa que todas las operaciones de base de datos dentro del método
     * serán tratadas como una única unidad de trabajo, y pueden ser confirmadas (commit) o revertidas (rollback) juntas.
     * En el método eliminarNacionalidad, @Transactional asegura que la operación
     * de marcar la entidad como eliminada y guardarla nuevamente ocurra dentro de una transacción.
     * Esto garantiza que ambos pasos se ejecuten correctamente o ninguno se ejecute en caso de un error.
     */
    @Transactional
    public void eliminarNacionalidad(String id) throws ErrorServiceException {

        try {

            Nacionalidad nacionalidad = buscarNacionalidad(id);
            nacionalidad.setEliminado(true);
            
            repository.save(nacionalidad);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    /* findAll(): Este método de JpaRepository
     * se utiliza para obtener todas las entidades del tipo E desde la base de datos.
     * Devuelve una lista (List<E>) de todas las entidades.
     */
    public Collection<Nacionalidad> listarNacionalidad() throws ErrorServiceException {
        try {
            
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
    
    /* listarNacionalidadActivo(): Este método personalizado de JpaRepository
     * se utiliza para obtener todas las nacionalidades activas desde la base de datos.
     * Devuelve una lista (List<Nacionalidad>) de todas las nacionalidades activas.
     */
    public List<Nacionalidad> listarNacionalidadActivo() throws ErrorServiceException {
        try {
            
            return repository.listarNacionalidadActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
}
