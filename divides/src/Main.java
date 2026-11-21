import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Janela janela = new Janela(Banco.carregar());
            janela.setVisible(true);
        });
    }
}
