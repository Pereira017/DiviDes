import java.util.*;

// Contas do app: quanto cada pessoa tem a receber e a pagar (só obrigações pendentes).
public class Calculadora {

    public record Saldo(int usuarioId, String nome, double aReceber, double aPagar) {
        public double liquido() {
            return aReceber - aPagar;
        }
    }

    public static List<Saldo> saldos(Evento evento, Banco banco) {
        Map<Integer, Double> receber = new LinkedHashMap<>();
        Map<Integer, Double> pagar = new LinkedHashMap<>();
        for (int id : evento.getMembroIds()) {
            receber.put(id, 0.0);
            pagar.put(id, 0.0);
        }
        for (Obrigacao o : evento.getObrigacoes()) {
            if (o.isPaga()) continue;
            Despesa d = evento.despesaDe(o);
            if (d == null) continue;
            pagar.merge(o.getDevedorId(), o.getValor(), Double::sum);
            receber.merge(d.getMembroId(), o.getValor(), Double::sum);
        }
        List<Saldo> lista = new ArrayList<>();
        for (int id : evento.getMembroIds()) {
            lista.add(new Saldo(id, banco.nomeDoUsuario(id), receber.get(id), pagar.get(id)));
        }
        return lista;
    }

    public static Map<String, Double> totalPorTipo(Evento evento) {
        Map<String, Double> totais = new LinkedHashMap<>();
        for (Tipo t : Tipo.values()) {
            double soma = 0;
            for (Despesa d : evento.getDespesas()) {
                if (d.getTipo() == t) soma += d.getValor();
            }
            if (soma > 0) totais.put(t.getRotulo(), soma);
        }
        return totais;
    }
}
