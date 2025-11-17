package main;

import dao.*;
import service.*;
import java.util.Scanner;
/**
 * Clase principal de ejecución del sistema (Entry Point).
 *
 * RESPONSABILIDADES:
 * 1. Bootstrapping: Inicialización de todos los componentes del sistema en el orden correcto.
 * 2. Dependency Injection (DI): Configuración manual de las dependencias entre capas
 * (DAO -> Service -> Handler -> AppMenu).
 * 3. Ejecución: Inicio del ciclo de vida de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
       // --- 1. INICIALIZACIÓN DE LA CAPA DE ACCESO A DATOS (DAO Layer) ---
        // Instanciación de las implementaciones concretas para el acceso a la Base de Datos.
        DuenioDAO duenioDao = new DuenioDaoImpl();
        MascotaDAO mascotaDao = new MascotaDaoImpl();
        MicrochipDAO microchipDao = new MicrochipDaoImpl();

       // --- 2. INICIALIZACIÓN DE LA CAPA DE SERVICIO (Service Layer) ---
        // Inyección de dependencias: Los servicios reciben las instancias de los DAOs necesarios.
        // Se configuran las dependencias cruzadas para validaciones de integridad referencial lógica.
        DuenioService duenioService = new DuenioServiceImpl(duenioDao, mascotaDao);
        MicrochipService microchipService = new MicrochipServiceImpl(microchipDao);
        MascotaService mascotaService = new MascotaServiceImpl(mascotaDao, microchipDao, duenioDao);
        
      // --- 3. INICIALIZACIÓN DE LA CAPA DE PRESENTACIÓN (UI Layer) ---
        // Configuración de componentes de Vista (Display) y Control (Handler).
        Scanner scanner = new Scanner(System.in);
        MenuDisplay menuDisplay = new MenuDisplay(); // Componente de visualización
        
        // El MenuHandler actúa como controlador, orquestando la interacción entre el usuario y los servicios.
        MenuHandler menuHandler = new MenuHandler(scanner, menuDisplay, 
                                                  duenioService, mascotaService, microchipService);
        
        // --- 4. INICIALIZACIÓN DEL ORQUESTADOR DE APLICACIÓN ---
        // Configuración del componente que gestiona el ciclo de vida del menú principal.
        AppMenu menu = new AppMenu(menuHandler, menuDisplay);
        
        // --- 5. EJECUCIÓN ---
        // Inicio del flujo principal de la aplicación.
        menu.iniciar();
    }
}
