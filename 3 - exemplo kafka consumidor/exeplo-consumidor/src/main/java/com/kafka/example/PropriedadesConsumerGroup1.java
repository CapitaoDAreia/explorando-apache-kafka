package com.kafka.example;

enum PropriedadesConsumerGroup1 {
    NOME_TOPICO("topic1"),
    NOME_GRUPO("consumer-group-1");

    private final String descricao;

    PropriedadesConsumerGroup1(String s){
        descricao = s;
    }

    public String getDescricao() {
        return descricao;
    }
}
