import java.io.Serializable;

// Tabela Obrigacoes: a parte que um devedor deve de uma despesa. Só existe pago ou não pago.
public class Obrigacao implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int cod;
    private final int despesaCod;
    private final int devedorId;
    private double valor;
    private String status = "pendente";   // "pendente" ou "paga"

    public Obrigacao(int despesaCod, int devedorId, double valor) {
        this.cod = Sequencia.proximo();
        this.despesaCod = despesaCod;
        this.devedorId = devedorId;
        this.valor = valor;
    }

    public boolean isPaga() { return "paga".equals(status); }
    public void marcarPaga() { status = "paga"; }
    public void desfazerBaixa() { status = "pendente"; }

    public int getCod() { return cod; }
    public int getDespesaCod() { return despesaCod; }
    public int getDevedorId() { return devedorId; }
    public double getValor() { return valor; }
    public String getStatus() { return status; }
}
