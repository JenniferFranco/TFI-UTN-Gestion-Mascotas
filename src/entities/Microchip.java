package entities;

import java.time.LocalDate;

public class Microchip {
    // Atributos 
    private Long id;
    private Boolean eliminado; 
    private String codigo;
    private LocalDate fechaImplantacion;
    private String veterinaria; 
    private String observaciones;  

    //Constructor vacio
    public Microchip() {
    }
    
     //Constructor con parametros
    public Microchip(Long id, Boolean eliminado, String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones) {
        this.id = id;
        this.eliminado = eliminado;
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
    }
    
    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaImplantacion() {
        return fechaImplantacion;
    }

    public void setFechaImplantacion(LocalDate fechaImplantacion) {
        this.fechaImplantacion = fechaImplantacion;
    }

    public String getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(String veterinaria) {
        this.veterinaria = veterinaria;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    //To String

    @Override
    public String toString() {
        return "Microchip{" + "id=" + id + 
                ", eliminado=" + eliminado + 
                ", codigo=" + codigo +
                ", fechaImplantacion=" + fechaImplantacion + 
                ", veterinaria=" + veterinaria + 
                ", observaciones=" + observaciones + '}';
        
    }
    
    
}
