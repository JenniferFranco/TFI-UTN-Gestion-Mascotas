package entities;

import java.util.ArrayList;
import java.util.List;

public class Duenio {
    // Atributos
    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean eliminado;
     // Atributos de RELACIÓN
    private List<Mascota> mascotas = new ArrayList <>();; // Relación 1-a-Muchos (lado "Uno")
    
     //Constructor vacio
    public Duenio() {
    }
    
    //Constructor con parametros
    public Duenio(Long id, String dni, String nombre, String apellido, String email, String telefono, String direccion, Boolean eliminado, ArrayList<Mascota> mascotas) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.eliminado = eliminado;
        this.mascotas = mascotas;
    }
    
    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }
    
    //To String

    @Override
    public String toString() {
        return "Duenio{" + "id=" + id + 
                ", dni=" + dni + 
                ", nombre=" + nombre + 
                ", apellido=" + apellido + 
                ", email=" + email +
                ", direccion=" + direccion + 
                ", eliminado=" + eliminado + 
                ", mascotas=" + mascotas + '}';     
    }
    
}
