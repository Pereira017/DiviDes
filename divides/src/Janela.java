import javax.swing.JFrame;
import javax.swing.JPanel;

// Janela única do app. Para trocar de tela, ela troca o painel de dentro.
public class Janela extends JFrame {
    private final Banco banco;
    private Usuario usuarioLogado;

    public Janela(Banco banco) {
        super("DiviDes");
        this.banco = banco;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 620);
        setLocationRelativeTo(null);
        mostrarLogin();
    }

    public void mostrarLogin() {
        usuarioLogado = null;
        trocarTela(new PainelLogin(this));
    }

    public void mostrarGrupos() {
        trocarTela(new PainelGrupos(this));
    }

    // aba: 0 = Membros, 1 = Gastos, 2 = Saldos, 3 = Resumo
    public void mostrarGrupo(Evento grupo, int aba) {
        trocarTela(new PainelGrupo(this, grupo, aba));
    }

    // Salva e redesenha o grupo (chamado depois de qualquer alteração).
    public void atualizarGrupo(Evento grupo, int aba) {
        banco.salvar();
        mostrarGrupo(grupo, aba);
    }

    private void trocarTela(JPanel painel) {
        setContentPane(painel);
        revalidate();
        repaint();
    }

    public Banco getBanco() { return banco; }
    public Usuario getUsuarioLogado() { return usuarioLogado; }
    public void setUsuarioLogado(Usuario usuario) { this.usuarioLogado = usuario; }
}
