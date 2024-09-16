/**
 * 
 */
package um.tesoreria.core.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author daniel
 *
 */
@Configuration
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients(basePackages = "um.tesoreria.core.client")
@PropertySource("classpath:config/tesoreria.properties")
public class TesoreriaConfiguration {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
}
