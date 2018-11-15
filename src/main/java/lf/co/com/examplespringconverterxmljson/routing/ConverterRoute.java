package lf.co.com.examplespringconverterxmljson.routing;

import lf.co.com.examplespringconverterxmljson.routing.handler.ConvertHandler;
import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class ConverterRoute {

    private final ConvertHandler convertHandler;

    public ConverterRoute(ConvertFormatService convertFormatService) {
        this.convertHandler = new ConvertHandler(convertFormatService);
    }

    @Bean
    public RouterFunction<?> route(){
        return nest(path("/v1/converts"),
                RouterFunctions.route(POST("/xmlTojson").and(accept(MediaType.APPLICATION_XML).and(contentType(MediaType.APPLICATION_XML))), convertHandler::getConvertXmlToJson)
      .andOther(RouterFunctions.route(POST("/jsonToxml").and(accept(MediaType.APPLICATION_JSON_UTF8).and(contentType(MediaType.APPLICATION_JSON_UTF8))), convertHandler::getConvertJsonToXml)));
    }
}
