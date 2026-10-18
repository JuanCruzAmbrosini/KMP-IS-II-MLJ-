package com.ecommerce.cart.model;

import java.io.Serializable;

/**
 * Representa un proveedor del sistema con su información comercial
 * y su ubicación geográfica (dirección, latitud y longitud) para OpenStreetMap.
 */
public class Supplier implements Serializable {
    private Long id;
    private String name;
    private String contactEmail;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private String category;
    private String city;

    public Supplier() {
    }

    public Supplier(Long id, String name, String contactEmail, String phone, 
                    String address, Double latitude, Double longitude, 
                    String category, String city) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
