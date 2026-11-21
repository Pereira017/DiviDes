import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AbaMembros extends JPanel {

    public AbaMembros(Janela janela, Evento grupo) {
        Banco banco = janela.getBanco();
        Usuario eu = janela.getUsuarioLogado();
        boolean admin = grupo.ehAdmin(eu.getId());
        // Com o evento encerrado, ninguém inclui nem remove membros.
        boolean podeEditar = admin && !grupo.isEncerrado();

        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        // --- Topo: só o administrador inclui membros ---
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (podeEditar) {
            JTextField campoEmail = new JTextField(22);
            JLabel mensagem = new JLabel(" ");
            mensagem.setForeground(Cores.VERMELHO);
            JButton botaoIncluir = new JButton("Incluir membro");
            botaoIncluir.addActionListener(e -> {
                String email = campoEmail.getText().trim().toLowerCase();
                if (email.isEmpty()) {
                    mensagem.setText("Informe o e-mail.");
                    return;
                }
                Usuario u = banco.buscarPorEmail(email);
                if (u == null) {
                    mensagem.setText("Usuário não encontrado. A pessoa precisa criar uma conta antes.");
                    return;
                }
                if (grupo.ehMembro(u.getId())) {
                    mensagem.setText("Essa pessoa já está no evento.");
                    return;
                }
                banco.adicionarMembro(grupo, u);
                janela.atualizarGrupo(grupo, 0);
            });
            topo.add(new JLabel("E-mail do usuário:"));
            topo.add(campoEmail);
            topo.add(botaoIncluir);
            topo.add(mensagem);
        } else if (grupo.isEncerrado()) {
            topo.add(new JLabel("Evento encerrado: não é possível incluir ou remover membros."));
        } else {
            topo.add(new JLabel("Somente o administrador do evento pode incluir e remover membros."));
        }
        add(topo, BorderLayout.NORTH);

        // --- Lista de membros ---
        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 6));
        for (int id : grupo.getMembroIds()) {
            Usuario u = banco.buscarPorId(id);
            String nome = u != null ? u.getNome() : "Usuário removido";
            String email = u != null ? u.getEmail() : "";
            String voce = id == eu.getId() ? " (você)" : "";
            String papel = grupo.ehAdmin(id) ? "Administrador" : "Membro";

            JPanel linha = new JPanel(new BorderLayout(10, 0));
            linha.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(8, 10, 8, 10)));
            linha.add(new JLabel("<html><b>" + Formato.escapar(nome) + "</b>" + voce + "<br>" + email + "</html>"),
                    BorderLayout.CENTER);

            JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            direita.setOpaque(false);
            direita.add(new JLabel(papel));

            if (podeEditar && !grupo.ehAdmin(id)) {
                JButton botaoRemover = new JButton("Remover");
                botaoRemover.addActionListener(e -> {
                    int r = JOptionPane.showConfirmDialog(this,
                            "Remover " + nome + "? As despesas e obrigações dessa pessoa também serão excluídas.",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        grupo.removerMembro(id);
                        janela.atualizarGrupo(grupo, 0);
                    }
                });
                direita.add(botaoRemover);
            }
            linha.add(direita, BorderLayout.EAST);
            lista.add(linha);
        }
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(lista, BorderLayout.NORTH);
        add(new JScrollPane(envoltorio), BorderLayout.CENTER);
    }
}
