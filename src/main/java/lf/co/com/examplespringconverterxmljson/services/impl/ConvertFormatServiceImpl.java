package lf.co.com.examplespringconverterxmljson.services.impl;

import lf.co.com.examplespringconverterxmljson.exceptions.ConverterException;
import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.script.*;
import java.io.FileReader;
import java.io.IOException;

@Log4j2
@Service
public class ConvertFormatServiceImpl implements ConvertFormatService {

    protected static final String EXTERNAL_SETTING_CONVERTER = "{compact: true, spaces: 4}";
    protected static final String FUNCTION_NAME_XML2JSON = "xml2json";
    protected static final String FUNCTION_NAME_JSON2XML = "json2xml";
    protected static final String ENGINE_NAME = "nashorn";
    private Invocable invocable;

    @PostConstruct
    public void initBean() throws IOException, ScriptException {
        ClassLoader classLoader = getClass().getClassLoader();
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
        loadGlobalVariables(engine);
        loadDependencies(classLoader, engine);
        engine.eval(new FileReader(classLoader.getResource("static/js/xml-js.js").getFile()));
        invocable = (Invocable) engine;
    }

    private void loadGlobalVariables(ScriptEngine engine) {
        Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("window", engineScope);
    }

    private void loadDependencies(ClassLoader classLoader, ScriptEngine engine) throws IOException, ScriptException {
        String dependencies[] = {"static/js/dependencies/sax.js"};
        for (String dependency : dependencies) {
            engine.eval(new FileReader(classLoader.getResource(dependency).getFile()));
        }
    }

    @Override
    public Mono<String> convertXmlToJson(Mono<String> xml) {
        return converter(FUNCTION_NAME_XML2JSON, xml, EXTERNAL_SETTING_CONVERTER);
    }

    @Override
    public Mono<String> convertJsonToXml(Mono<String> json) {
        return converter(FUNCTION_NAME_JSON2XML, json, EXTERNAL_SETTING_CONVERTER);
    }

    private Mono<String> converter(String function, Mono<String> stringToConverter, String setting) {
        log.info("salida, {}", stringToConverter.block());
        try {
            Object result = invocable.invokeFunction(function, stringToConverter, setting);
            log.info("{}", result);
            return Mono.just((String) result);
        } catch (Exception e) {
            throw new ConverterException(e.getMessage());
        }
    }
}
