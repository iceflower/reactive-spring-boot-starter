package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.ResponseBodyEmitterObserver;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

public class FluxSseEmitter<T> extends SseEmitter {

  private final ResponseBodyEmitterObserver<T> observer;

  public FluxSseEmitter(Flux<T> flux) {
    this(null, flux);
  }

  public FluxSseEmitter(MediaType mediaType, Flux<T> flux) {
    this(null, mediaType, flux);
  }

  public FluxSseEmitter(Long timeout, MediaType mediaType, Flux<T> flux) {
    super(timeout);
    this.observer = new ResponseBodyEmitterObserver<T>(mediaType, flux, this);
  }
}
