import java.io.Serializable;

// Tabela Membros: a pessoa como participante. Não tem papel fixo: quem é administrador
// é definido por evento (Eventos.administrador_id). O id do membro é o id do usuário.
public class Membro implements Serializable {
    private static final long serialVersionUID = 2L;

    private final int usuarioId;

    public Membro(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getId() { return usuarioId; }
    public int getUsuarioId() { return usuarioId; }
}
