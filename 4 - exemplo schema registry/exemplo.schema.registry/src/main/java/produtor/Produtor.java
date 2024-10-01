package produtor;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class Produtor {
    public void produzir(Object mensagem) {
        System.out.println("Enviando mensagem para o tópico " + ProdutorExemploEnum.TOPICO.getDescricao());

        var propriedades = constroiPropriedades();

        try (var produtor = new KafkaProducer<String, Object>(propriedades)) {

            var dados = new ProducerRecord<>(
                    ProdutorExemploEnum.TOPICO.getDescricao(),
                    ProdutorExemploEnum.CHAVE.getDescricao(),
                    mensagem
            );

            produtor.send(dados);
            produtor.flush();

        } catch (Exception e) {
            System.err.println("Erro ao produzir mensagem: " + e);
        }
    }

    private static Properties constroiPropriedades() {
        var properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", KafkaJsonSchemaSerializer.class.getName());
        properties.put("schema.registry.url", "http://localhost:8081");
        return properties;
    }
}
