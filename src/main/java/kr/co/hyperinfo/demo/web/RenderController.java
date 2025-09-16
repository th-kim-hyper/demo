package kr.co.hyperinfo.demo.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class RenderController {

    private final SpringTemplateEngine templateEngine;

    public RenderController(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @GetMapping(value = "/render/{page}")
    public ResponseEntity<String> renderTemplate(@PathVariable String page,
                                                 HttpServletRequest request) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("page", page.equals("about") ? "about" : "home");
        Context ctx = new Context(request.getLocale());
        ctx.setVariables(vars);

        String rendered;
        try {
            rendered = templateEngine.process(page, ctx);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Template render error: " + e.getMessage());
        }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/html; charset=UTF-8"));
    return new ResponseEntity<>(rendered, headers, HttpStatus.OK);
    }
}
