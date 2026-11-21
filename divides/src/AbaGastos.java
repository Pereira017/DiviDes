import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbaGastos extends JPanel {
    private final Janela janela;
    private final Evento grupo;
    private final Banco banco;
    private final Usuario eu;
    private final boolean admin;
    private final List<Integer> membroIds;

    // Campos do formulário
    private final JTextField campoDescricao = new JTextField(18);
    private final JTextField campoValor = new JTextField(8);
    private final JComboBox<String> comboPagador = new JComboBox<>();
    private final JComboBox<String> comboTipo = new JComboBox<>();
    private final List<JCheckBox> checks = new ArrayList<>();
    private final JLabel lblAnexo = new JLabel("nenhum");
    private final JLabel mensagem = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Registrar gasto");
    private final JButton botaoCancelar = new JButton("Cancelar edição");

    private Despesa editando = null;   // null = cadastrando uma despesa nova
    private File anexoNovo = null;     // arquivo escolhido que ainda não foi copiado
    private boolean removerAnexo = false;

    public AbaGastos(Janela janela, Evento grupo) {
        this.janela = janela;
        this.grupo = grupo;
        this.banco = janela.getBanco();
        this.eu = janela.getUsuarioLogado();
        this.admin = grupo.ehAdmin(eu.getId());
        this.membroIds = grupo.getMembroIds();

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        if (grupo.isEncerrado()) {
            // Evento encerrado: sem formulário, só a lista.
            JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT));
            aviso.add(new JLabel("Evento encerrado: não é possível registrar, editar ou excluir gastos."));
            add(aviso, BorderLayout.NORTH);
        } else {
            add(montarFormulario(), BorderLayout.NORTH);
        }
        add(montarLista(), BorderLayout.CENTER);
    }

    private String nomeDoMembro(int membroId) {
        return banco.nomeDoUsuario(membroId);
    }

    private int indiceDoMembro(int membroId) {
        return Math.max(membroIds.indexOf(membroId), 0);
    }

    private JPanel montarFormulario() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder("Gasto"));

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha1.add(new JLabel("Descrição:"));
        linha1.add(campoDescricao);
        linha1.add(new JLabel("Valor (" + grupo.getMoeda() + "):"));
        linha1.add(campoValor);

        for (int id : membroIds) {
            comboPagador.addItem(nomeDoMembro(id));
        }
        // Membro comum só registra o que ele mesmo pagou.
        comboPagador.setSelectedIndex(indiceDoMembro(eu.getId()));
        comboPagador.setEnabled(admin);

        for (Tipo t : Tipo.values()) {
            comboTipo.addItem(t.getRotulo());
        }

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha2.add(new JLabel("Quem pagou:"));
        linha2.add(comboPagador);
        linha2.add(new JLabel("Tipo:"));
        linha2.add(comboTipo);

        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha3.add(new JLabel("Dividir entre:"));
        for (int id : membroIds) {
            JCheckBox check = new JCheckBox(nomeDoMembro(id), true);
            checks.add(check);
            linha3.add(check);
        }

        // Anexo (foto ou PDF)
        JButton botaoEscolher = new JButton("Escolher arquivo...");
        JButton botaoTirar = new JButton("Remover anexo");
        botaoEscolher.addActionListener(e -> escolherArquivo());
        botaoTirar.addActionListener(e -> {
            anexoNovo = null;
            removerAnexo = true;
            lblAnexo.setText("nenhum");
        });
        JPanel linhaAnexo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaAnexo.add(new JLabel("Anexo (foto ou PDF):"));
        linhaAnexo.add(lblAnexo);
        linhaAnexo.add(botaoEscolher);
        linhaAnexo.add(botaoTirar);

        mensagem.setForeground(Cores.VERMELHO);
        botaoCancelar.setVisible(false);
        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> janela.mostrarGrupo(grupo, 1));

        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaBotoes.add(botaoSalvar);
        linhaBotoes.add(botaoCancelar);
        linhaBotoes.add(mensagem);

        form.add(linha1);
        form.add(linha2);
        form.add(linha3);
        form.add(linhaAnexo);
        form.add(linhaBotoes);
        return form;
    }

    private void escolherArquivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens e PDF", "jpg", "jpeg", "png", "gif", "pdf"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File arquivo = chooser.getSelectedFile();
        if (arquivo.length() > Anexos.TAMANHO_MAXIMO) {
            mensagem.setText("O arquivo é maior que 10 MB.");
            return;
        }
        anexoNovo = arquivo;
        removerAnexo = false;
        lblAnexo.setText(arquivo.getName());
        mensagem.setText(" ");
    }

    private void salvar() {
        String descricao = campoDescricao.getText().trim();
        double valor = 0;
        try {
            valor = Double.parseDouble(campoValor.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            // valor continua 0 e cai na validação abaixo
        }

        List<Integer> participantes = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i).isSelected()) participantes.add(membroIds.get(i));
        }
        int pagadorId = membroIds.get(comboPagador.getSelectedIndex());
        Tipo tipo = Tipo.values()[comboTipo.getSelectedIndex()];

        if (descricao.isEmpty()) { mensagem.setText("Dê um nome para o gasto."); return; }
        if (valor <= 0) { mensagem.setText("Informe um valor válido."); return; }
        if (participantes.isEmpty()) { mensagem.setText("Escolha ao menos uma pessoa para dividir."); return; }
        if (!admin && pagadorId != eu.getId()) {
            mensagem.setText("Como membro, você só registra despesas pagas por você.");
            return;
        }

        try {
            if (editando != null) {
                if (!grupo.podeMexer(eu.getId(), editando)) {
                    mensagem.setText("Você só pode editar as despesas que você pagou.");
                    return;
                }
                grupo.atualizarDespesa(editando, descricao, valor, tipo, pagadorId, participantes);
                aplicarAnexo(editando);
            } else {
                Despesa nova = grupo.registrarDespesa(descricao, valor, tipo, pagadorId, participantes);
                aplicarAnexo(nova);
            }
        } catch (IllegalStateException ex) {
            mensagem.setText(ex.getMessage());
            return;
        } catch (IOException ex) {
            mensagem.setText("Não foi possível salvar o anexo: " + ex.getMessage());
            return;
        }
        janela.atualizarGrupo(grupo, 1);
    }

    // Copia o arquivo novo (se houver) ou remove o anexo atual (se o usuário pediu).
    private void aplicarAnexo(Despesa despesa) throws IOException {
        if (anexoNovo != null) {
            String antigo = despesa.getAnexoArquivo();
            String guardado = Anexos.copiar(anexoNovo, String.valueOf(despesa.getCod()));
            if (antigo != null && !antigo.equals(guardado)) Anexos.apagar(antigo);
            despesa.setAnexo(guardado, anexoNovo.getName());
        } else if (removerAnexo) {
            Anexos.apagar(despesa.getAnexoArquivo());
            despesa.setAnexo(null, null);
        }
    }

    private void iniciarEdicao(Despesa d) {
        editando = d;
        anexoNovo = null;
        removerAnexo = false;
        campoDescricao.setText(d.getNome());
        campoValor.setText(String.valueOf(d.getValor()));
        comboPagador.setSelectedIndex(indiceDoMembro(d.getMembroId()));
        comboTipo.setSelectedItem(d.getTipo().getRotulo());
        List<Integer> participantes = grupo.participantesDe(d);
        for (int i = 0; i < checks.size(); i++) {
            checks.get(i).setSelected(participantes.contains(membroIds.get(i)));
        }
        lblAnexo.setText(d.temAnexo() ? d.getAnexoNome() : "nenhum");
        botaoSalvar.setText("Salvar alterações");
        botaoCancelar.setVisible(true);
        mensagem.setText(" ");
    }

    private JComponent montarLista() {
        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 6));
        if (grupo.getDespesas().isEmpty()) {
            lista.add(new JLabel("Nenhum gasto registrado."));
        }
        for (Despesa d : grupo.getDespesas()) {
            int participantes = grupo.participantesDe(d).size();
            double porPessoa = d.getValor() / participantes;
            String texto = "<html><b>" + Formato.escapar(d.getNome()) + "</b> · " + d.getTipo().getRotulo() + "<br>"
                    + nomeDoMembro(d.getMembroId()) + " pagou · dividido entre " + participantes
                    + " (" + Formato.moeda(porPessoa, grupo.getMoeda()) + " cada)";
            if (d.temAnexo()) {
                texto += "<br>Anexo: " + Formato.escapar(d.getAnexoNome());
            }
            texto += "</html>";

            JPanel linha = new JPanel(new BorderLayout(10, 0));
            linha.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(8, 10, 8, 10)));
            linha.add(new JLabel(texto), BorderLayout.CENTER);

            JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            direita.setOpaque(false);
            JLabel valor = new JLabel(Formato.moeda(d.getValor(), grupo.getMoeda()));
            valor.setFont(valor.getFont().deriveFont(Font.BOLD));
            direita.add(valor);

            // Todos do evento podem ver o anexo.
            if (d.temAnexo()) {
                JButton botaoAnexo = new JButton("Ver anexo");
                botaoAnexo.addActionListener(e -> {
                    try {
                        Anexos.abrir(d.getAnexoArquivo());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                });
                direita.add(botaoAnexo);
            }

            if (!grupo.isEncerrado() && grupo.podeMexer(eu.getId(), d)) {
                JButton botaoEditar = new JButton("Editar");
                JButton botaoExcluir = new JButton("Excluir");
                botaoEditar.addActionListener(e -> iniciarEdicao(d));
                botaoExcluir.addActionListener(e -> {
                    int r = JOptionPane.showConfirmDialog(this, "Excluir \"" + d.getNome() + "\"?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        try {
                            grupo.excluirDespesa(d);
                            janela.atualizarGrupo(grupo, 1);
                        } catch (IllegalStateException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não é possível excluir",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
                direita.add(botaoEditar);
                direita.add(botaoExcluir);
            }
            linha.add(direita, BorderLayout.EAST);
            lista.add(linha);
        }
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(lista, BorderLayout.NORTH);
        return new JScrollPane(envoltorio);
    }
}
