package lf.co.com.examplespringconverterxmljson.routing.handler;

import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.scheduler.Schedulers.elastic;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_XML;

public class ConvertHandler {

    private final ConvertFormatService convertFormatService;

    public ConvertHandler(ConvertFormatService convertFormatService) {
        this.convertFormatService = convertFormatService;
    }

    public Mono<ServerResponse> getConvertXmlToJson(ServerRequest serverRequest) {
        Mono<String> requestString = serverRequest.bodyToMono(String.class);
        Mono<String> response = this.convertFormatService.convertXmlToJson(requestString);
        return response.subscribeOn(elastic()).flatMap(rta -> ok()
                .contentType(APPLICATION_JSON_UTF8)
                .body(fromObject(rta)))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> getConvertJsonToXml(ServerRequest serverRequest) {
        Mono<String> requestString = serverRequest.bodyToMono(String.class);
        Mono<String> response = this.convertFormatService.convertXmlToJson(requestString);
        return response.subscribeOn(elastic()).flatMap(rta -> ok().contentType(APPLICATION_XML).body(fromObject(rta)))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }
}
