package hexlet.code.controllers.api;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-sentry")
    public String testSentry() {
        try {
            throw new Exception("Это тестовая ошибка Sentry");
        } catch (Exception e) {
            Sentry.captureException(e);
            Sentry.flush(5000);
        }
        return "Тестовое событие отправлено в Sentry!";
    }
}
