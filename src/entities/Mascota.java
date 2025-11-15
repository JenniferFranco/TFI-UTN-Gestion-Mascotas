package entities;

import java.time.LocalDate;

public class Mascota {
    // Atributos 
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento; 
    private Boolean eliminado;
    // Atributos de RELACIÓN
    private Duenio duenio; // Relación 1-a-Muchos (lado "Muchos")
    private Microchip microchip; // Relación 1-a-1 (lado "A")

    //Constructor vacio
    public Mascota() {
    }
    
    //Constructor con parametros
    public Mascota(Long id, String nombre, String especie, String raza, LocalDate fechaNacimiento, Boolean eliminado, Duenio duenio, Microchip microchip) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.eliminado = eliminado;
        this.duenio = duenio;
        this.microchip = microchip;
    }

    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Duenio getDuenio() {
        return duenio;
    }

    public void setDuenio(Duenio duenio) {
        this.duenio = duenio;
    }

    public Microchip getMicrochip() {
        return microchip;
    }

    public void setMicrochip(Microchip microchip) {
        this.microchip = microchip;
    }
    
    //To String
    @Override
    public String toString() {
        return "Mascota{" + "id=" + id + 
                ", nombre=" + nombre + 
                ", especie=" + especie 
                + ", raza=" + raza + 
                ", fechaNacimiento=" + fechaNacimiento + 
                ", eliminado=" + eliminado + 
                ", duenio=" + duenio +
                ", microchip=" + microchip + '}';
    }
    
}
