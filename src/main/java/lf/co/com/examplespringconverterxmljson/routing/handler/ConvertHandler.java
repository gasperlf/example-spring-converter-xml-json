package lf.co.com.examplespringconverterxmljson.routing.handler;

import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;

public class ConvertHandler {

    private final ConvertFormatService convertFormatService;

    public ConvertHandler(ConvertFormatService convertFormatService) {
        this.convertFormatService = convertFormatService;
    }

    public Mono<ServerResponse> getConvertXmlToJson(ServerRequest serverRequest) {
        Mono<String> xmlRequest = serverRequest.bodyToMono(String.class);
        Mono<String> responseJson = convertFormatService.convertXmlToJson(xmlRequest);
        return responseJson.flatMap(rta -> ok()
                .contentType(APPLICATION_JSON_UTF8)
                .body(fromObject(rta)))
                .switchIfEmpty(badRequest().build());
    }

    public Mono<ServerResponse> getConvertJsonToXml(ServerRequest serverRequest) {
        Mono<String> requestJson = serverRequest.bodyToMono(String.class);
        Mono<String> response = convertFormatService.convertJsonToXml(requestJson);
        return response.flatMap(rta -> ok()
                .contentType(APPLICATION_XML)
                .body(fromObject(rta)))
                .switchIfEmpty(badRequest().build());
    }
}
