package lf.co.com.examplespringconverterxmljson.services;

import reactor.core.publisher.Mono;

public interface ConvertFormatService {

    public Mono<String> convertXmlToJson(String xml);
    public Mono<String> convertJsonToXml(String json);

}
