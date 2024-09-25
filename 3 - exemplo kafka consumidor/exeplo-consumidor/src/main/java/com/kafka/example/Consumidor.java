package com.kafka.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumidor {
    // Prop para controlar o loop de consumo
    private static final AtomicBoolean keepConsuming = new AtomicBoolean(true);

    public void consumir() {
        System.out.println("Inicializando consumidor Kafka...");

        var propriedades = constroiPropriedades();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(propriedades)) {
            var topico = Collections.singletonList(PropriedadesConsumerGroup1.NOME_TOPICO.getDescricao());

            consumer.subscribe(topico);

            // Registra um shutdown hook para graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Sinal de shutdown recebido, graciosamente interrompendo o consumer...");
                keepConsuming.set(false);
            }));

            while (keepConsuming.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Offset: %d, Key: %s, Value: %s%n", record.offset(), record.key(), record.value());
                }

                //Commita os offsets manualmente
                consumer.commitSync();
            }

        } catch (Exception ex) {
            System.out.println("Erro ao consumir tópico: " + PropriedadesConsumerGroup1.NOME_TOPICO.getDescricao());
            System.out.println(ex.getMessage());
        } finally {
            System.out.println("Consumidor interrompido graciosamente...");
        }
    }

    private Properties constroiPropriedades() {
        var propriedades = new Properties();
        propriedades.put("bootstrap.servers", "localhost:9092"); // Padrão do Kafka
        propriedades.put("group.id", PropriedadesConsumerGroup1.NOME_GRUPO.getDescricao()); // Define consumer group
        propriedades.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        propriedades.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        propriedades.put("auto.offset.reset", "earliest"); //Propriedade de leitura após conexão, neste caso, consumir a partir do menor offset se não houver commit anterior
        propriedades.put("enable.auto.commit", "false"); // Desabilita commit automático

        return propriedades;
    }
}
