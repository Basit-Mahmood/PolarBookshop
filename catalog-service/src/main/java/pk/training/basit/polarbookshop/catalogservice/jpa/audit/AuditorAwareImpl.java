package pk.training.basit.polarbookshop.catalogservice.jpa.audit;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId = 0L;
        return Optional.of(userId);
    }

}
