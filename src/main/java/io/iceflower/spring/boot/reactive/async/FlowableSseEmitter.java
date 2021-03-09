package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.ResponseBodyEmitterObserver;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class FlowableSseEmitter<T> extends SseEmitter {

  private final ResponseBodyEmitterObserver<T> observer;

  public FlowableSseEmitter(Flowable<T> flowable) {
    this(null, flowable);
  }

  public FlowableSseEmitter(MediaType mediaType, Flowable<T> flowable) {
    this(null, mediaType, flowable);
  }

  public FlowableSseEmitter(Long timeout, MediaType mediaType, Flowable<T> flowable) {
    super(timeout);
    this.observer = new ResponseBodyEmitterObserver<T>(mediaType, flowable, this);
  }
}
