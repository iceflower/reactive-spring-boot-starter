package io.iceflower.spring.boot.reactive.mvc;

import io.iceflower.spring.boot.reactive.async.ObservableDeferredResult;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

  @Override
  public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
    return returnValue != null && supportsReturnType(returnType);
  }

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return Observable.class.isAssignableFrom(returnType.getParameterType());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handleReturnValue(
      Object returnValue,
      MethodParameter returnType,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest) throws Exception {

    if (returnValue == null) {
      mavContainer.setRequestHandled(true);
      return;
    }

    final Observable<?> observable = Observable.class.cast(returnValue);
    WebAsyncUtils.getAsyncManager(webRequest)
        .startDeferredResultProcessing(new ObservableDeferredResult(observable), mavContainer);
  }
}
