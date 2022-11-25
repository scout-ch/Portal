package ch.itds.pbs.portal;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"ch.itds.pbs.portal", "asset.pipeline.springboot"})
public class PortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalApplication.class, args);
	}

	@Value("${tomcat.ajp.enabled:false}")
	private transient boolean tomcatAjpEnabled;

	@Value("${tomcat.ajp.port:8009}")
	private transient int ajpPort;

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		if (tomcatAjpEnabled) {
			log.info("add AJP connector on port {}", ajpPort);
			tomcat.addAdditionalTomcatConnectors(ajpConnector(ajpPort));
		}
		return tomcat;
	}

	private Connector ajpConnector(int ajpPort) {
		Connector ajpConnector = new Connector("AJP/1.3");
		ajpConnector.setPort(ajpPort);
		ajpConnector.setSecure(false);
		ajpConnector.setAllowTrace(false);
		ajpConnector.setScheme("http");
		ProtocolHandler protocolHandler = ajpConnector.getProtocolHandler();
		if (protocolHandler instanceof AbstractAjpProtocol<?> ajpProtocolHandler) {
			ajpProtocolHandler.setSecretRequired(false);
			ajpProtocolHandler.setAddress(new InetSocketAddress(0).getAddress());
		} else {
			log.warn("unexpected protocol handler: unable to disable secret/set wildcard address");
		}
		return ajpConnector;
	}

}
