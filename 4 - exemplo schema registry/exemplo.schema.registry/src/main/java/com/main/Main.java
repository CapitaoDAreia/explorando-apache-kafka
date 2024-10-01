package com.main;

import org.jetbrains.annotations.NotNull;
import produtor.Produtor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Produtor produtor = new Produtor();

        var mensagem1 = produzirMensagemDeSucesso1();
        var mensagem2 = produzirMensagemDeSucesso2();
        var mensagem3 = produzirMensagemDeSucesso3();

        var mensagens = Stream.of(mensagem1, mensagem2, mensagem3);

        mensagens.forEach(produtor::produzir);
    }

    private static @NotNull Map<String, Object> produzirMensagemDeSucesso1() {
        Map<String, Object> mensagem = new HashMap<>();
        mensagem.put("nome", "John Marston");
        mensagem.put("idade", 27);
        mensagem.put("email", "john.marston@rdr2.com");
        return mensagem;
    }

    private static @NotNull Map<String, Object> produzirMensagemDeSucesso2() {
        //Evoluindo o schema - adicionando campos
        Map<String, Object> mensagem = new HashMap<>();
        mensagem.put("nome", "John Marston");
        mensagem.put("idade", 27);
        mensagem.put("email", "john.marston@rdr2.com");
        mensagem.put("telefone", "123456789");
        return mensagem;
    }

    private static @NotNull Map<String, Object> produzirMensagemDeSucesso3() {
        //Evoluindo o schema - removendo campos
        Map<String, Object> mensagem = new HashMap<>();
        mensagem.put("nome", "John Marston");
        mensagem.put("idade", 27);
        return mensagem;
    }
}