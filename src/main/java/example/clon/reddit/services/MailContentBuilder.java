package example.clon.reddit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

@Service
public class MailContentBuilder {

    @Autowired
    private TemplateEngine templateEngine;

    public String build(String message) {
        Context context = new Context();  // Instanciar correctamente el contexto
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);  // Asegurarse de que el nombre del template es correcto
    }
}
