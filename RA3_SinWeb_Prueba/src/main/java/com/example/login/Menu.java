package com.example.login;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;

import java.util.Scanner;

public class Menu {

    private final UsuarioService usuarioService;
    private final Scanner sc;
    private Usuario usuarioLogueado = null;

    public Menu(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.sc = new Scanner(System.in);
    }

    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            if (usuarioLogueado == null) {
                mostrarMenuLogin();
                int opcion = leerOpcion();

                switch (opcion) {
                    case 1 -> login();
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida");
                }
            } else {
                mostrarMenuPrincipal();
                int opcion = leerOpcion();

                switch (opcion) {
                    case 1 -> crearUsuario();
                    case 2 -> listarUsuarios();
                    case 3 -> buscarUsuario();
                    case 4 -> actualizarEmail();
                    case 5 -> cambiarPassword();
                    case 6 -> desactivarUsuario();
                    case 7 -> eliminarUsuario();
                    case 8 -> logout();
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida");
                }
            }
        }

        System.out.println("Saliendo del programa...");
        sc.close();
    }

    // ================= MENÚS =================

    private void mostrarMenuLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n=== MENÚ DE USUARIOS ===");
        System.out.println("1. Crear usuario");
        System.out.println("2. Listar usuarios activos");
        System.out.println("3. Buscar usuario por username");
        System.out.println("4. Actualizar email de usuario");
        System.out.println("5. Cambiar mi contraseña");
        System.out.println("6. Desactivar usuario");
        System.out.println("7. Eliminar usuario");
        System.out.println("8. Cerrar sesión");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    // ================= ACCIONES =================

    private int leerOpcion() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Debes ingresar un número");
            return -1;
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        boolean ok = usuarioService.login(username, password);

        if (ok) {
            usuarioLogueado = usuarioService.buscarPorUsername(username);
            System.out.println("Login correcto. Bienvenido " + usuarioLogueado.getUsername());
        } else {
            System.out.println("Credenciales incorrectas");
        }
    }

    private void logout() {
        usuarioLogueado = null;
        System.out.println("Sesión cerrada");
    }

    private void crearUsuario() {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);

        usuarioService.crearUsuario(u);
        System.out.println("Usuario creado correctamente");
    }

    private void listarUsuarios() {
        usuarioService.listarUsuariosActivos().forEach(System.out::println);
    }

    private void buscarUsuario() {
        System.out.print("Username a buscar: ");
        String username = sc.nextLine();
        System.out.println(usuarioService.buscarPorUsername(username));
    }

    private void actualizarEmail() {
        System.out.print("Username a actualizar: ");
        String username = sc.nextLine();
        System.out.print("Nuevo email: ");
        String email = sc.nextLine();

        Usuario u = usuarioService.buscarPorUsername(username);
        usuarioService.actualizarUsuario(u.getId(), email);
        System.out.println("Email actualizado");
    }

    private void cambiarPassword() {
        System.out.print("Contraseña actual: ");
        String actual = sc.nextLine();
        System.out.print("Nueva contraseña: ");
        String nueva = sc.nextLine();

        usuarioService.cambiarPassword(usuarioLogueado.getId(), actual, nueva);
        System.out.println("Contraseña cambiada correctamente");
    }

    private void desactivarUsuario() {
        System.out.print("Username a desactivar: ");
        String username = sc.nextLine();
        Usuario u = usuarioService.buscarPorUsername(username);
        usuarioService.desactivarUsuario(u.getId());
        System.out.println("Usuario desactivado");
    }

    private void eliminarUsuario() {
        System.out.print("Username a eliminar: ");
        String username = sc.nextLine();
        Usuario u = usuarioService.buscarPorUsername(username);
        usuarioService.eliminarUsuario(u.getId());
        System.out.println("Usuario eliminado");
    }
}
