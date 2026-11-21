import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PainelGrupos extends JPanel {

    public PainelGrupos(Janela janela) {
        Banco banco = janela.getBanco();
        Usuario eu = janela.getUsuarioLogado();
        setLayout(new BorderLayout());

        Cabecalho cabecalho = new Cabecalho("Meus eventos", "Olá, " + eu.getNome() + " (" + eu.getEmail() + ")");
        cabecalho.adicionarBotao("Sair", e -> janela.mostrarLogin());
        add(cabecalho, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(0, 14));
        corpo.setBorder(new EmptyBorder(16, 16, 16, 16));

        // --- Criar novo evento ---
        JPanel novo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        novo.setBorder(BorderFactory.createTitledBorder("Novo evento (você será o administrador)"));
        JTextField campoNome = new JTextField(18);
        JComboBox<String> comboMoeda = new JComboBox<>(new String[]{"BRL", "USD", "EUR", "ARS"});
        JButton botaoCriar = new JButton("Criar evento");
        botaoCriar.addActionListener(e -> {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dê um nome para o evento.");
                return;
            }
            Evento g = banco.criarEvento(nome, (String) comboMoeda.getSelectedItem(), eu);
            banco.salvar();
            janela.mostrarGrupo(g, 1);
        });
        novo.add(new JLabel("Nome:"));
        novo.add(campoNome);
        novo.add(new JLabel("Moeda:"));
        novo.add(comboMoeda);
        novo.add(botaoCriar);
        corpo.add(novo, BorderLayout.NORTH);

        // --- Lista de eventos em que a pessoa participa ---
        List<Evento> meus = banco.eventosDe(eu);
        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 8));
        if (meus.isEmpty()) {
            lista.add(new JLabel("Você ainda não participa de nenhum evento. Crie um acima ou peça ao administrador de um evento para incluir seu e-mail."));
        }
        for (Evento g : meus) {
            String papel = g.ehAdmin(eu.getId()) ? "Administrador" : "Membro";
            String status = g.isEncerrado()
                    ? "<font color='#808080'>Encerrado</font>"
                    : "<font color='#1F9D6B'>Ativo</font>";
            String texto = "<html><b>" + Formato.escapar(g.getNome()) + "</b> (" + papel + ") · " + status + "<br>"
                    + g.getMembroIds().size() + " membros · " + g.getDespesas().size() + " despesas · "
                    + Formato.moeda(g.total(), g.getMoeda()) + "</html>";
            JButton botao = new JButton(texto);
            botao.setHorizontalAlignment(SwingConstants.LEFT);
            botao.addActionListener(e -> janela.mostrarGrupo(g, 1));
            lista.add(botao);
        }
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(lista, BorderLayout.NORTH);
        corpo.add(new JScrollPane(envoltorio), BorderLayout.CENTER);

        add(corpo, BorderLayout.CENTER);
    }
}
