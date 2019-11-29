package gr.arhs.firtapp.web;

import java.time.Duration;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@RestController
public class HelloController {

    @GetMapping(value = "/hello", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamMessages(){
    	
    	Flux<String> data = Flux.fromStream(Stream.generate(()-> "Hello docker"));
    	Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
    	
    	return Flux.zip(data, interval).map(Tuple2::getT1);
    }

}