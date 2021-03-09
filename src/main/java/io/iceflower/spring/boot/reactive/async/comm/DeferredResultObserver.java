package io.iceflower.spring.boot.reactive.async.comm;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableObserver;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Mono;


public class DeferredResultObserver<T> extends DisposableObserver<T> implements Runnable {

  private final DeferredResult<T> deferredResult;

  /**
   * 생성자.
   */
  public DeferredResultObserver(Observable<T> observable, DeferredResult<T> deferredResult) {
    this.deferredResult = deferredResult;
    this.deferredResult.onTimeout(this);
    this.deferredResult.onCompletion(this);
    observable.subscribe(this);
  }

  /**
   * 생성자.
   */
  public DeferredResultObserver(Single<T> single, DeferredResult<T> deferredResult) {
    this.deferredResult = deferredResult;
    this.deferredResult.onTimeout(this);
    this.deferredResult.onCompletion(this);

    single.toObservable()
        .subscribe(this);
  }

  /**
   * 생성자.
   */
  // TODO : Reactor 쪽 코드는 분리가 필요함
  public DeferredResultObserver(Mono<T> mono, DeferredResult<T> deferredResult) {
    this.deferredResult = deferredResult;
    this.deferredResult.onTimeout(this);
    this.deferredResult.onCompletion(this);
    RxJava3Adapter.monoToSingle(mono)
        .toObservable()
        .subscribe(this);

  }

  @Override
  public void onNext(T value) {
    deferredResult.setResult(value);
  }

  @Override
  public void onError(Throwable e) {
    deferredResult.setErrorResult(e);
  }

  @Override
  public void onComplete() {
  }

  @Override
  public void run() {
    this.dispose();
  }
}
