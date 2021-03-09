package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.dto.EventDto;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = FluxSseEmitterTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("FluxSseEmitter 클래스")
class FluxSseEmitterTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private static Date getDate(int year, int month, int day) {
    return new GregorianCalendar(year, month, day).getTime();
  }

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/sse")
    public FluxSseEmitter<String> flux() {
      return new FluxSseEmitter<>(Flux.just(("flux value")));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    public FluxSseEmitter<String> messages() {
      return new FluxSseEmitter<String>(Flux.just("message 1", "message 2", "message 3"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events")
    public FluxSseEmitter<EventDto> event() {
      return new FluxSseEmitter<EventDto>(MediaType.APPLICATION_JSON, Flux.just(
          new EventDto("Spring.io", getDate(2016, 5, 11)),
          new EventDto("JavaOne", getDate(2016, 9, 22))
      ));
    }
  }

  @Nested
  @DisplayName("ObservableSseEmitter 는")
  class Describe_of_ObservableSseEmitter {

    @Nested
    @DisplayName("SSE 데이터를 전송받아야 할 때")
    class Context_with_retrieve_sse_data {

      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/sse", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("data:flux value\n\n", response.getBody());
      }
    }

    @Nested
    @DisplayName("SSE 메시지를 한번에 여러 개 받아야 할 때")
    class Context_with_retrieve_multiple_messages {

      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/messages", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("data:message 1\n\ndata:message 2\n\ndata:message 3\n\n",
            response.getBody());
      }
    }

    @Nested
    @DisplayName("JSON으로 직렬화된 SSE 메시지를 여러개 받아야 할 때")
    class Context_with_retrieve_json_over_sse_multiple_messages {

      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/events", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
      }
    }
  }

}