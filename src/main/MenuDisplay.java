package main;

import entities.Duenio;
import entities.Mascota;
import java.util.List;

/**
 * Componente de VISTA (View) de la capa de presentación.
 *
 * RESPONSABILIDADES:
 * 1.  **Visualización:** Encargada exclusivamente de mostrar datos y menús en la consola.
 * 2.  **Formato:** Define la estructura visual de los mensajes (éxito, error, listados).
 * 3.  **Desacoplamiento:** No contiene lógica de negocio ni manejo de entrada (Scanner).
 */
public class MenuDisplay {
     /**
     * Despliega las opciones del menú principal en la consola.
     * Los índices corresponden a la lógica implementada en {@link MenuHandler}.
     */
    public void mostrarMenuPrincipal() {
        System.out.println("\n=======================================");
        System.out.println("      SISTEMA DE GESTIÓN VETERINARIA");
        System.out.println("=======================================");
        
        System.out.println("\n--- Gestión de Dueños ---");
        System.out.println(" 1. Crear Dueño");
        System.out.println(" 2. Listar Todos los Dueños");
        System.out.println(" 3. Buscar Dueño por DNI");
        System.out.println(" 4. Buscar Dueño por Email");
        System.out.println(" 5. Buscar Dueños por Apellido");
        System.out.println(" 6. Actualizar Datos de Dueño");
        System.out.println(" 7. Eliminar Dueño (Baja Lógica)");
        
        System.out.println("\n--- Gestión de Mascotas ---");
        System.out.println(" 8. Registrar Mascota y Microchip");
        System.out.println(" 9. Listar Todas las Mascotas");
        System.out.println(" 10. Listar Mascotas por Dueño");
        System.out.println(" 11. Eliminar Mascota (Baja en Cascada)");
        
        System.out.println("--------------------------------------------------------------");
        System.out.println(" 0. Salir del Sistema");
        System.out.println("=======================================");
        System.out.print(">> Ingrese una opción: ");
    }

    /**
     * Muestra un mensaje de error con formato de alerta.
     * @param mensaje El detalle del error ocurrido.
     */
    public void mostrarError(String mensaje) {
        System.err.println("\n/--- ¡ERROR! --- ");
        System.err.println(mensaje);
    }

   /**
     * Muestra un mensaje de operación exitosa.
     * @param mensaje El detalle de la operación realizada.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n--- ¡ÉXITO! ---");
        System.out.println(mensaje);
        System.out.println("----------------\n");
    }

    /**
     * Renderiza una lista de objetos {@link Duenio} en formato tabular simple.
     * @param duenios La lista de dueños a mostrar.
     */
    public void mostrarDuenios(List<Duenio> duenios) {
        if (duenios.isEmpty()) {
            System.out.println("No se encontraron dueños registrados.");
            return;
        }
        
        System.out.println("\n--- LISTADO DE DUEÑOS ---");
        for (Duenio duenio : duenios) {
            System.out.println("ID: " + duenio.getId() + 
                               " | DNI: " + duenio.getDni() + 
                               " | Nombre: " + duenio.getNombre() + " " + duenio.getApellido() +
                               " | Email: " + duenio.getEmail());
        }
    }

    /**
     * Renderiza una lista de objetos {@link Mascota}, incluyendo la información
     * de sus entidades relacionadas (Duenio y Microchip).
     * @param mascotas La lista de mascotas a mostrar.
     */
    public void mostrarMascotas(List<Mascota> mascotas) {
        if (mascotas.isEmpty()) {
            System.out.println("-> No se encontraron mascotas.");
            return;
        }

        System.out.println("--- Listado de Mascotas ---");
        for (Mascota mascota : mascotas) {
            System.out.println("-------------------------");
            System.out.println(" MASCOTA ID: " + mascota.getId());
            System.out.println("   Nombre: " + mascota.getNombre() + 
                               " | Especie: " + mascota.getEspecie() +
                               " | Raza: " + mascota.getRaza());

            // Muestra el Dueño (de la Carga Ansiosa)
            if (mascota.getDuenio() != null) {
                System.out.println("   Dueño: " + mascota.getDuenio().getNombre() + 
                                   " " + mascota.getDuenio().getApellido() +
                                   " (DNI: " + mascota.getDuenio().getDni() + ")");
            } else {
                System.out.println("   Dueño: (No asignado)");
            }

            // Muestra el Microchip (de la Carga Ansiosa)
            if (mascota.getMicrochip() != null) {
                System.out.println("   Microchip: " + mascota.getMicrochip().getCodigo());
            } else {
                System.out.println("   Microchip: (Sin chip)");
            }
        }
        System.out.println("-------------------------");
    }
}
