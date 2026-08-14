package org.example.entities;

import jakarta.persistence.*;

@Entity
public class Domicilio {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String calle;
//    private String ciudad;
//
//    public Domicilio() {}
//
//    public Domicilio(Long id, String calle, String ciudad) {
//        this.id = id;
//        this.calle = calle;
//        this.ciudad = ciudad;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getCiudad() {
//        return ciudad;
//    }
//
//    public void setCiudad(String ciudad) {
//        this.ciudad = ciudad;
//    }
//
//    public String getCalle() {
//        return calle;
//    }
//
//    public void setCalle(String calle) {
//        this.calle = calle;
//    }
//
//    @Override
//    public String toString() {
//        return "Domicilio{" +
//                "id=" + id +
//                ", calle='" + calle + '\'' +
//                ", ciudad='" + ciudad + '\'' +
//                '}';
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String calle;
    private String ciudad;

    public Domicilio() {}
    public Domicilio(String calle, String ciudad) {
        this.calle = calle;
        this.ciudad = ciudad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Domicilio{" +
                "id=" + id +
                ", calle='" + calle + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
