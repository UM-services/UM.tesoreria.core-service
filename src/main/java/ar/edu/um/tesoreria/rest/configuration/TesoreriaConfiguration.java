/**
 * 
 */
package ar.edu.um.tesoreria.rest.configuration;

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
@PropertySource("classpath:config/tesoreria.properties")
public class TesoreriaConfiguration {

}
