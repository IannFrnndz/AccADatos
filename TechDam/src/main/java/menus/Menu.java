package menus;



import DAO.EmpleadoDAO;
import DAO.ProyectoDAO;
import model.Empleado;
import model.Proyecto;
import service.ProcedimientosService;
import service.TransaccionesService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {

    private Scanner sc = new Scanner(System.in);
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private ProyectoDAO proyectoDAO = new ProyectoDAO();
    private ProcedimientosService procService = new ProcedimientosService();
    private TransaccionesService transService = new TransaccionesService();

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n===== MENÚ EMPRESA =====");
            System.out.println("1. Crear empleado");
            System.out.println("2. Listar empleados");
            System.out.println("3. Actualizar empleado");
            System.out.println("4. Eliminar empleado");
            System.out.println("5. Crear proyecto");
            System.out.println("6. Listar proyectos");
            System.out.println("7. Actualizar proyecto");
            System.out.println("8. Eliminar proyecto");
            System.out.println("9. Transferir presupuesto entre proyectos");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> crearEmpleado();
                case 2 -> listarEmpleados();
                case 3 -> actualizarEmpleado();
                case 4 -> eliminarEmpleado();
                case 5 -> crearProyecto();
                case 6 -> listarProyectos();
                case 7 -> actualizarProyecto();
                case 8 -> eliminarProyecto();
                case 9 -> transferirPresupuesto();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // =================== MÉTODOS ===================

    private void crearEmpleado() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Departamento: ");
        String dept = sc.nextLine();
        System.out.print("Salario: ");
        BigDecimal salario = new BigDecimal(sc.nextLine());
        System.out.print("Activo (true/false): ");
        boolean activo = Boolean.parseBoolean(sc.nextLine());

        Empleado e = new Empleado(0, nombre, dept, salario, activo);
        int id = empleadoDAO.crear(e);
        System.out.println("Empleado creado con ID: " + id);
    }

    private void listarEmpleados() {
        List<Empleado> empleados = empleadoDAO.obtenerTodos();
        empleados.forEach(System.out::println);
    }

    private void actualizarEmpleado() {
        System.out.print("ID del empleado a actualizar: ");
        int id = Integer.parseInt(sc.nextLine());
        Optional<Empleado> empOpt = empleadoDAO.obtenerPorId(id);
        if (empOpt.isEmpty()) {
            System.out.println("Empleado no encontrado.");
            return;
        }
        Empleado emp = empOpt.get();

        System.out.print("Nombre [" + emp.getNombre() + "]: ");
        String nombre = sc.nextLine();
        if (!nombre.isBlank()) emp.setNombre(nombre);

        System.out.print("Departamento [" + emp.getDepartamento() + "]: ");
        String dept = sc.nextLine();
        if (!dept.isBlank()) emp.setDepartamento(dept);

        System.out.print("Salario [" + emp.getSalario() + "]: ");
        String salarioStr = sc.nextLine();
        if (!salarioStr.isBlank()) emp.setSalario(new BigDecimal(salarioStr));

        System.out.print("Activo [" + emp.isActivo() + "] (true/false): ");
        String activoStr = sc.nextLine();
        if (!activoStr.isBlank()) emp.setActivo(Boolean.parseBoolean(activoStr));

        if (empleadoDAO.actualizar(emp)) {
            System.out.println("Empleado actualizado correctamente.");
        } else {
            System.out.println("Error al actualizar empleado.");
        }
    }

    private void eliminarEmpleado() {
        System.out.print("ID del empleado a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());
        if (empleadoDAO.eliminar(id)) {
            System.out.println("Empleado eliminado correctamente.");
        } else {
            System.out.println("Error al eliminar empleado.");
        }
    }

    private void crearProyecto() {
        System.out.print("Nombre del proyecto: ");
        String nombre = sc.nextLine();
        System.out.print("Presupuesto: ");
        BigDecimal presupuesto = new BigDecimal(sc.nextLine());

        Proyecto p = new Proyecto(0, nombre, presupuesto);
        int id = proyectoDAO.crear(p);
        System.out.println("Proyecto creado con ID: " + id);
    }

    private void listarProyectos() {
        List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
        proyectos.forEach(System.out::println);
    }

    private void actualizarProyecto() {
        System.out.print("ID del proyecto a actualizar: ");
        int id = Integer.parseInt(sc.nextLine());
        Optional<Proyecto> proyOpt = proyectoDAO.obtenerPorId(id);
        if (proyOpt.isEmpty()) {
            System.out.println("Proyecto no encontrado.");
            return;
        }
        Proyecto proy = proyOpt.get();

        System.out.print("Nombre [" + proy.getNombre() + "]: ");
        String nombre = sc.nextLine();
        if (!nombre.isBlank()) proy.setNombre(nombre);

        System.out.print("Presupuesto [" + proy.getPresupuesto() + "]: ");
        String presStr = sc.nextLine();
        if (!presStr.isBlank()) proy.setPresupuesto(new BigDecimal(presStr));

        if (proyectoDAO.actualizar(proy)) {
            System.out.println("Proyecto actualizado correctamente.");
        } else {
            System.out.println("Error al actualizar proyecto.");
        }
    }

    private void eliminarProyecto() {
        System.out.print("ID del proyecto a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());
        if (proyectoDAO.eliminar(id)) {
            System.out.println("Proyecto eliminado correctamente.");
        } else {
            System.out.println("Error al eliminar proyecto.");
        }
    }

    private void transferirPresupuesto() {
        System.out.print("ID proyecto origen: ");
        int origen = Integer.parseInt(sc.nextLine());
        System.out.print("ID proyecto destino: ");
        int destino = Integer.parseInt(sc.nextLine());
        System.out.print("Monto a transferir: ");
        BigDecimal monto = new BigDecimal(sc.nextLine());

        if (transService.transferirPresupuesto(origen, destino, monto)) {
            System.out.println("Transferencia completada correctamente.");
        } else {
            System.out.println("Error en la transferencia.");
        }
    }
}
