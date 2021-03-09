package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.DeferredResultObserver;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.DeferredResult;

public class ObservableDeferredResult<T> extends DeferredResult<List<T>> {

  private static final Object EMPTY_RESULT = new Object();

  private final DeferredResultObserver<List<T>> observer;

  public ObservableDeferredResult(Observable<T> observable) {
    this(null, EMPTY_RESULT, observable);
  }

  public ObservableDeferredResult(long timeout, Observable<T> observable) {
    this(timeout, EMPTY_RESULT, observable);
  }

  /**
   * 생성자.
   */
  public ObservableDeferredResult(Long timeout, Object timeoutResult, Observable<T> observable) {
    super(timeout, timeoutResult);
    Assert.notNull(observable, "observable can not be null");

    observer = new DeferredResultObserver<List<T>>(observable.toList().toObservable(), this);
  }
}
