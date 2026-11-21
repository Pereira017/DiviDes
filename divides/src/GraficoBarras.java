import javax.swing.JPanel;
import java.awt.*;
import java.util.Map;

// Gráfico de barras horizontais desenhado à mão (sem biblioteca externa).
public class GraficoBarras extends JPanel {
    private final Map<String, Double> valores;
    private final String moeda;

    public GraficoBarras(Map<String, Double> valores, String moeda) {
        this.valores = valores;
        this.moeda = moeda;
        setPreferredSize(new Dimension(500, valores.size() * 36 + 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double maximo = 1;
        for (double v : valores.values()) maximo = Math.max(maximo, v);

        int larguraMaxima = Math.max(getWidth() - 300, 50);
        int y = 20;
        for (Map.Entry<String, Double> item : valores.entrySet()) {
            int largura = (int) (larguraMaxima * item.getValue() / maximo);

            g2.setColor(getForeground());
            g2.drawString(item.getKey(), 10, y + 16);

            g2.setColor(Cores.AZUL);
            g2.fillRoundRect(120, y, Math.max(largura, 2), 22, 6, 6);

            g2.setColor(getForeground());
            g2.drawString(Formato.moeda(item.getValue(), moeda), 128 + largura, y + 16);
            y += 36;
        }
    }
}
