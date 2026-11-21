import javax.swing.*;
import java.awt.*;

// Tela de um evento: cabeçalho + 4 abas.
public class PainelGrupo extends JPanel {

    public PainelGrupo(Janela janela, Evento grupo, int abaInicial) {
        Usuario eu = janela.getUsuarioLogado();
        boolean admin = grupo.ehAdmin(eu.getId());
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Membros", new AbaMembros(janela, grupo));
        abas.addTab("Gastos", new AbaGastos(janela, grupo));
        abas.addTab("Saldos", new AbaSaldos(janela, grupo));
        abas.addTab("Resumo", new AbaResumo(janela, grupo));
        abas.setSelectedIndex(abaInicial);

        String status = grupo.isEncerrado() ? "Encerrado" : "Ativo";
        String papel = admin ? "Administrador" : "Membro";
        String subtitulo = grupo.getMembroIds().size() + " membros · você é " + papel
                + " · moeda " + grupo.getMoeda() + " · " + status;
        Cabecalho cabecalho = new Cabecalho(grupo.getNome(), subtitulo);

        // Só o administrador do evento encerra/reabre e exclui.
        if (admin) {
            String textoEncerrar = grupo.isEncerrado() ? "Reabrir evento" : "Encerrar evento";
            cabecalho.adicionarBotao(textoEncerrar, e -> alternarEncerramento(janela, grupo, abas.getSelectedIndex()));
            cabecalho.adicionarBotao("Excluir evento", e -> excluirGrupo(janela, grupo));
        }
        cabecalho.adicionarBotao("Meus eventos", e -> janela.mostrarGrupos());
        cabecalho.adicionarBotao("Sair", e -> janela.mostrarLogin());

        add(cabecalho, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
    }

    private void alternarEncerramento(Janela janela, Evento grupo, int aba) {
        String pergunta;
        if (grupo.isEncerrado()) {
            pergunta = "Reabrir o evento? Gastos e membros poderão ser alterados de novo.";
        } else {
            pergunta = "Encerrar o evento?\n"
                    + "Não será mais possível registrar ou alterar gastos, nem incluir ou remover membros.\n"
                    + "As baixas de pagamento continuam liberadas. Você pode reabrir depois.";
        }
        int r = JOptionPane.showConfirmDialog(this, pergunta, "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            grupo.setEncerrado(!grupo.isEncerrado());
            janela.atualizarGrupo(grupo, aba);
        }
    }

    private void excluirGrupo(Janela janela, Evento grupo) {
        int r = JOptionPane.showConfirmDialog(this,
                "Excluir o evento \"" + grupo.getNome() + "\"?\n"
                + "Todos os gastos, anexos e pagamentos serão apagados. Não dá para desfazer.",
                "Excluir evento", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            janela.getBanco().excluirEvento(grupo);
            janela.getBanco().salvar();
            janela.mostrarGrupos();
        }
    }
}
