package io.iceflower.spring.boot.reactive.async;

import io.iceflower.spring.boot.reactive.async.comm.DeferredResultObserver;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

public class MonoDeferredResult<T> extends DeferredResult<List<T>> {

  private static final Object EMPTY_RESULT = new Object();
  private final DeferredResultObserver<T> observer;

  public MonoDeferredResult(Mono<T> mono) {
    this(null, EMPTY_RESULT, mono);
  }

  public MonoDeferredResult(long timeout, Mono<T> mono) {
    this(timeout, EMPTY_RESULT, mono);
  }

  /**
   * 생성자.
   */
  public MonoDeferredResult(Long timeout, Object timeoutResult, Mono<T> mono) {
    super(timeout, timeoutResult);
    Assert.notNull(mono, "mono can not be null");

    this.observer = new DeferredResultObserver<T>(mono, (DeferredResult<T>) this);
  }

}
