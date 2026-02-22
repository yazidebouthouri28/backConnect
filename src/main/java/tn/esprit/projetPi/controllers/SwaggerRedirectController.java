package tn.esprit.projetPi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SwaggerRedirectController {

    @GetMapping({
            "/",
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-ui/index.html"
    })
    public RedirectView redirectToSwaggerUi() {
        RedirectView redirectView = new RedirectView();
        // URL complète avec paramètres pour forcer l'API locale
        redirectView.setUrl(
                "/webjars/swagger-ui/5.18.2/index.html?" +
                        "url=/v3/api-docs&" +
                        "configUrl=/v3/api-docs&" +
                        "validatorUrl=&" +
                        "displayOperationId=true&" +
                        "docExpansion=none&" +
                        "tryItOutEnabled=true&" +
                        "filter=true"
        );
        redirectView.setContextRelative(true);
        redirectView.setExposeModelAttributes(false);
        return redirectView;
    }
}