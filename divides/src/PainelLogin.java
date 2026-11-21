import javax.swing.*;
import java.awt.*;

public class PainelLogin extends JPanel {
    private final Janela janela;

    public PainelLogin(Janela janela) {
        this.janela = janela;
        Banco banco = janela.getBanco();
        setLayout(new GridBagLayout());

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("DiviDes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 36f));
        titulo.setForeground(Cores.AZUL);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Divida suas despesas, com mais simplicidade!");
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        JTextField campoEmail = new JTextField(20);
        JPasswordField campoSenha = new JPasswordField(20);
        campoEmail.setMaximumSize(new Dimension(300, 32));
        campoSenha.setMaximumSize(new Dimension(300, 32));

        JLabel mensagem = new JLabel(" ");
        mensagem.setForeground(Cores.VERMELHO);
        mensagem.setAlignmentX(CENTER_ALIGNMENT);

        JButton botaoEntrar = new JButton("Entrar");
        JButton botaoCriar = new JButton("Criar conta");
        botaoEntrar.setAlignmentX(CENTER_ALIGNMENT);
        botaoCriar.setAlignmentX(CENTER_ALIGNMENT);

        botaoEntrar.addActionListener(e -> {
            Usuario u = banco.buscarPorEmail(campoEmail.getText());
            String senha = new String(campoSenha.getPassword());
            if (u == null || !u.senhaConfere(senha)) {
                mensagem.setText("E-mail ou senha incorretos.");
                return;
            }
            janela.setUsuarioLogado(u);
            janela.mostrarGrupos();
        });
        campoSenha.addActionListener(e -> botaoEntrar.doClick());
        botaoCriar.addActionListener(e -> criarConta());

        caixa.add(titulo);
        caixa.add(subtitulo);
        caixa.add(Box.createVerticalStrut(24));
        caixa.add(new JLabel("E-mail"));
        caixa.add(campoEmail);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(new JLabel("Senha"));
        caixa.add(campoSenha);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(mensagem);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(botaoEntrar);
        caixa.add(Box.createVerticalStrut(6));
        caixa.add(botaoCriar);

        // Só aparece enquanto não existe nenhum usuário cadastrado.
        if (banco.getUsuarios().isEmpty()) {
            JButton botaoExemplo = new JButton("Carregar dados de exemplo");
            botaoExemplo.setAlignmentX(CENTER_ALIGNMENT);
            botaoExemplo.addActionListener(e -> {
                banco.criarExemplo();
                botaoExemplo.setVisible(false);
                JOptionPane.showMessageDialog(this,
                    "Exemplo criado. Entre com ana@exemplo.com (administradora)\n"
                    + "ou bruno@exemplo.com (membro). Senha de todos: 123456");
            });
            caixa.add(Box.createVerticalStrut(20));
            caixa.add(botaoExemplo);
        }

        add(caixa);
    }

    private void criarConta() {
        Banco banco = janela.getBanco();
        JTextField nome = new JTextField(18);
        JTextField email = new JTextField(18);
        JPasswordField senha = new JPasswordField(18);
        JPasswordField senha2 = new JPasswordField(18);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nome completo")); form.add(nome);
        form.add(new JLabel("E-mail")); form.add(email);
        form.add(new JLabel("Senha")); form.add(senha);
        form.add(new JLabel("Confirmar senha")); form.add(senha2);

        int opcao = JOptionPane.showConfirmDialog(this, form, "Criar conta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        String erro = null;
        String s1 = new String(senha.getPassword());
        String s2 = new String(senha2.getPassword());
        if (nome.getText().trim().isEmpty()) erro = "Informe seu nome.";
        else if (!email.getText().trim().matches("^\\S+@\\S+\\.\\S+$")) erro = "Informe um e-mail válido.";
        else if (s1.length() < 6) erro = "A senha precisa ter pelo menos 6 caracteres.";
        else if (!s1.equals(s2)) erro = "As senhas não conferem.";
        else if (banco.buscarPorEmail(email.getText()) != null) erro = "Já existe uma conta com esse e-mail.";

        if (erro != null) {
            JOptionPane.showMessageDialog(this, erro, "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario novo = new Usuario(nome.getText().trim(), email.getText(), s1);
        banco.getUsuarios().add(novo);
        banco.salvar();
        janela.setUsuarioLogado(novo);
        janela.mostrarGrupos();
    }
}
