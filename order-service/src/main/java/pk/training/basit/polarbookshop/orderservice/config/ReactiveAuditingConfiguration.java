package pk.training.basit.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import pk.training.basit.polarbookshop.orderservice.r2dbc.audit.ReactiveAuditorAwareImpl;

@Configuration
@EnableR2dbcAuditing(auditorAwareRef = "auditorAware")
public class ReactiveAuditingConfiguration {

    @Bean
    public ReactiveAuditorAware<Long> auditorAware() {
        return new ReactiveAuditorAwareImpl();
    }

}
