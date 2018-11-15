package lf.co.com.examplespringconverterxmljson.services.impl;

import lf.co.com.examplespringconverterxmljson.exceptions.ConverterException;
import lf.co.com.examplespringconverterxmljson.services.ConvertFormatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;

import javax.annotation.PostConstruct;
import javax.script.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

@Log4j2
@Service
public class ConvertFormatServiceImpl implements ConvertFormatService {

    protected static final String SETTING_CONVERTER = "{ignoreComment: true, alwaysChildren: true,compact: true, spaces: 2}";
    protected static final String SETTING_CONVERTER_XML2JSON = "{ignoreComment: true, alwaysChildren: true,compact: false, spaces: 2}";
    protected static final String FUNCTION_NAME_XML2JSON = "xml2json";
    protected static final String FUNCTION_NAME_JSON2XML = "json2xml";
    protected static final String ENGINE_NAME = "nashorn";
    private Invocable invocable;

    @PostConstruct
    public void initBean() throws IOException, ScriptException {
        ClassLoader classLoader = getClass().getClassLoader();
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);

        Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("window", engineScope);

        String dependencies[] = {"static/js/sax.js"};
        for (String dependency : dependencies) {
            engine.eval(new FileReader(classLoader.getResource(dependency).getFile()));
        }

        engine.eval(new FileReader(classLoader.getResource("static/js/xml-js.js").getFile()));
        invocable = (Invocable) engine;
    }

    private void loadGlobalVariables(ScriptEngine engine) {
        Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("window", engineScope);
    }

    private void loadDependencies(ClassLoader classLoader, ScriptEngine engine) throws IOException, ScriptException {
        String dependencies[] = {"static/js/sax.js"};
        for (String dependency : dependencies) {
            engine.eval(new FileReader(classLoader.getResource(dependency).getFile()));
        }
    }

    @Override
    public Mono<String> convertXmlToJson(String xml) {
        return converter("xml2json", xml, SETTING_CONVERTER_XML2JSON);
    }

    @Override
    public Mono<String> convertJsonToXml(String json) {
        return converter("json2xml", json, SETTING_CONVERTER);
    }

    private Mono<String> converter(@NonNull String function, @NonNull String contentToConverter, String setting) {
        try {
            return Mono.just((String) invocable.invokeFunction(function, contentToConverter, setting));
        } catch (Exception e) {
            throw new ConverterException(e.getMessage());
        }
    }

    private Mono<String> converter2(@NonNull String function, @NonNull Mono<String> stringToConverter, String setting) {
        return stringToConverter.map(content-> {
            try{
                log.info(content);
                return ((String)invocable.invokeFunction(function, content, setting));
            }catch (Exception e){
                throw new ConverterException(e.getMessage());
            }
        });
    }
}
