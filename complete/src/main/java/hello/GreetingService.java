package hello;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GreetingService {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    public Greeting getGreeting(String name) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Greeting greeting = new Greeting(counter.incrementAndGet(), String.format(template, name));
        return greeting;
    }
}
