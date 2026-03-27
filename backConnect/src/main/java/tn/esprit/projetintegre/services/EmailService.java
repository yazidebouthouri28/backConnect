package tn.esprit.projetintegre.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        log.info("Preparing to send email to {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Add Logo as inline attachment
            try {
                ClassPathResource logo = new ClassPathResource("static/images/logo.png");
                if (logo.exists()) {
                    helper.addInline("logo", logo);
                } else {
                    log.warn("Logo not found at static/images/logo.png, skipping inline attachment");
                }
            } catch (Exception envEx) {
                log.error("Error while adding inline logo: {}", envEx.getMessage());
            }

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("CRITICAL: Failed to send email to {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email delivery failed: " + e.getMessage());
        }
    }
}
