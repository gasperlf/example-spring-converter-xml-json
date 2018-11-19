package lf.co.com.examplespringconverterxmljson.services.impl;

import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import lf.co.com.examplespringconverterxmljson.natives.NativeScriptConverter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ConvertFormatServiceImpl implements ConvertFormatService {

    protected static final String FUNCTION_NAME_XML2JSON = "xml2json";
    protected static final String FUNCTION_NAME_JSON2XML = "json2xml";

    @Override
    public Mono<String> convertXmlToJson(Mono<String> xml) {
        return xml.subscribeOn(Schedulers.elastic())
                .flatMap(content-> NativeScriptConverter.getInstance().converter(FUNCTION_NAME_XML2JSON, content));
    }

    @Override
    public Mono<String> convertJsonToXml(Mono<String> json) {
        return json.subscribeOn(Schedulers.elastic())
                .flatMap(content-> NativeScriptConverter.getInstance().converter(FUNCTION_NAME_JSON2XML, content));
    }
}
