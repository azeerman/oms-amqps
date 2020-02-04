package com.carrefour.oms;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oms")
public class OmsProperties {

	private RabbitProperties rabbitmq = new RabbitProperties();

	public RabbitProperties getRabbitmq() {
		return rabbitmq;
	}

	public void setRabbitmq(RabbitProperties rabbitmq) {
		this.rabbitmq = rabbitmq;
	}

}
