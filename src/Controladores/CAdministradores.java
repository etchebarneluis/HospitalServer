/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Clases.Administrador;
import Clases.Cliente;
import Clases.Hospital;
import Clases.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Jorge
 */
public class CAdministradores {
    
    public static Administrador getAdminByUsuario(long idUsuario) {
        Administrador admin = null;
        try {
            Singleton.getInstance().getEntity().getTransaction().begin();
            admin = (Administrador) Singleton.getInstance().getEntity().createNativeQuery("SELECT * FROM administrador WHERE usuario_id=" + idUsuario, Administrador.class)
                    .getSingleResult();
            Singleton.getInstance().getEntity().getTransaction().commit();
        } catch (Exception e) {
            Singleton.getInstance().getEntity().getTransaction().rollback();
            System.err.println("No se puedo encontrar el cliente relacionado al usuario con id: " + idUsuario);
        }
        return admin;
    }
    
    public static Hospital obtenerHospitalAdministrador (String ci) {
        EntityManager em = Singleton.getInstance ().getEntity();
        em.getTransaction().begin();
        Hospital h = null;

        try {
            h = (Hospital) em.createNativeQuery("SELECT h.* FROM hospital AS h, administrador AS a, usuario AS u WHERE h.id = a.hospital_id AND a.usuario_id = u.id AND u.ci = :cedula AND h.activado = 1", Hospital.class)
                    .setParameter("cedula", ci)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontró el usuario");
        }
        return h;
    }
    
    public static List<Administrador> obtenerAdministradores () {
        List<Administrador> lista = null;
        Singleton.getInstance().getEntity().getTransaction().begin();
        try {
            lista = Singleton.getInstance().getEntity().createNativeQuery ("SELECT * FROM administrador", Administrador.class).getResultList ();
            Singleton.getInstance().getEntity().getTransaction().commit();
        } catch (Exception e) {
            Singleton.getInstance().getEntity().getTransaction().rollback();
        }
        return lista;
    }
    
    public static List<Administrador> obtenerAdministradores2 () {
        List<Administrador> lista = null;

        try {
            if (!Singleton.getInstance().getEntity().getTransaction().isActive()) {
                Singleton.getInstance().getEntity().getTransaction().begin();
            }
            lista = Singleton.getInstance().getEntity().createNativeQuery("SELECT * FROM administrador", Administrador.class)
                    .getResultList();
            Singleton.getInstance().getEntity().getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Controladores.CAdministradores.obtenerAdministradores2(): Error en la transaccion");
            if (Singleton.getInstance().getEntity().getTransaction().isActive()) {
                Singleton.getInstance().getEntity().getTransaction().rollback();
            }
        }
        if (lista != null) {
            return lista;
        }
        return new ArrayList<>();
    }
    
    public static void agregarAdminGeneral (Usuario u) {
        Administrador a = new Administrador ();
        a.setAdminGeneral (true);
        a.setUsuario (u);
        Singleton.getInstance ().persist (a);
    }
}
