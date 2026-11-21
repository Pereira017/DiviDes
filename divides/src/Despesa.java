import java.io.Serializable;
import java.time.LocalDateTime;

// Tabela Despesas (cod, nome, valor, data_hora, membro_id, evento_cod, tipo_cod, conta_id).
public class Despesa implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int cod;
    private String nome;
    private double valor;
    private final LocalDateTime dataHora = LocalDateTime.now();
    private int membroId;        // quem pagou (credor)
    private final int eventoCod;
    private int tipoCod;
    private final int contaId;

    // Anexo (foto ou PDF): não está no banco, fica no arquivo da pasta "anexos".
    private String anexoArquivo;
    private String anexoNome;

    public Despesa(String nome, double valor, Tipo tipo, int membroId, int eventoCod, int contaId) {
        this.cod = Sequencia.proximo();
        this.nome = nome;
        this.valor = valor;
        this.tipoCod = tipo.getCod();
        this.membroId = membroId;
        this.eventoCod = eventoCod;
        this.contaId = contaId;
    }

    void atualizar(String nome, double valor, Tipo tipo, int membroId) {
        this.nome = nome;
        this.valor = valor;
        this.tipoCod = tipo.getCod();
        this.membroId = membroId;
    }

    public boolean temAnexo() { return anexoArquivo != null; }

    public void setAnexo(String arquivo, String nomeOriginal) {
        this.anexoArquivo = arquivo;
        this.anexoNome = nomeOriginal;
    }

    public int getCod() { return cod; }
    public String getNome() { return nome; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public int getMembroId() { return membroId; }
    public int getEventoCod() { return eventoCod; }
    public int getTipoCod() { return tipoCod; }
    public Tipo getTipo() { return Tipo.porCod(tipoCod); }
    public int getContaId() { return contaId; }
    public String getAnexoArquivo() { return anexoArquivo; }
    public String getAnexoNome() { return anexoNome; }
}
