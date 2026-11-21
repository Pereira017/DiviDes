// Tabela Tipos: cada tipo de despesa tem um código fixo.
public enum Tipo {
    BEBIDA(1, "Bebida"),
    ALIMENTACAO(2, "Alimentação"),
    HOSPEDAGEM(3, "Hospedagem"),
    TRANSPORTE(4, "Transporte"),
    PASSEIO(5, "Passeios"),
    OUTROS(6, "Outros");

    private final int cod;
    private final String rotulo;

    Tipo(int cod, String rotulo) {
        this.cod = cod;
        this.rotulo = rotulo;
    }

    public int getCod() { return cod; }
    public String getRotulo() { return rotulo; }

    public static Tipo porCod(int cod) {
        for (Tipo t : values()) {
            if (t.cod == cod) return t;
        }
        return OUTROS;
    }
}
