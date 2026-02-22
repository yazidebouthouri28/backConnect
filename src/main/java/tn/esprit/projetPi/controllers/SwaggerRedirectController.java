package tn.esprit.projetPi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/swagger-ui")
    public String redirectToSwaggerUi() {
        return "redirect:/webjars/swagger-ui/5.18.2/index.html";
    }

    @GetMapping("/swagger-ui.html")
    public String redirectToSwaggerHtml() {
        return "redirect:/webjars/swagger-ui/5.18.2/index.html";
    }

    @GetMapping("/swagger-ui/index.html")
    public String redirectToSwaggerIndex() {
        return "redirect:/webjars/swagger-ui/5.18.2/index.html";
    }
}