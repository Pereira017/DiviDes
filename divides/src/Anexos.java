import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// Guarda os anexos (foto/PDF) na pasta "anexos", ao lado do dados.bin.
public class Anexos {
    private static final String PASTA = "anexos";
    public static final long TAMANHO_MAXIMO = 10L * 1024 * 1024; // 10 MB

    // Copia o arquivo escolhido para a pasta do app e devolve o nome guardado.
    public static String copiar(File origem, String despesaId) throws IOException {
        Files.createDirectories(Paths.get(PASTA));
        String nome = origem.getName();
        int ponto = nome.lastIndexOf('.');
        String extensao = ponto >= 0 ? nome.substring(ponto).toLowerCase() : "";
        String guardado = despesaId + extensao;
        Files.copy(origem.toPath(), Paths.get(PASTA, guardado), StandardCopyOption.REPLACE_EXISTING);
        return guardado;
    }

    public static File arquivo(String guardado) {
        return new File(PASTA, guardado);
    }

    public static void apagar(String guardado) {
        if (guardado != null) arquivo(guardado).delete();
    }

    // Abre o arquivo no programa padrão do computador (visualizador de imagem, leitor de PDF...).
    public static void abrir(String guardado) throws IOException {
        File f = arquivo(guardado);
        if (!f.exists()) throw new IOException("Arquivo não encontrado: " + f.getPath());
        if (!Desktop.isDesktopSupported()) throw new IOException("Este sistema não permite abrir arquivos.");
        Desktop.getDesktop().open(f);
    }
}
