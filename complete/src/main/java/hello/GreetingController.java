package hello;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Async
@RestController
public class GreetingController {

    @Autowired
    private GreetingService greetingService;

    final static ConcurrentHashMap<String, CompletableFuture<Greeting>> tasks = new ConcurrentHashMap<>();

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/greeting")
    public DeferredResult<Greeting> greeting(@RequestParam(required=false, defaultValue="World") String name,
                                           @RequestParam(required=false, defaultValue="10000") String timeout,
                                             @RequestParam String token) throws ExecutionException, InterruptedException {

        System.out.println("=== Request greeting with timeout: " + timeout + " and token = " + token + " ===");

        CompletableFuture<Greeting> task = null;
        System.out.println(tasks.size());
        if (!tasks.containsKey(token)) {
            System.out.println("Task with token not exist");
            task = new CompletableFuture();
            tasks.put(token, task);
            task.supplyAsync(() -> greetingService.getGreeting(name));
        } else {
            System.out.println("Task with token exist");
            task = tasks.get(token);
        }

        DeferredResult<Greeting> response = new DeferredResult<>();

        if (task.isDone()) {
            System.out.println("task is done");
            response.setResult(task.get());

        } else {
            System.out.println("task is not done");
            long delay = Long.parseLong(timeout);
            checkResult(task, delay, response);
        }

        if (!response.hasResult()) {
            System.out.println("Result not exist");
            response.setResult(new Greeting());
        } else {
            System.out.println("Result exist");
            tasks.remove(token);
        }
        return response;
    }

    private void checkResult(final CompletableFuture<Greeting> task, long delay, DeferredResult<Greeting> result) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!task.isDone()) {
                    task.thenAccept(this::notify);
                } else {
                    try {
                        notify(task.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void notify(Greeting greeting) {
                result.setResult(greeting);
            }
        };
        timer.schedule(timerTask, delay);
    }









//    @GetMapping("/greeting-javaconfig")
//    public Greeting greetingWithJavaconfig(@RequestParam(required=false, defaultValue="World") String name) {
//        System.out.println("==== in greeting ====");
//        return new Greeting(counter.incrementAndGet(), String.format(template, name));
//    }

//    public void pollMsg(){
//        Timer timer = new Timer();
//        TimerTask myTask = new TimerTask() {
//            @Override
//            public void run() {
//                CompletableFuture.supplyAsync(this::sendMsg).thenAccept(this::notify);
//            }
//
//            private <String> java.lang.String sendMsg() {
//                RestTemplate restTemplate = new RestTemplate();
//                java.lang.String msg  = restTemplate.getForObject("http://example.com", java.lang.String.class);
//                return msg;
//            }
//
//            private <String> void notify(java.lang.String msg) {
//                System.out.println("Process this message for further: " + msg);
//                run();
//            }
//        };
//        timer.schedule(myTask, 1000, 2000);

//    }

}
