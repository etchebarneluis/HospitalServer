package Controladores;

import Clases.Cliente;
import Clases.Empleado;
import Clases.Turno;
import Clases.Usuario;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author Jorge
 */
public class CCorreo {

    private static final String NOMBRE = "HospitalWeb";
    private static final String CORREO = "HospitalWebUy@gmail.com";
    private static final String PASS = "rooteo1234";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    public static void enviarContrasenia(Usuario u) {
        enviar(u.getCorreo(), "Bienvenido a Hospital Web", "Hola Administrador,\n\nLe damos la bienvenida a Hospital Web y esperamos que disfrute de nuestro servicios.\n\nSu contraseña es: " + u.getContrasenia() + "\nLa misma se puede cambiar ingresando a la pagina.\n\nDisfrútalo.\n\nEl Equipo de HospitalWeb.");
    }

    public static void enviarContrasenia(Cliente c) {
        enviar(c.getUsuario().getCorreo(), "Bienvenido a Hospital Web", "Hola " + c.getNombre() + " " + c.getApellido() + ",\n\nLe damos la bienvenida a Hospital Web y esperamos que disfrute de nuestro servicios.\n\nSu contraseña es: " + c.getUsuario().getContrasenia() + "\nLa misma se puede cambiar ingresando a la pagina.\n\nDisfrútalo.\n\nEl Equipo de HospitalWeb.");
    }

    public static void enviarReserva(Cliente c, Empleado medico, String hospital, String tipoTurno, Turno t, String asunto) {

        String contenido;

        if (asunto.contains("Recordatorio")) {
            contenido = "Sr/a " + c.getNombre() + " " + c.getApellido() + " " + "le recordamos que usted posee una reserva para mañana, a continuación se detalla dicha reserva:\n\n";
        } else {
            contenido = "Sr/a " + c.getNombre() + " " + c.getApellido() + " " + "a continuación se adjuntan los detalles de su reserva:\n\n";
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        DateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
        Format f = new SimpleDateFormat("MMMM");
        String mes = f.format(t.getFecha());
        String fecha = fechaFormat.format(t.getFecha());
        String[] array = fecha.split("-");
        String dia = array[2];
        String anio = array[0];
        String hora = dateFormat.format(t.getHora());

        //String contenido = "Sr/a " + c.getNombre() + " " + c.getApellido() + " " + "a continuación se adjuntan los detalles de su reserva:\n\n";
        contenido += "Día: " + dia + " de " + mes + " del " + anio + "\n";
        contenido += "Hora: " + hora + "hs\n";
        contenido += "Tipo: Atención\n";
        contenido += "Médico: " + medico.getNombre() + " " + medico.getApellido() + "\n";
        contenido += "Especialidad: " + t.getEspecialidad() + "\n";
        contenido += "Hospital: " + hospital + "\n\n";
        contenido += "Te esperamos, Hospital Web.";

        enviar(c.getUsuario().getCorreo(), asunto, contenido);
    }

    public static void enviar(String destino, String asunto, String contenido) {
        Properties p = System.getProperties();
        p.setProperty("mail.smtp.host", HOST);
        p.setProperty("mail.smtp.port", PORT);
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.smtp.auth", "true");
        Session s = Session.getDefaultInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                try {
                    return new PasswordAuthentication(CORREO, PASS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
        try {
            MimeMessage m = new MimeMessage(s);
            m.setFrom(new InternetAddress(CORREO, NOMBRE));
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
            m.setSubject(asunto);
            m.setText(contenido);
            Transport.send(m);
        } catch (Exception e) {
            System.err.println("Ups, no se pudo mandar");
        }
    }
}
