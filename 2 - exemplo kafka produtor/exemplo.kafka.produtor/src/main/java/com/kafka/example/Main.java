package com.kafka.example;

public class Main {
    public static void main(String[] args) {
        var produtor = new Produtor();
        var mensagem = "Mensagem enviada de um produtor java de exemplo...";
        produtor.produzir(mensagem);
    }
}