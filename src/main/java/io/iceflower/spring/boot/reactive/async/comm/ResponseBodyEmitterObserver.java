package io.iceflower.spring.boot.reactive.async.comm;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Flux;

public class ResponseBodyEmitterObserver<T> extends DisposableObserver<T> implements Runnable {

  private final MediaType mediaType;

  private final ResponseBodyEmitter responseBodyEmitter;

  private boolean completed;

  /**
   * 생성자.
   */
  public ResponseBodyEmitterObserver(
      MediaType mediaType, Observable<T> observable, ResponseBodyEmitter responseBodyEmitter) {

    this.mediaType = mediaType;
    this.responseBodyEmitter = responseBodyEmitter;
    this.responseBodyEmitter.onTimeout(this);
    this.responseBodyEmitter.onCompletion(this);
    observable.subscribe(this);
  }

  /**
   * 생성자.
   */
  public ResponseBodyEmitterObserver(MediaType mediaType, Flowable<T> flowable,
      ResponseBodyEmitter responseBodyEmitter) {

    this.mediaType = mediaType;
    this.responseBodyEmitter = responseBodyEmitter;
    this.responseBodyEmitter.onTimeout(this);
    this.responseBodyEmitter.onCompletion(this);
    flowable.toObservable()
        .subscribe(this);
  }

  /**
   * 생성자.
   */
  public ResponseBodyEmitterObserver(MediaType mediaType, Flux<T> flux,
      ResponseBodyEmitter responseBodyEmitter) {

    this.mediaType = mediaType;
    this.responseBodyEmitter = responseBodyEmitter;
    this.responseBodyEmitter.onTimeout(this);
    this.responseBodyEmitter.onCompletion(this);

    RxJava3Adapter.fluxToFlowable(flux)
        .toObservable()
        .subscribe(this);
  }

  @Override
  public void onNext(T value) {

    try {
      if (!completed) {
        responseBodyEmitter.send(value, mediaType);
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public void onError(Throwable e) {
    responseBodyEmitter.completeWithError(e);
  }

  @Override
  public void onComplete() {
    if (!completed) {
      completed = true;
      responseBodyEmitter.complete();
    }
  }

  @Override
  public void run() {
    this.dispose();
  }
}
