/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Clases.Administrador;
import Clases.Cliente;
import Clases.Empleado;
import Clases.Hospital;
import Clases.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Ale
 */
public class CUsuario {

    Singleton s = Singleton.getInstance();

    public static boolean cambiarPass(long idUsuario, String nuevaPass) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        try {
            em.createNativeQuery("UPDATE usuario SET contrasenia = :contrasenia WHERE id = :id")
                    .setParameter("id", idUsuario)
                    .setParameter("contrasenia", nuevaPass)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            System.out.println("No se pudo cambiar pass");
            return false;
        }
        return true;
    }

    public static String obtenerTipo(Usuario u) {
        List<Administrador> admins = CAdministradores.obtenerAdministradores();
        List<Cliente> soloClientes = CCliente.obtenerClientesNoEmpleados();

        if (admins != null) {
            for (Administrador a : admins) {
                if (a.getUsuario().getCi().equals(u.getCi())) {
                    if (a.isAdminGeneral()) {
                        return "General";
                    } else {
                        return "Hospital";
                    }
                }
            }
        }

        if (soloClientes != null) {
            for (Cliente c : soloClientes) {
                if (c.getUsuario().getId() == u.getId()) {
                    return "Cliente";
                }
            }
        }

        return "Empleado";
    }

    public Usuario login(String ci, String contrasenia) {
        EntityManager em = s.getEntity();
        em.getTransaction().begin();
        Usuario u = null;

        try {
            u = (Usuario) em.createQuery("SELECT u FROM Usuario u WHERE ci= :cedula AND contrasenia= :pass", Usuario.class)
                    .setParameter("cedula", ci)
                    .setParameter("pass", contrasenia)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontró el usuario");
        }
        if (u != null) {
            Cliente cliente = CCliente.getClientebyUsuario(u.getId());
            if (cliente != null && !cliente.isActivo()) {
                u = null;
            }
        }
        return u;
    }

    public Empleado getEmpleadobyUsuario(long id) {
        EntityManager em = s.getEntity();

        Empleado empleado = null;
        em.getTransaction().begin();
        try {
            empleado = (Empleado) em.createNativeQuery("SELECT * FROM cliente WHERE usuario_id=" + id, Empleado.class)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

            System.err.println("No se puedo encontrar el empleado relacionado al usuario con id: " + id);
        }
        return empleado;
    }

    public static Empleado getEmpleado(long id) {
        Empleado empleado = null;
        try {
            Singleton.getInstance().getEntity().getTransaction().begin();
            empleado = (Empleado) Singleton.getInstance().getEntity().createNativeQuery("SELECT * FROM cliente WHERE id=" + id, Empleado.class)
                    .getSingleResult();
            Singleton.getInstance().getEntity().getTransaction().commit();
        } catch (Exception e) {
            Singleton.getInstance().getEntity().getTransaction().rollback();
            System.err.println("No se puedo encontrar el empleado con id: " + id);
        }
        return empleado;
    }

    public List<Empleado> obtenerEmpleados(long idHospital) {
        EntityManager em = s.getEntity();

        List<Empleado> empleados = null;
        em.getTransaction().begin();
        try {
            empleados = (List<Empleado>) em.createNativeQuery("SELECT * FROM cliente WHERE DTYPE = 'Empleado' AND activo = 1 AND id NOT IN(SELECT empleados_id FROM hospital_cliente WHERE Hospital_id ="+idHospital+") ", Empleado.class)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            System.err.println("No se pudo cargar los empleados");
        }
        return empleados;
    }

    public boolean bajaEmpleado(String idEmpleado) {
        Empleado empleado = getEmpleado(Long.valueOf(idEmpleado));
        empleado.setActivo(false);
        return s.merge(empleado);
    }

    public boolean cedulaExiste(String cedula) {
        EntityManager em = s.getEntity();

        Usuario u = null;

        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            u = (Usuario) em.createQuery("FROM Usuario U WHERE U.ci= :cedula", Usuario.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Error en la transaccion, usuario con cedula: " + cedula);
        }
        return u != null;

    }
}
