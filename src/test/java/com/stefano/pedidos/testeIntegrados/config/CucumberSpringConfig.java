package com.stefano.pedidos.testeIntegrados.config;

import com.stefano.pedidos.kafka.constants.KafkaTopics;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {
                KafkaTopics.PEDIDO_CRIADO,
                KafkaTopics.PEDIDO_VALIDADO,
                KafkaTopics.PEDIDO_RESERVADO_ESTOQUE,
                KafkaTopics.PEDIDO_EM_SEPARACAO,
                KafkaTopics.PEDIDO_SEPARADO,
                KafkaTopics.PEDIDO_EM_TRANSPORTE,
                KafkaTopics.PEDIDO_ENTREGUE,
                KafkaTopics.CANCELADO
        }
)
public class CucumberSpringConfig {

    @LocalServerPort
    private int port;

    @PostConstruct
    public void setup() {
        RestAssured.port = port;
    }
}
