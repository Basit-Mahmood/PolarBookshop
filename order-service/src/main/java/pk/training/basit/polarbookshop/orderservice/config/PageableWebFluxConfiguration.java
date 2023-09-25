package pk.training.basit.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class PageableWebFluxConfiguration implements WebFluxConfigurer {

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(reactivePageableHandlerResolver());
    }

    private HandlerMethodArgumentResolver reactivePageableHandlerResolver() {

        ReactivePageableHandlerMethodArgumentResolver reactivePageableHandler = new ReactivePageableHandlerMethodArgumentResolver();
        reactivePageableHandler.setOneIndexedParameters(true);
        reactivePageableHandler.setMaxPageSize(50);
        return reactivePageableHandler;
    }

}
