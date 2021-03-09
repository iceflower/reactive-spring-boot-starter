package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.ResponseBodyEmitterObserver;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ObservableSseEmitter<T> extends SseEmitter {

  private final ResponseBodyEmitterObserver<T> observer;

  public ObservableSseEmitter(Observable<T> observable) {
    this(null, observable);
  }

  public ObservableSseEmitter(MediaType mediaType, Observable<T> observable) {
    this(null, mediaType, observable);
  }

  public ObservableSseEmitter(Long timeout, MediaType mediaType, Observable<T> observable) {
    super(timeout);
    this.observer = new ResponseBodyEmitterObserver<T>(mediaType, observable, this);
  }
}
