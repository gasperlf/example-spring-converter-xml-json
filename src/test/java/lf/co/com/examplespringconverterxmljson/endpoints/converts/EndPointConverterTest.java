package lf.co.com.examplespringconverterxmljson.endpoints.converts;


import lf.co.com.examplespringconverterxmljson.context.TestContext;
import lf.co.com.examplespringconverterxmljson.utils.LoadData;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
public class EndPointConverterTest extends TestContext {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldConvertXmlToJsonSuccess() throws Exception {

        final String xmlContent = LoadData.loadFile("xmlSingleContent.xml");
        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/v1/converts/xmlTojson")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML)
                .body(BodyInserters.fromPublisher(Mono.just(xmlContent), String.class))
                .exchange();

        WebTestClient.BodyContentSpec bodyContentSpec = responseSpec.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody();

        bodyContentSpec.consumeWith(responseSpeci -> Assertions.assertThat(responseSpeci.getResponseBody()).isNotEmpty());
    }

    @Test
    public void shouldConvertJsonToXmlSuccess() throws Exception {

        final String xmlContent = LoadData.loadFile("jsonSingleContent.json");
        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/v1/converts/jsonToxml")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromPublisher(Mono.just(xmlContent), String.class))
                .exchange();

        WebTestClient.BodyContentSpec bodyContentSpec = responseSpec.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_XML)
                .expectBody();

        bodyContentSpec.consumeWith(responseSpeci -> Assertions.assertThat(responseSpeci.getResponseBody()).isNotEmpty());
    }


    @Test
    public void shouldConvertJsonToXmlWithJsonMalFormedSuccess() throws Exception {

        final String xmlContent = LoadData.loadFile("jsonSingleContentMalformed.json");
        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/v1/converts/jsonToxml")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromPublisher(Mono.just(xmlContent), String.class))
                .exchange();

        WebTestClient.BodyContentSpec bodyContentSpec = responseSpec.expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody();

        bodyContentSpec.consumeWith(responseSpeci -> Assertions.assertThat(responseSpeci.getResponseBody()).isNotEmpty());
    }

}
