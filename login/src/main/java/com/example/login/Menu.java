package com.example.login;


import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import java.util.Scanner;

public class Menu {

    private final UsuarioService usuarioService;
    private final Scanner sc;

    public Menu(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.sc = new Scanner(System.in);
    }

    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1 -> crearUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarUsuario();
                case 4 -> actualizarEmail();
                case 5 -> desactivarUsuario();
                case 6 -> eliminarUsuario();
                case 0 -> {
                    salir = true;
                    System.out.println("Saliendo del programa...");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Opción no válida");
            }
        }

        sc.close();
    }

    private void mostrarMenu() {
        System.out.println("\n=== MENÚ DE USUARIOS ===");
        System.out.println("1. Crear usuario");
        System.out.println("2. Listar usuarios activos");
        System.out.println("3. Buscar usuario por username");
        System.out.println("4. Actualizar email de usuario");
        System.out.println("5. Desactivar usuario (borrado lógico)");
        System.out.println("6. Eliminar usuario (borrado físico)");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private int leerOpcion() {
        int op = -1;
        try {
            op = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Debes ingresar un número");
        }
        return op;
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
        u.setPassword(password); // tu service lo hashea automáticamente

        usuarioService.crearUsuario(u);
        System.out.println("Usuario creado: " + u);
    }

    private void listarUsuarios() {
        System.out.println("Usuarios activos:");
        usuarioService.listarUsuariosActivos().forEach(System.out::println);
    }

    private void buscarUsuario() {
        System.out.print("Username a buscar: ");
        String username = sc.nextLine();
        Usuario u = usuarioService.buscarPorUsername(username);
        System.out.println("Encontrado: " + u);
    }

    private void actualizarEmail() {
        System.out.print("Username a actualizar: ");
        String username = sc.nextLine();
        System.out.print("Nuevo email: ");
        String nuevoEmail = sc.nextLine();

        Usuario u = usuarioService.buscarPorUsername(username);
        usuarioService.actualizarUsuario(u.getId(), nuevoEmail);
        System.out.println("Usuario actualizado: " + usuarioService.buscarPorUsername(username));
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
