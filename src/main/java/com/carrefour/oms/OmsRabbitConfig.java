package com.carrefour.oms;

import java.time.Duration;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OmsRabbitConfig {
	
	@Autowired
	private OmsProperties omsProperties;
	
	
	@Bean(name = "omsRabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() throws Exception {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(omsConnectionFactory());
		return factory;
	}
	
	
	private ConnectionFactory omsConnectionFactory() throws Exception {
		PropertyMapper map = PropertyMapper.get();
		RabbitProperties properties=omsProperties.getRabbitmq();
		CachingConnectionFactory factory = new CachingConnectionFactory(
				getRabbitConnectionFactoryBean(properties).getObject());
		map.from(properties::determineAddresses).to(factory::setAddresses);
		map.from(properties::isPublisherReturns).to(factory::setPublisherReturns);
		map.from(properties::getPublisherConfirmType).whenNonNull().to(factory::setPublisherConfirmType);
		RabbitProperties.Cache.Channel channel = properties.getCache().getChannel();
		map.from(channel::getSize).whenNonNull().to(factory::setChannelCacheSize);
		map.from(channel::getCheckoutTimeout).whenNonNull().as(Duration::toMillis)
				.to(factory::setChannelCheckoutTimeout);
		RabbitProperties.Cache.Connection connection = properties.getCache().getConnection();
		map.from(connection::getMode).whenNonNull().to(factory::setCacheMode);
		map.from(connection::getSize).whenNonNull().to(factory::setConnectionCacheSize);
		//map.from(connectionNameStrategy::getIfUnique).whenNonNull().to(factory::setConnectionNameStrategy);
		return factory;
	}
	
	
	private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(RabbitProperties properties)
			throws Exception {
		PropertyMapper map = PropertyMapper.get();
		RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
		map.from(properties::determineHost).whenNonNull().to(factory::setHost);
		map.from(properties::determinePort).to(factory::setPort);
		map.from(properties::determineUsername).whenNonNull().to(factory::setUsername);
		map.from(properties::determinePassword).whenNonNull().to(factory::setPassword);
		map.from(properties::determineVirtualHost).whenNonNull().to(factory::setVirtualHost);
		map.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds)
				.to(factory::setRequestedHeartbeat);
		RabbitProperties.Ssl ssl = properties.getSsl();
		if (ssl.determineEnabled()) {
			factory.setUseSSL(true);
			map.from(ssl::getAlgorithm).whenNonNull().to(factory::setSslAlgorithm);
			map.from(ssl::getKeyStoreType).to(factory::setKeyStoreType);
			map.from(ssl::getKeyStore).to(factory::setKeyStore);
			map.from(ssl::getKeyStorePassword).to(factory::setKeyStorePassphrase);
			map.from(ssl::getTrustStoreType).to(factory::setTrustStoreType);
			map.from(ssl::getTrustStore).to(factory::setTrustStore);
			map.from(ssl::getTrustStorePassword).to(factory::setTrustStorePassphrase);
			map.from(ssl::isValidateServerCertificate)
					.to((validate) -> factory.setSkipServerCertificateValidation(!validate));
			map.from(ssl::getVerifyHostname).to(factory::setEnableHostnameVerification);
		}
		map.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis)
				.to(factory::setConnectionTimeout);
		factory.afterPropertiesSet();
		return factory;
	}

}
