import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AbaSaldos extends JPanel {

    public AbaSaldos(Janela janela, Evento grupo) {
        Banco banco = janela.getBanco();
        Usuario eu = janela.getUsuarioLogado();
        String moeda = grupo.getMoeda();
        List<Calculadora.Saldo> saldos = Calculadora.saldos(grupo, banco);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));

        // --- Saldo de cada pessoa (só obrigações pendentes) ---
        conteudo.add(titulo("Saldo de cada pessoa"));
        JPanel painelSaldos = new JPanel(new GridLayout(0, 1, 0, 6));
        for (Calculadora.Saldo s : saldos) {
            double liquido = s.liquido();
            String situacao;
            Color cor;
            if (Math.abs(liquido) < 0.005) {
                situacao = "Quitado";
                cor = Color.GRAY;
            } else if (liquido > 0) {
                situacao = "recebe " + Formato.moeda(liquido, moeda);
                cor = Cores.VERDE;
            } else {
                situacao = "deve " + Formato.moeda(-liquido, moeda);
                cor = Cores.VERMELHO;
            }
            String detalhe = "a pagar " + Formato.moeda(s.aPagar(), moeda) + " · a receber " + Formato.moeda(s.aReceber(), moeda);

            JPanel linha = linhaComBorda();
            linha.add(new JLabel("<html><b>" + Formato.escapar(s.nome()) + "</b><br>" + detalhe + "</html>"), BorderLayout.CENTER);
            JLabel lblSituacao = new JLabel(situacao);
            lblSituacao.setForeground(cor);
            lblSituacao.setFont(lblSituacao.getFont().deriveFont(Font.BOLD));
            linha.add(lblSituacao, BorderLayout.EAST);
            painelSaldos.add(linha);
        }
        conteudo.add(painelSaldos);

        // --- Obrigações pendentes ---
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(titulo("Pendentes"));
        JPanel painelPendentes = new JPanel(new GridLayout(0, 1, 0, 6));
        boolean temPendente = false;
        for (Obrigacao o : grupo.getObrigacoes()) {
            if (o.isPaga()) continue;
            temPendente = true;
            painelPendentes.add(linhaObrigacao(janela, grupo, eu, o, moeda, false));
        }
        if (!temPendente) {
            painelPendentes.add(new JLabel("Nenhuma pendência. Tudo certo."));
        }
        conteudo.add(painelPendentes);

        // --- Obrigações já pagas ---
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(titulo("Já pagas"));
        JPanel painelPagas = new JPanel(new GridLayout(0, 1, 0, 6));
        boolean temPaga = false;
        for (Obrigacao o : grupo.getObrigacoes()) {
            if (!o.isPaga()) continue;
            // A parte de quem pagou a despesa nasce paga e não aparece aqui.
            Despesa d = grupo.despesaDe(o);
            if (d != null && d.getMembroId() == o.getDevedorId()) continue;
            temPaga = true;
            painelPagas.add(linhaObrigacao(janela, grupo, eu, o, moeda, true));
        }
        if (!temPaga) {
            painelPagas.add(new JLabel("Nenhum pagamento registrado ainda."));
        }
        conteudo.add(painelPagas);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(conteudo, BorderLayout.NORTH);
        add(new JScrollPane(envoltorio), BorderLayout.CENTER);
    }

    private JPanel linhaObrigacao(Janela janela, Evento grupo, Usuario eu, Obrigacao o, String moeda, boolean paga) {
        Banco banco = janela.getBanco();
        Despesa d = grupo.despesaDe(o);
        String nomeDevedor = banco.nomeDoUsuario(o.getDevedorId());
        String nomeCredor = d != null ? banco.nomeDoUsuario(d.getMembroId()) : "—";
        String nomeDespesa = d != null ? d.getNome() : "—";

        JPanel linha = linhaComBorda();
        linha.add(new JLabel("<html><b>" + Formato.escapar(nomeDevedor) + "</b>  →  <b>" + Formato.escapar(nomeCredor)
                + "</b><br>" + Formato.escapar(nomeDespesa) + "</html>"), BorderLayout.CENTER);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);
        JLabel valor = new JLabel(Formato.moeda(o.getValor(), moeda));
        valor.setForeground(paga ? Cores.VERDE : Cores.VERMELHO);
        valor.setFont(valor.getFont().deriveFont(Font.BOLD));
        direita.add(valor);

        if (!paga && grupo.podeDarBaixa(eu.getId(), o)) {
            JButton botaoBaixa = new JButton("Dar baixa");
            botaoBaixa.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(this,
                        "Confirmar que " + nomeDevedor + " pagou " + Formato.moeda(o.getValor(), moeda) + "?",
                        "Dar baixa", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    o.marcarPaga();
                    janela.atualizarGrupo(grupo, 2);
                }
            });
            direita.add(botaoBaixa);
        }
        if (paga && grupo.podeDarBaixa(eu.getId(), o)) {
            JButton botaoDesfazer = new JButton("Desfazer");
            botaoDesfazer.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(this,
                        "Desfazer esta baixa? O valor volta a aparecer como pendente.",
                        "Desfazer baixa", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    o.desfazerBaixa();
                    janela.atualizarGrupo(grupo, 2);
                }
            });
            direita.add(botaoDesfazer);
        }
        linha.add(direita, BorderLayout.EAST);
        return linha;
    }

    private JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 16f));
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel linhaComBorda() {
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(8, 10, 8, 10)));
        return linha;
    }
}
