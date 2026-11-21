import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class AbaResumo extends JPanel {

    public AbaResumo(Janela janela, Evento grupo) {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        if (grupo.getDespesas().isEmpty()) {
            add(new JLabel("Registre alguns gastos para ver o resumo do evento."), BorderLayout.NORTH);
            return;
        }

        JLabel total = new JLabel("Total gasto: " + Formato.moeda(grupo.total(), grupo.getMoeda())
                + "  ·  " + grupo.getDespesas().size() + " despesas");
        total.setFont(total.getFont().deriveFont(Font.BOLD, 16f));
        add(total, BorderLayout.NORTH);

        JPanel graficoComTitulo = new JPanel(new BorderLayout(0, 8));
        JLabel titulo = new JLabel("Gastos por categoria");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        Map<String, Double> porCategoria = Calculadora.totalPorTipo(grupo);
        graficoComTitulo.add(titulo, BorderLayout.NORTH);
        graficoComTitulo.add(new GraficoBarras(porCategoria, grupo.getMoeda()), BorderLayout.CENTER);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(graficoComTitulo, BorderLayout.NORTH);
        add(new JScrollPane(envoltorio), BorderLayout.CENTER);
    }
}
