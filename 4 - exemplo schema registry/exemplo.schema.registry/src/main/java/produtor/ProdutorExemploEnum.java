package produtor;

enum ProdutorExemploEnum {
    TOPICO("topic1"),
    CHAVE("key");

    private final String descricao;

    ProdutorExemploEnum(String s) {
        descricao = s;
    }

    public String getDescricao() {
        return descricao;
    }
}
