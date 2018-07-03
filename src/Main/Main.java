package Main;

import Clases.TipoTurno;
import Controladores.CHospital;
import Controladores.Singleton;
import java.text.ParseException;

/**
 *
 * @author Jorge
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, ParseException {
        CHospital.obtenerFechasOcupadasJorge(6, 2, TipoTurno.ATENCION);
    }

}
