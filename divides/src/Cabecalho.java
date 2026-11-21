import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

// Faixa azul do topo, com título, subtítulo e botões à direita.
public class Cabecalho extends JPanel {
    private final JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));

    public Cabecalho(String titulo, String subtitulo) {
        setLayout(new BorderLayout());
        setBackground(Cores.AZUL);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setForeground(new Color(195, 211, 240));

        textos.add(lblTitulo);
        textos.add(lblSub);
        botoes.setOpaque(false);

        add(textos, BorderLayout.WEST);
        add(botoes, BorderLayout.EAST);
    }

    public void adicionarBotao(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(acao);
        botoes.add(botao);
    }
}
