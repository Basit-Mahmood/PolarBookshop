package pk.training.basit.polarbookshop.catalogservice.web.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pk.training.basit.polarbookshop.catalogservice.config.PolarProperties;

@RestController
public class IndexController {

    // Bean to access the custom properties injected via constructor auto-wiring
    private final PolarProperties polarProperties;

    public IndexController(PolarProperties polarProperties) {
        this.polarProperties = polarProperties;
    }

    @GetMapping("/")
    public String getGreeting() {
        // Uses the welcome message from the configuration data bean
        return polarProperties.getGreeting();
    }
}
