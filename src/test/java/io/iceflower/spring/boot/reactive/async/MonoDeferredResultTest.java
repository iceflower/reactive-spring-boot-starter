package io.iceflower.spring.boot.reactive.async;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import io.iceflower.spring.boot.reactive.dto.EventDto;
import java.time.Duration;
import java.util.Date;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * A unit test code of SingleDeferredResult
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = MonoDeferredResultTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("SingleDeferredResult 클래스")
class MonoDeferredResultTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/mono")
    public MonoDeferredResult<String> mono() {
      return new MonoDeferredResult<String>(Mono.just("mono value"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/monoWithResponse")
    public MonoDeferredResult<ResponseEntity<String>> monoWithResponse() {
      return new MonoDeferredResult<ResponseEntity<String>>(
          Mono.just(new ResponseEntity<String>("mono value", HttpStatus.NOT_FOUND)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event", produces = APPLICATION_JSON_VALUE)
    public MonoDeferredResult<EventDto> event() {
      return new MonoDeferredResult<EventDto>(Mono.just(new EventDto("Spring.io", new Date())));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/throw")
    public MonoDeferredResult<Object> error() {
      return new MonoDeferredResult<Object>(Mono.error(new RuntimeException("Unexpected")));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/timeout")
    public MonoDeferredResult<String> timeout() {
      return new MonoDeferredResult<String>(
          Mono.delay(Duration.ofMinutes(1))
              .map(e -> "Mono value"));
    }
  }

  @Nested
  @DisplayName("MonoDeferredResult 는")
  class Describe_of_MonoDeferredResult {

    @Nested
    @DisplayName("단일 값을 전달해야 할때")
    class Context_with_retrive_mono_value {

      @Test
      @DisplayName("성공적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/mono", String.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("mono value", response.getBody());
      }
    }

    @Nested
    @DisplayName("단일 값과 HTTP 상태코드를 함께 전달해야 할 때")
    class Context_with_retrieve_single_value_with_status_code {

      @Test
      @DisplayName("성공적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate
            .getForEntity("/monoWithResponse", String.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("mono value", response.getBody());
      }
    }

    @Nested
    @DisplayName("json으로 직렬화한 값을 전달해 줄 때")
    class Context_with_json_serialized_pojo_value {

      @Test
      @DisplayName("성공적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<EventDto> response = restTemplate.getForEntity("/event", EventDto.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Spring.io", response.getBody().getName());
      }
    }

    @Nested
    @DisplayName("오류가 발생했을 때")
    class Context_with_retrieve_error_response {

      @Test
      @DisplayName("500 에러를 받는다")
      void it_returns_http_500_code() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/throw", Object.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      }
    }

    @Nested
    @DisplayName("타임아웃이 발생했을 때")
    class Context_with_timeout_connection {

      @Test
      @DisplayName("정상적으로 에러값을 돌려준다")
      void it_returns_http_406_error() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/timeout", Object.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
      }
    }
  }

}