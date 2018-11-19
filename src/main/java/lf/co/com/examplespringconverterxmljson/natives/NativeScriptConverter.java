package lf.co.com.examplespringconverterxmljson.natives;

import lf.co.com.examplespringconverterxmljson.exceptions.ConverterException;
import lf.co.com.examplespringconverterxmljson.exceptions.LoadScriptException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import javax.script.*;
import java.io.FileReader;
import java.io.IOException;

@Log4j2
public class NativeScriptConverter {

    private Invocable invocable;
    private ClassLoader classLoader = getClass().getClassLoader();
    protected static NativeScriptConverter nativeScriptConverter;
    protected static final String SETTING_CONVERTER_XML2JSON = "{ignoreComment: true, alwaysChildren: true,compact: false, spaces: 2}";
    protected static final String ENGINE_NAME = "nashorn";

    public static NativeScriptConverter getInstance() {
        try {
            if (nativeScriptConverter == null) {
                nativeScriptConverter = new NativeScriptConverter();
            }
            return nativeScriptConverter;
        } catch (Exception e) {
            log.error("{}", e);
            throw new LoadScriptException(e.getMessage());
        }
    }

    private NativeScriptConverter() throws IOException, ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
        loadGlobalVariables(engine);
        loadDependencies(engine);
        engine.eval(new FileReader(classLoader.getResource("static/js/xml-js.js").getFile()));
        invocable = (Invocable) engine;
    }

    private void loadGlobalVariables(ScriptEngine engine) {
        Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("window", engineScope);
    }

    private void loadDependencies(ScriptEngine engine) throws IOException, ScriptException {
        String dependencies[] = {"static/js/dependencies/sax.js"};
        for (String dependency : dependencies) {
            engine.eval(new FileReader(classLoader.getResource(dependency).getFile()));
        }
    }

    public Mono<String> converter(@NonNull String function, @NonNull String contentToConverter) {
        try {
            return Mono.just((String) invocable.invokeFunction(function, contentToConverter, SETTING_CONVERTER_XML2JSON));
        } catch (Exception e) {
            log.error("{}", e);
            throw new ConverterException(e.getMessage());
        }
    }
}
