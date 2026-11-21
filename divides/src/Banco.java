import java.io.*;
import java.util.ArrayList;
import java.util.List;

// "Banco de dados" do app: guarda tudo em um arquivo (dados.bin) usando serialização.
// Não está ligado a nenhum banco externo.
public class Banco implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final String ARQUIVO = "dados.bin";

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Membro> membros = new ArrayList<>();
    private List<Evento> eventos = new ArrayList<>();
    private int ultimoCod = 0;

    public static Banco carregar() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new Banco();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            Banco b = (Banco) in.readObject();
            Sequencia.restaurar(b.ultimoCod);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return new Banco();
        }
    }

    public void salvar() {
        this.ultimoCod = Sequencia.atual();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- Usuários ----

    public Usuario buscarPorEmail(String email) {
        String em = email.trim().toLowerCase();
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(em)) return u;
        }
        return null;
    }

    public Usuario buscarPorId(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public String nomeDoUsuario(int id) {
        Usuario u = buscarPorId(id);
        return u != null ? u.getNome() : "Usuário removido";
    }

    // ---- Membros e eventos ----

    // Garante que a pessoa existe na tabela Membros.
    private Membro garantirMembro(int usuarioId) {
        for (Membro m : membros) {
            if (m.getUsuarioId() == usuarioId) return m;
        }
        Membro novo = new Membro(usuarioId);
        membros.add(novo);
        return novo;
    }

    public Evento criarEvento(String nome, String moeda, Usuario criador) {
        Evento e = new Evento(nome, moeda, criador);
        garantirMembro(criador.getId());
        eventos.add(e);
        return e;
    }

    public void adicionarMembro(Evento evento, Usuario usuario) {
        garantirMembro(usuario.getId());
        evento.incluirMembro(usuario.getId());
    }

    // Eventos em que a pessoa participa (como administrador ou como membro).
    public List<Evento> eventosDe(Usuario usuario) {
        List<Evento> resultado = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.ehMembro(usuario.getId())) resultado.add(e);
        }
        return resultado;
    }

    // Apaga o evento e os anexos das despesas dele.
    public void excluirEvento(Evento evento) {
        for (Despesa d : evento.getDespesas()) {
            Anexos.apagar(d.getAnexoArquivo());
        }
        eventos.remove(evento);
    }

    // Dados de exemplo para testar. Senha de todos: 123456
    public void criarExemplo() {
        Usuario ana = new Usuario("Ana Souza", "ana@exemplo.com", "123456");
        Usuario bruno = new Usuario("Bruno Lima", "bruno@exemplo.com", "123456");
        Usuario carla = new Usuario("Carla Mendes", "carla@exemplo.com", "123456");
        Usuario diego = new Usuario("Diego Rocha", "diego@exemplo.com", "123456");
        usuarios.add(ana);
        usuarios.add(bruno);
        usuarios.add(carla);
        usuarios.add(diego);

        Evento e = criarEvento("Viagem para Bonito", "BRL", ana);
        adicionarMembro(e, bruno);
        adicionarMembro(e, carla);
        adicionarMembro(e, diego);

        List<Integer> todos = new ArrayList<>(List.of(ana.getId(), bruno.getId(), carla.getId(), diego.getId()));
        List<Integer> tres = new ArrayList<>(List.of(ana.getId(), bruno.getId(), carla.getId()));

        e.registrarDespesa("Compras no mercado", 180, Tipo.ALIMENTACAO, diego.getId(), todos);
        e.registrarDespesa("Combustível", 300, Tipo.TRANSPORTE, diego.getId(), todos);
        e.registrarDespesa("Passeio Gruta do Lago Azul", 400, Tipo.PASSEIO, carla.getId(), tres);
        e.registrarDespesa("Jantar de chegada", 250, Tipo.ALIMENTACAO, bruno.getId(), todos);
        e.registrarDespesa("Pousada (4 diárias)", 1800, Tipo.HOSPEDAGEM, ana.getId(), todos);
        salvar();
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Membro> getMembros() { return membros; }
    public List<Evento> getEventos() { return eventos; }
}
