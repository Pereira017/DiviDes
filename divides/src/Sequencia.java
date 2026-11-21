// Gera os códigos (cod/id) das tabelas, como um campo serial do banco.
public class Sequencia {
    private static int ultimo = 0;

    public static synchronized int proximo() {
        return ++ultimo;
    }

    static int atual() {
        return ultimo;
    }

    // Ao carregar o banco, continua a numeração de onde parou.
    static void restaurar(int valor) {
        if (valor > ultimo) ultimo = valor;
    }
}
