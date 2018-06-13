package Main;

import Controladores.Singleton;
import java.text.ParseException;

/**
 *
 * @author Jorge
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, ParseException {
        Singleton.getInstance().getEntity().getTransaction();

    }

}
