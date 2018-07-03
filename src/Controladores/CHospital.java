package Controladores;

import Clases.Administrador;
import Clases.Cliente;
import Clases.Empleado;
import Clases.EstadoSuscripcion;
import Clases.EstadoTurno;
import Clases.HorarioAtencion;
import Clases.Hospital;
import Clases.Suscripcion;
import Clases.TipoTurno;
import Clases.Turno;
import Clases.Usuario;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author Jorge
 */
public class CHospital {

    //TODO: Poner tildes
    private static final String[] DIAS = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};

    private static int opd(String dia) {
        for (int i = 0; i < DIAS.length; i++) {
            if (DIAS[i].equals(dia)) {
                return i;
            }
        }
        return 0;
    }

    public static String obtenerDiasNoDisponibles(long idEmpleado, long idHospital, TipoTurno tipo) {
        List<HorarioAtencion> hs = CHospital.obtenerHorariosAtencion(idEmpleado, idHospital);

        String dias = "";

        for (HorarioAtencion h : hs) {
            if (h.getTipo() == tipo) {
                dias += h.getDia();
            }
        }

        String res = "";

        for (String dia : DIAS) {
            if (!dias.contains(dia)) {
                res += opd(dia) + ",";
            }
        }

        if (res.charAt(res.length() - 1) == ',') {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static List<String> obtenerFechasOcupadasyo(long idEmpleado, long idHospital, TipoTurno tipo) {
        List<HorarioAtencion> hs = obtenerHorariosAtencion(idEmpleado, idHospital);
        List<String> fechas = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        List<String> fin = new ArrayList<>();
        for (String dia : DIAS) {
            List<Turno> turnos = new ArrayList<>();
            int cant = 0;
            for (HorarioAtencion h : hs) {
                if (h.getDia().equals(dia)) {
                    cant += h.getClientesMax();
                    turnos.addAll(h.getTurnos());
                }
            }
            for (Turno t : turnos) {
                if (t.getTipo().equals(tipo)) {
                    fechas.add(sdf.format(t.getFecha()));
                }
            }
            Set<String> quipu = new HashSet<String>(fechas);
            for (String key : quipu) {
                if (Collections.frequency(fechas, key) == cant) {
                    fin.add(key);
                }
            }
        }
        return fin;
    }

    public static String obtenerFechasOcupadasJorge(long idEmpleado, long idHospital, TipoTurno tipo) {
        System.out.println("em: " + idEmpleado + ", " + idHospital + ", tipo" + tipo);
        // dias guarda String: Nombre del dia, Integer: cantidad de horarios que tiene ese dia
        HashMap<String, Integer> dias = new HashMap<>();
        // fechas guarda String: fecha#id_horario, Integer: cantidad de turnos vendidos para esa fecha en ese horario
        HashMap<String, Integer> fechas = new HashMap<>();

        List<HorarioAtencion> hs = obtenerHorariosAtencion(idEmpleado, idHospital);

        System.out.println(hs.size());

        for (HorarioAtencion h : hs) {
            // Si el tipo de horario no es del especificado no se cuenta
            if (h.getTipo() != tipo) {
                continue;
            }

            // Por cada "Lunes", "Martes", etc. voy guardando cuantos horarios de atencion tiene
            if (dias.get(h.getDia()) == null) {
                dias.put(h.getDia(), 1);
            } else {
                dias.put(h.getDia(), dias.get(h.getDia()) + 1);
            }

            // Si ese horario tiene algun turno ocupado
            if (h.getTurnos() != null && h.getTurnos().size() > 0) {
                // Los recorro a todos y guardo por cada turno#id_horario cuantos tiene en el HashMap fechas
                for (Turno t : h.getTurnos()) {
                    Date date = t.getFecha();
                    // Aca es el fecha#id_horario
                    String turno_por_horario = new SimpleDateFormat("yyyy-MM-dd").format(date) + "#" + h.getId();

                    if (fechas.get(turno_por_horario) == null) {
                        fechas.put(turno_por_horario, 1);
                    } else {
                        fechas.put(turno_por_horario, fechas.get(turno_por_horario) + 1);
                    }
                }
            }
        }

        // horariosOcupados guarda String: fecha, Integer: cantidad de horarios ocupados en esa fecha
        HashMap<String, Integer> horariosOcupados = new HashMap<>();

        // Recorro todos los pares fechas#id_horario
        Iterator i = fechas.entrySet().iterator();
        while (i.hasNext()) {
            // p: cada cosa en el HashMap (el p.getKey me da lo de la izq y el p.getValue lo de la der)
            Map.Entry<String, Integer> p = (Map.Entry<String, Integer>) i.next();

            // A la key la separo en la parte de la fecha y la parte del id_horario
            String parteKeyFecha = p.getKey().split("#")[0];
            int parteKeyIdHA = Integer.valueOf(p.getKey().split("#")[1]);
            // Obtengo el horario de atencion
            HorarioAtencion ha = null;
            for (HorarioAtencion asdasf : hs) {
                if (asdasf.getId() == parteKeyIdHA) {
                    ha = asdasf;
                }
            }
            // Si el valor de esa fecha (la cantidad de turnos vendidos es igual (o mayor por las dudas) a la cantidad max del turno entonces agrego uno al numero de horarios ocupados en esa fecha)
            // IMPORTANTE: sumo 1 a la cantidad de horarios ocupados en esa fecha
            if (ha != null && p.getValue() >= ha.getClientesMax()) {
                if (horariosOcupados.get(parteKeyFecha) == null) {
                    horariosOcupados.put(parteKeyFecha, 1);
                } else {
                    horariosOcupados.put(parteKeyFecha, horariosOcupados.get(parteKeyFecha) + 1);
                }
            }
        }

        List<String> fechasOcupadas = new ArrayList<>();

        // Recorro toda la lista de turnos ocupados
        Iterator i2 = horariosOcupados.entrySet().iterator();
        while (i2.hasNext()) {
            // Recordatorio p: en String: fecha, Integer: horarios ocupados
            Map.Entry<String, Integer> p = (Map.Entry<String, Integer>) i2.next();

            try {
                // Obtengo el nombre del dia de esa fecha
                String dia = obtenerDiaEspanol(new SimpleDateFormat("yyyy-MM-dd").parse(p.getKey()));

                // Si la cantidad de horarios ocupados en esa fecha es igual a la cantidad de turnos en ese dia (Ver que contiene el HashMap dia mas arriba)
                // Entonces significa que esa fecha en particular esta completamente vendida
                if (dias.get(dia) != null && p.getValue() >= dias.get(dia)) {
                    // Se agrega a las fechas ocupadas
                    fechasOcupadas.add(p.getKey());
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }

        // Se separan por # como el rompe huevo de Luis queria
        String coso = "";
        for (int j = 0; j < fechasOcupadas.size(); j++) {
            coso += fechasOcupadas.get(j) + (j != fechasOcupadas.size() - 1 ? "#" : "");
        }
        return coso;
    }

    public static String obtenerDiaEspanol(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int dia = cal.get(Calendar.DAY_OF_WEEK);
        String diaString = "";
        switch (dia) {
            case Calendar.MONDAY:
                diaString = "Lunes";
                break;
            case Calendar.TUESDAY:
                diaString = "Martes";
                break;
            case Calendar.WEDNESDAY:
                diaString = "Miércoles";
                break;
            case Calendar.THURSDAY:
                diaString = "Jueves";
                break;
            case Calendar.FRIDAY:
                diaString = "Viernes";
                break;
            case Calendar.SATURDAY:
                diaString = "Sábado";
                break;
            case Calendar.SUNDAY:
                diaString = "Domingo";
                break;
        }
        return diaString;
    }

    public static boolean eliminarHorarioAtencion(int id) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        HorarioAtencion ha = null;
        try {
            ha = (HorarioAtencion) em.createNativeQuery("SELECT * FROM horarioatencion WHERE id = " + id, HorarioAtencion.class)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se eimino el horairo de atencion " + id);
            return false;
        }
        if (ha == null) {
            return false;
        }

        ha.eliminar();
        return true;
    }

    public static List<HorarioAtencion> obtenerHorariosAtencion(long idEmpleado, Usuario u) {
        long idHospital = CAdministradores.getAdminByUsuario(u.getId()).getHospital().getId();
        return obtenerHorariosAtencion(idEmpleado, idHospital);
    }

    public static List<HorarioAtencion> obtenerHorariosAtencion(long idEmpleado, long idHospital) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        List<HorarioAtencion> lista = new ArrayList<>();

        try {
            lista = (List<HorarioAtencion>) em.createNativeQuery("SELECT ha.* FROM horarioatencion AS ha, cliente AS c, hospital AS h, hospital_cliente AS hc WHERE hc.hospital_id = h.id AND hc.empleados_id = c.id AND ha.empleado_id = c.id AND ha.hospital_id = h.id AND c.id = :idEmpleado AND h.id = :idHospital AND h.activado = 1", HorarioAtencion.class)
                    .setParameter("idEmpleado", idEmpleado)
                    .setParameter("idHospital", idHospital)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontraron los horairos de atencion");
        }
        return lista;
    }

    public static boolean agregaHorarioAtencion(Usuario u, int idEmpleado, HorarioAtencion ha) {
        Hospital h = CAdministradores.obtenerHospitalAdministrador(u.getCi());
        if (h == null) {
            return false;
        }

        Empleado e = CUsuario.getEmpleado(idEmpleado);
        if (e == null) {
            return false;
        }

        if (!Singleton.getInstance().persist(ha)) {
            return false;
        }

        h.agregarHA(ha);
        e.agregarHA(ha);
        ha.setEmpleado(e);
        ha.setHospital(h);

        if (!Singleton.getInstance().merge(h)) {
            return false;
        }

        if (!Singleton.getInstance().merge(e)) {
            return false;
        }

        return true;
    }

    public static void borrarAdministrador(String nomHospital, String ciAdmin) {
        Hospital h = obtenerHospital(nomHospital);
        h.removerAdministrador(ciAdmin);
        Singleton.getInstance().merge(h);
    }

    public static void modificarAdministrador(String nomHospital, Usuario u) {
        List<Administrador> administradores = obtenerHospital(nomHospital).getAdministradores();

        for (Administrador a : administradores) {
            if (a.getUsuario().getCi().equals(u.getCi())) {
                a.setUsuario(u);
                Singleton.getInstance().merge(a);
            }
        }
    }

    public static List<Usuario> obtenerAdministradoresHospital(String nomHospital) {
        List<Administrador> administradores = obtenerHospital(nomHospital).getAdministradores();

        if (administradores == null) {
            return null;
        }

        List<Usuario> usuarios = new ArrayList<>();

        for (Administrador a : administradores) {
            usuarios.add(a.getUsuario());
        }

        return usuarios.size() == 0 ? null : usuarios;
    }

    public static String agregarAdministrador(String nomHospital, Usuario u) {
        Hospital h = obtenerHospital(nomHospital);
        List<Administrador> admins = h.getAdministradores();

        if (admins != null) {
            for (Administrador a : admins) {
                if (a.getUsuario().getCi().equals(u.getCi())) {
                    return "C.I. ya existe";
                } else if (a.getUsuario().getCorreo().equals(u.getCorreo())) {
                    return "Correo ya existe";
                }
            }
        }

        h.agregarAdministrador(u);
        Singleton.getInstance().merge(h);
        return "";
    }

    public static void modificarHospital(String nombre, Hospital h) {
        Hospital viejo = obtenerHospital(nombre);
        viejo.setNombre(h.getNombre());
        viejo.setDirectora(h.getDirectora());
        viejo.setPublico(h.isPublico());
        viejo.setCorreo(h.getCorreo());
        viejo.setTelefono(h.getTelefono());
        viejo.setDepartamento(h.getDepartamento());
        viejo.setCalle(h.getCalle());
        viejo.setNumero(h.getNumero());
        viejo.setLatitud(h.getLatitud());
        viejo.setLongitud(h.getLongitud());
        Singleton.getInstance().merge(viejo);
    }

    public static void borrarHospital(String nombre) {
        Hospital h = obtenerHospital(nombre);

        if (h != null) {
            h.setActivado(false);
            Singleton.getInstance().merge(h);
        }
    }

    public static Hospital obtenerHospital(String nombre) {
        List<Hospital> hospitales = obtenerHospitales();
        for (Hospital h : hospitales) {
            if (h.getNombre().equals(nombre)) {
                return h;
            }
        }

        return null;
    }

    public static Hospital obtenerHospital(long idHosp) {
        List<Hospital> hospitales = obtenerHospitales();
        for (Hospital h : hospitales) {
            if (h.getId() == idHosp) {
                return h;
            }
        }

        return null;
    }

    public static List<HorarioAtencion> obtenerHorariosHospital(long idHospital) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        List<HorarioAtencion> lista = new ArrayList<>();
        try {
            lista = (List<HorarioAtencion>) em.createNativeQuery("SELECT ha.* FROM horarioatencion AS ha,hospital AS h WHERE ha.hospital_id=h.id AND h.id =:idHospital", HorarioAtencion.class)
                    .setParameter("idHospital", idHospital)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontraron los horairos de atencion");
        }
        return lista;
    }

    public static List<HorarioAtencion> obtenerHorariosConTurnosDisp(long idHospital) {
        List<HorarioAtencion> Todos = obtenerHorariosHospital(idHospital);
        List<HorarioAtencion> fin = new ArrayList<>();

        for (HorarioAtencion ha : Todos) {
            if (ha.getClientesMax() != ha.getTurnos().size()) {
                fin.add(ha);
            }

        }
        return fin;
    }

    public static List<Turno> obtenerTurnosDeUnHorario(long idHorario) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        List<Turno> lista = new ArrayList<>();
        try {
            lista = (List<Turno>) em.createNativeQuery("SELECT * FROM turno  WHERE horarioAtencion_id=:idHorario", Turno.class)
                    .setParameter("idHorario", idHorario)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontraron los turnos ");
        }
        return lista;
    }

    public static List<Hospital> obtenerHospitales() {
        List<Hospital> lista = null;
        if (!Singleton.getInstance().getEntity().getTransaction().isActive()) {
            Singleton.getInstance().getEntity().getTransaction().begin();
        }

        try {
            lista = Singleton.getInstance().getEntity().createNativeQuery("SELECT * FROM hospital WHERE activado = 1", Hospital.class).getResultList();
            Singleton.getInstance().getEntity().getTransaction().commit();
        } catch (Exception e) {

            if (Singleton.getInstance().getEntity().getTransaction().isActive()) {
                Singleton.getInstance().getEntity().getTransaction().rollback();
            }

        }
        return lista;
    }

    public static String obtenerHoras(long idEmpleado, String hospital, TipoTurno turno) {
        Hospital h = obtenerHospital(hospital);
        Empleado medico = CUsuario.getEmpleado(idEmpleado);
        List<HorarioAtencion> horarios = h.getHorarioAtencions();
        String res = "";
        if (!horarios.isEmpty()) {
            for (HorarioAtencion hs : horarios) {
                if (hs.getTipo() == turno && hs.getEmpleado().getId() == medico.getId() && hs.getEstado() == EstadoTurno.PENDIENTE) {

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String horaInicio = dateFormat.format(hs.getHoraInicio());
                    String horaFin = dateFormat.format(hs.getHoraFin());
                    res += hs.getId() + "-" + hs.getDia() + "-" + horaInicio + "-" + horaFin + "/";
                }
            }
        }

        if (res.equals("")) {
            res = "/";
        }

        if (res.charAt(res.length() - 1) == '/') {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static int getOrden(List<Turno> turnos) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getNumero() != turnos.get(i + 1).getNumero() - 1) {
                return i + 1;
            }
        }
        return 0;
    }

    public static String agregarTurno(String hospital, long idUsuario, String dia, long ciEmpleado, String especialidad, long idHorario) throws ParseException {
        Hospital h = obtenerHospital(hospital);
        Empleado medico = CUsuario.getEmpleado(ciEmpleado);
        HorarioAtencion ha = h.obtenerHorarioById(idHorario);
        List<Date> horarios = new ArrayList<>();
        List<Turno> turnosDia = new ArrayList<>();

        /*parsear el string de la fecha a date*/
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dia);
        } catch (ParseException ex) {
            Logger.getLogger(CHospital.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Termina de parsear la fecha*/

 /* Parseo el dia a español */
        Locale spanishLocale = new Locale("es", "ES");
        String dayOfWeek = new SimpleDateFormat("EEEE", spanishLocale).format(date);

        /* Termina el parseo del dia */
        String[] array = dia.split("-");
        Date d = new Date(Integer.valueOf(array[0]), Integer.valueOf(array[1]) - 1, Integer.valueOf(array[2]));
        Format f = new SimpleDateFormat("MMMM");
        String mes = f.format(d);

        /* parseo para el mensaje */
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date dd = formato.parse(dia);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        List<Turno> turnos = ha.getTurnos();

        Cliente c = CCliente.getClientebyUsuario(idUsuario);

        String hora;
        Turno t = new Turno();

        t.setCliente(c);
        t.setEstado(EstadoTurno.PENDIENTE);
        t.setHorarioAtencion(ha);
        t.setFecha(dd);
        t.setTipo(TipoTurno.ATENCION);
        t.setEspecialidad(especialidad);

        //email
        if (turnos.isEmpty()) {
            t.setNumero(1);
            t.setHora(ha.getHoraInicio());
            ha.agregarTurno(t);
            medico.agregarTurno(t);
            c.agregarTurno(t);
            hora = dateFormat.format(ha.getHoraInicio());
            Singleton.getInstance().persist(t);

            //preparar mail
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CCorreo.enviarReserva(c, medico, h.getNombre(), "Atención", t, "Detalles de su reserva");
                }
            }).start();

            return "Su turno ha sido reservado para el día " + array[2] + " de " + mes + " del " + array[0] + " a las " + hora + "hs";
        } else {

            /*Para sacar el numero del turno*/
            for (Turno ts : turnos) {

                if (ts.getTipo() == TipoTurno.ATENCION && ts.getEstado() == EstadoTurno.PENDIENTE && ts.getFecha().compareTo(dd) == 0 && ts.getEspecialidad().equals(especialidad)
                        && ts.getCliente().getId() == c.getId()) {
                    return "Usted ya solicitó un turno de atención con ese médico y especialidad";
                }

                if (ts.getTipo() == TipoTurno.ATENCION && ts.getEstado() == EstadoTurno.PENDIENTE && ts.getFecha().compareTo(dd) == 0) {
                    turnosDia.add(ts);
                }
            }

            for (int i = 0; i < ha.getClientesMax(); i++) {
                Date hi = ha.getHoraInicio();
                Date hf = ha.getHoraFin();
                Date hsss = Date.from(Instant.ofEpochMilli(hi.getTime() + ((hf.getTime() - hi.getTime()) / ha.getClientesMax()) * i));
                horarios.add(hsss);
            }

            List<Turno> orden = turnos;

            Collections.sort(orden, new Comparator<Turno>() {
                public int compare(Turno t1, Turno t2) {
                    return t1.getNumero() - t2.getNumero();
                }
            });

            if (orden.get(orden.size() - 1).getNumero() == orden.size()) {
                /*Para sacar la hora*/
                t.setNumero(turnosDia.size() + 1);
                t.setHora(horarios.get(turnosDia.size()));

            } else {
                if (orden.get(0).getNumero() != 1) {

                    t.setNumero(1);
                    t.setHora(horarios.get(0));

                } else {
                    int i = getOrden(orden);
                    t.setNumero(i + 1);
                    t.setHora(horarios.get(i));
                }

                //setear
            }

            ha.agregarTurno(t);
            medico.agregarTurno(t);
            c.agregarTurno(t);
            hora = dateFormat.format(t.getHora());
            Singleton.getInstance().persist(t);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    CCorreo.enviarReserva(c, medico, h.getNombre(), "Atención", t, "Detalles de su reserva");
                }
            }).start();

            return "Su turno ha sido reservado para el día " + array[2] + " de " + mes + " del " + array[0] + " a las " + hora + "hs";

        }

    }

    public static List<Suscripcion> obtenerSuscripcionesbyUsuarioAdminHospital(Long idAdminHospital) {
        Administrador admin = CAdministradores.getAdminByUsuario(idAdminHospital);
        return admin.getHospital().getSuscripciones();
    }

    public static boolean actualizarSuscripcion(long idSus, EstadoSuscripcion estado) {
        Suscripcion s = obtenerSuscripcion(idSus);
        EstadoSuscripcion estadoAnterior = s.getEstado();
        s.setEstado(estado);
        if (estado == EstadoSuscripcion.ACTIVA) {
            Date fechaC = new GregorianCalendar().getTime();

            s.setFechaContratada(fechaC);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaC);
            cal.add(Calendar.MONTH, s.getCantMeses());

            s.setFechaVencimiento(cal.getTime());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String contenido = "Su suscripción en el hospital " + s.getHospital().getNombre() + " fue ";
                switch (s.getEstado()) {
                    case ACTIVA:
                        contenido += ((estadoAnterior.equals(EstadoSuscripcion.VENCIDA)) ? "renovada":"aceptada") +", a partir de este momento puede solicitar turnos en el mismo.";
                        break;
                    case ELIMINADA:
                        contenido += "eliminada, ya no puede solicitar turnos en el mismo.";
                        break;
                    case VENCIDA:
                        contenido = "Su suscripción en el hospital " + s.getHospital().getNombre() + " se ha vencido, para continuar utilizando los servicios en dicho hospital solicite una nueva o pida la renovación de la actual al administrador.";
                        break;
                    case RECHAZADA:
                        contenido += "rechazada, solicite una nueva para utilizar los servicios del mismo.";
                        break;
                    default:
                        break;
                }
                CCorreo.enviar(s.getCliente().getUsuario().getCorreo(), "Suscripción actualizada - Hospital Web", contenido);

            }
        }).start();
        return Singleton.getInstance().merge(s);
    }

    public static Suscripcion obtenerSuscripcion(long idSus) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        Suscripcion lista = null;

        try {
            lista = (Suscripcion) em.createNativeQuery("SELECT * FROM suscripcion WHERE id = :idSus", Suscripcion.class
            )
                    .setParameter("idSus", idSus)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontro la suscripcion");
        }
        return lista;
    }

    public static void agregarSuscripcion(long idCli, long idHosp, int cantMeses) {
        Suscripcion s = new Suscripcion();
        Cliente c = CCliente.getCliente(idCli);
        Hospital h = CHospital.obtenerHospital(idHosp);

        s.setCliente(c);
        s.setHospital(h);
        s.setEstado(EstadoSuscripcion.PENDIENTE);
        s.setCantMeses(cantMeses);

        Singleton.getInstance().persist(s);
    }

    public static Suscripcion tieneSuscripcion(long idCli, long idHosp, EstadoSuscripcion tipo) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        Suscripcion lista = null;

        try {
            lista = (Suscripcion) em.createNativeQuery("SELECT * FROM suscripcion WHERE cliente_id = :idCli AND hospital_id = :idHosp AND estado = :estado", Suscripcion.class
            )
                    .setParameter("idCli", idCli)
                    .setParameter("idHosp", idHosp)
                    .setParameter("estado", tipo)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontraro la suscripcion");
        }
        return lista;
    }

    public static Suscripcion obtenerEstadoDeSuscripcion(long idCli, long idHosp) {
        EntityManager em = Singleton.getInstance().getEntity();
        em.getTransaction().begin();
        List<Suscripcion> lista = null;

        try {
            lista = (List<Suscripcion>) em.createNativeQuery("SELECT * FROM suscripcion WHERE cliente_id = :idCli AND hospital_id = :idHosp", Suscripcion.class
            )
                    .setParameter("idCli", idCli)
                    .setParameter("idHosp", idHosp)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("No se encontraro la suscripcion");
        }

        if (lista == null || lista.size() == 0) {
            return null;
        }

        if (suscripcionesContiene(lista, EstadoSuscripcion.ACTIVA) != null) {
            return suscripcionesContiene(lista, EstadoSuscripcion.ACTIVA);
        }
        if (suscripcionesContiene(lista, EstadoSuscripcion.PENDIENTE) != null) {
            return suscripcionesContiene(lista, EstadoSuscripcion.PENDIENTE);
        }
        if (suscripcionesContiene(lista, EstadoSuscripcion.VENCIDA) != null) {
            return suscripcionesContiene(lista, EstadoSuscripcion.VENCIDA);
        }
        if (suscripcionesContiene(lista, EstadoSuscripcion.RECHAZADA) != null) {
            return suscripcionesContiene(lista, EstadoSuscripcion.RECHAZADA);
        }
        if (suscripcionesContiene(lista, EstadoSuscripcion.ELIMINADA) != null) {
            return suscripcionesContiene(lista, EstadoSuscripcion.ELIMINADA);
        }

        return null;
    }

    private static Suscripcion suscripcionesContiene(List<Suscripcion> sus, EstadoSuscripcion estado) {
        for (Suscripcion s : sus) {
            if (s.getEstado() == estado) {
                return s;
            }
        }
        return null;
    }

    public static String chequearDisponibilidadDeHorarioDeAtencionParaPoderIngresarElMismoSiEsQueEstaDisponible(String hInicio, String hFin, long med, String dia) {
        Empleado e = CUsuario.getEmpleado(med);
        if (e == null) {
            return "Medico no existe";
        }

        List<HorarioAtencion> hs = e.getHorariosAtencions();
        if (hs == null) {
            return "OK";
        }

        long dInicio = new Date(2018, 0, 0, Integer.valueOf(hInicio.split(":")[0]), Integer.valueOf(hInicio.split(":")[1])).getTime();
        long dFin = new Date(2018, 0, 0, Integer.valueOf(hFin.split(":")[0]), Integer.valueOf(hFin.split(":")[1])).getTime();

        System.out.println(dInicio);
        System.out.println(dFin);

        for (HorarioAtencion h : hs) {
            if (!h.isEliminado() && !h.isDesactivado() && h.getDia().equals(dia)) {
                long haInicio = new Date(2018, 0, 0, h.getHoraInicio().getHours(), h.getHoraInicio().getMinutes()).getTime();
                long haFin = new Date(2018, 0, 0, h.getHoraFin().getHours(), h.getHoraFin().getMinutes()).getTime();
                System.out.println(haInicio);
                System.out.println(haFin);
                if (dInicio < haFin && dFin > haInicio) {
                    return "Hospital: " + h.getHospital().getNombre() + "</br>"
                            + "Hora Inicio: " + h.getHoraInicio() + "</br>"
                            + "Hora Fin: " + h.getHoraFin();
                }
            }
        }

        return "OK";
    }
    
}
