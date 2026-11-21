import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// Tabela Usuarios (id, nome, email, senha, status).
public class Usuario implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int id;
    private String nome;
    private String email;
    private String senha;      // guarda o hash SHA-256, não a senha
    private String status = "ativo";

    public Usuario(String nome, String email, String senha) {
        this.id = Sequencia.proximo();
        this.nome = nome;
        this.email = email.trim().toLowerCase();
        this.senha = gerarHash(this.email, senha);
    }

    public boolean senhaConfere(String senha) {
        return this.senha.equals(gerarHash(email, senha));
    }

    // Só para estudo: em um sistema real use bcrypt/argon2 no servidor.
    private static String gerarHash(String email, String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((email + "::" + senha).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
}
