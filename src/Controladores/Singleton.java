package Controladores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Jorge
 */
@PersistenceContext
public class Singleton {

    private static Singleton INSTANCE;
    private static EntityManagerFactory EMF;
    private static EntityManager EM;

    private Singleton() {
        EMF = Persistence.createEntityManagerFactory("HospitalServerPU");
        EM = EMF.createEntityManager();
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }

        return INSTANCE;
    }

    public EntityManager getEntity() {
        return EM;
    }

    public void cerrarSesion() {
        EM.close();
    }

    public boolean persist(Object object) {
        System.out.print("PERSIST: " + object.getClass().getSimpleName());
        EntityManager em = getEntity();

        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println(" FALLO");
            return false;
        }
        System.out.println(" EXITO");
        return true;
    }

    public boolean remove(Object object) {
        System.out.print("REMOVE: " + object.getClass().getSimpleName());
        EntityManager em = getEntity();
        em.getTransaction().begin();

        try {
            em.remove(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println(" FALLO");
            return false;
        }
        System.out.println(" EXITO");
        return true;
    }

    public void refresh(Object object) {
        boolean f = false;
        System.out.print("REFRESH: " + object.getClass().getSimpleName());
        EntityManager em = getEntity();
        em.getTransaction().begin();
        try {
            em.refresh(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            f = true;
            System.out.println(" FALLO");
        }
        if (!f)
        System.out.println(" EXITO");
    }

    public boolean merge(Object object) {
        System.out.print("MERGE: " + object.getClass().getSimpleName());
        EntityManager em = getEntity();
        em.getTransaction().begin();
        try {
            em.merge(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println(" FALLO");
            return false;
        }
        System.out.println(" EXITO");
        return true;
    }

}
