package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.DeferredResultObserver;
import io.reactivex.rxjava3.core.Single;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.DeferredResult;

public class SingleDeferredResult<T> extends DeferredResult<T> {

  private static final Object EMPTY_RESULT = new Object();

  private final DeferredResultObserver<T> observer;

  public SingleDeferredResult(Single<T> single) {
    this(null, EMPTY_RESULT, single);
  }

  public SingleDeferredResult(long timeout, Single<T> single) {
    this(timeout, EMPTY_RESULT, single);
  }

  /**
   * 생성자.
   */
  public SingleDeferredResult(Long timeout, Object timeoutResult, Single<T> single) {
    super(timeout, timeoutResult);
    Assert.notNull(single, "single can not be null");

    observer = new DeferredResultObserver<T>(single, this);
  }
}
