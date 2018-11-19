package lf.co.com.examplespringconverterxmljson.services;

import reactor.core.publisher.Mono;

public interface ConvertFormatService {

    public Mono<String> convertXmlToJson(Mono<String> xml);
    public Mono<String> convertJsonToXml(Mono<String> json);

}
