import java.io.Serializable;
import java.time.LocalDateTime;

// Tabela Contas: criada para cada despesa. As pessoas dessa conta devem sua parte (Obrigacao).
public class Conta implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int id;
    private double valor;
    private final LocalDateTime dataHora = LocalDateTime.now();

    public Conta(double valor) {
        this.id = Sequencia.proximo();
        this.valor = valor;
    }

    public int getId() { return id; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setValor(double valor) { this.valor = valor; }
}
