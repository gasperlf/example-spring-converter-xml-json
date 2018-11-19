package lf.co.com.examplespringconverterxmljson.routing;

import lf.co.com.examplespringconverterxmljson.exceptions.ConverterException;
import lf.co.com.examplespringconverterxmljson.exceptions.LoadScriptException;
import lf.co.com.examplespringconverterxmljson.routing.dtos.ErrorMessage;
import lf.co.com.examplespringconverterxmljson.routing.handler.ConvertHandler;
import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

@Configuration
public class ConverterRoute {

    private final ConvertHandler convertHandler;

    public ConverterRoute(ConvertFormatService convertFormatService) {
        this.convertHandler = new ConvertHandler(convertFormatService);
    }

    @Bean
    public RouterFunction<?> route() {
        return nest(path("/v1/converts"),
                RouterFunctions.route(POST("/xmlTojson").and(accept(MediaType.APPLICATION_XML).and(contentType(MediaType.APPLICATION_XML))), convertHandler::getConvertXmlToJson)
                        .filter(converterExceptionBadRequest())
                        .filter(loadScriptExceptionBadRequest())
                        .andOther(RouterFunctions.route(POST("/jsonToxml").and(accept(MediaType.APPLICATION_JSON_UTF8).and(contentType(MediaType.APPLICATION_JSON_UTF8))), convertHandler::getConvertJsonToXml)
                                .filter(converterExceptionBadRequest())
                                .filter(loadScriptExceptionBadRequest())));
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> converterExceptionBadRequest() {
        return (request, next) -> next.handle(request)
                .onErrorResume(ConverterException.class, e -> ServerResponse.badRequest().body(BodyInserters.fromObject(new ErrorMessage("", e.getMessage()))));
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> loadScriptExceptionBadRequest() {
        return (request, next) -> next.handle(request)
                .onErrorResume(LoadScriptException.class, e -> ServerResponse.status(HttpStatus.BAD_GATEWAY).body(BodyInserters.fromObject(new ErrorMessage("", e.getMessage()))));
    }
}
