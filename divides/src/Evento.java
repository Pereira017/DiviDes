import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Tabela Eventos (cod, nome, status, data_hora, administrador_id).
// A moeda não está no banco: é um campo extra do app.
public class Evento implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int cod;
    private String nome;
    private String moeda;                    // fora do banco
    private String status = "ativo";         // "ativo" ou "encerrado"
    private final LocalDateTime dataHora = LocalDateTime.now();
    private final int administradorId;       // quem administra este evento
    private final List<Integer> membroIds = new ArrayList<>();   // Membros_Eventos
    private final List<Despesa> despesas = new ArrayList<>();
    private final List<Conta> contas = new ArrayList<>();
    private final List<Obrigacao> obrigacoes = new ArrayList<>();

    // Quem cria o evento vira administrador deste evento (e membro).
    public Evento(String nome, String moeda, Usuario criador) {
        this.cod = Sequencia.proximo();
        this.nome = nome;
        this.moeda = moeda;
        this.administradorId = criador.getId();
        this.membroIds.add(criador.getId());
    }

    // ---- Membros ----

    void incluirMembro(int usuarioId) {
        if (!membroIds.contains(usuarioId)) membroIds.add(usuarioId);
    }

    public boolean ehMembro(int usuarioId) { return membroIds.contains(usuarioId); }

    public boolean ehAdmin(int usuarioId) { return administradorId == usuarioId; }

    // Remove o membro, as despesas que ele pagou, as obrigações dele e as despesas que ficaram sem ninguém.
    public void removerMembro(int usuarioId) {
        membroIds.remove(Integer.valueOf(usuarioId));
        for (Despesa d : new ArrayList<>(despesas)) {
            if (d.getMembroId() == usuarioId) excluirSemChecar(d);
        }
        obrigacoes.removeIf(o -> o.getDevedorId() == usuarioId);
        for (Despesa d : new ArrayList<>(despesas)) {
            if (participantesDe(d).isEmpty()) excluirSemChecar(d);
        }
    }

    // ---- Permissões ----

    // Administrador mexe em tudo; membro só nas despesas que ele pagou.
    public boolean podeMexer(int usuarioId, Despesa despesa) {
        return ehAdmin(usuarioId) || despesa.getMembroId() == usuarioId;
    }

    // Dar baixa: o administrador ou quem recebe (quem pagou a despesa).
    public boolean podeDarBaixa(int usuarioId, Obrigacao obrigacao) {
        Despesa d = despesaDe(obrigacao);
        return ehAdmin(usuarioId) || (d != null && d.getMembroId() == usuarioId);
    }

    // ---- Despesas, contas e obrigações ----

    // Registra a despesa, cria a conta dela e a obrigação de cada participante.
    public Despesa registrarDespesa(String nome, double valor, Tipo tipo, int pagadorId, List<Integer> participantes) {
        Conta conta = new Conta(valor);
        Despesa d = new Despesa(nome, valor, tipo, pagadorId, cod, conta.getId());
        contas.add(conta);
        despesas.add(0, d);
        criarObrigacoes(d, participantes);
        return d;
    }

    public void atualizarDespesa(Despesa d, String nome, double valor, Tipo tipo, int pagadorId, List<Integer> participantes) {
        if (temBaixasRegistradas(d)) {
            throw new IllegalStateException("Há pagamentos registrados nesta despesa. Desfaça as baixas antes de editar.");
        }
        d.atualizar(nome, valor, tipo, pagadorId);
        contaDe(d).setValor(valor);
        obrigacoes.removeIf(o -> o.getDespesaCod() == d.getCod());
        criarObrigacoes(d, participantes);
    }

    public void excluirDespesa(Despesa d) {
        if (temBaixasRegistradas(d)) {
            throw new IllegalStateException("Há pagamentos registrados nesta despesa. Desfaça as baixas antes de excluir.");
        }
        excluirSemChecar(d);
    }

    private void excluirSemChecar(Despesa d) {
        obrigacoes.removeIf(o -> o.getDespesaCod() == d.getCod());
        contas.removeIf(c -> c.getId() == d.getContaId());
        despesas.remove(d);
        Anexos.apagar(d.getAnexoArquivo());
    }

    private void criarObrigacoes(Despesa d, List<Integer> participantes) {
        double parte = d.getValor() / participantes.size();
        for (int id : participantes) {
            Obrigacao o = new Obrigacao(d.getCod(), id, parte);
            if (id == d.getMembroId()) o.marcarPaga();   // quem pagou não deve a si mesmo
            obrigacoes.add(o);
        }
    }

    // Só dá para editar ou excluir se ninguém, exceto quem pagou, já deu baixa.
    public boolean temBaixasRegistradas(Despesa d) {
        for (Obrigacao o : obrigacoesDe(d)) {
            if (o.isPaga() && o.getDevedorId() != d.getMembroId()) return true;
        }
        return false;
    }

    public List<Obrigacao> obrigacoesDe(Despesa d) {
        List<Obrigacao> r = new ArrayList<>();
        for (Obrigacao o : obrigacoes) {
            if (o.getDespesaCod() == d.getCod()) r.add(o);
        }
        return r;
    }

    public List<Integer> participantesDe(Despesa d) {
        List<Integer> r = new ArrayList<>();
        for (Obrigacao o : obrigacoesDe(d)) r.add(o.getDevedorId());
        return r;
    }

    public Despesa despesaDe(Obrigacao o) {
        for (Despesa d : despesas) {
            if (d.getCod() == o.getDespesaCod()) return d;
        }
        return null;
    }

    public Conta contaDe(Despesa d) {
        for (Conta c : contas) {
            if (c.getId() == d.getContaId()) return c;
        }
        return null;
    }

    public double total() {
        double soma = 0;
        for (Despesa d : despesas) soma += d.getValor();
        return soma;
    }

    // ---- Status e dados ----

    public boolean isEncerrado() { return "encerrado".equals(status); }
    public void setEncerrado(boolean encerrado) { this.status = encerrado ? "encerrado" : "ativo"; }

    public int getCod() { return cod; }
    public String getNome() { return nome; }
    public String getMoeda() { return moeda; }
    public String getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public int getAdministradorId() { return administradorId; }
    public List<Integer> getMembroIds() { return membroIds; }
    public List<Despesa> getDespesas() { return despesas; }
    public List<Conta> getContas() { return contas; }
    public List<Obrigacao> getObrigacoes() { return obrigacoes; }
}
