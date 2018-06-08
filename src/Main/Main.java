package Main;

import Controladores.Singleton;
import java.text.ParseException;
import java.util.Timer;

/**
 *
 * @author Jorge
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, ParseException {
        Singleton.getInstance().getEntity().getTransaction();
        // Timer time = new Timer();
        //TareasProgramadas tp = new TareasProgramadas();
        //time.schedule(tp, 0, 10000);// cada 10seg

    }

}
