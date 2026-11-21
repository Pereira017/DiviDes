import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Formato {

    public static String moeda(double valor, String codigoMoeda) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        nf.setCurrency(Currency.getInstance(codigoMoeda));
        return nf.format(valor);
    }

    // Evita que um texto digitado com < ou & quebre os rótulos que usam HTML.
    public static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
