package pk.training.basit.polarbookshop.orderservice.r2dbc.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class ReactiveAuditorAwareImpl implements ReactiveAuditorAware<Long> {

    @Override
    public Mono<Long> getCurrentAuditor() {
        Long userId = 0L;
        return Mono.justOrEmpty(Optional.of(userId));
    }

}
