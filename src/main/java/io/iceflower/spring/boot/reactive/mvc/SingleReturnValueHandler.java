package io.iceflower.spring.boot.reactive.mvc;

import io.iceflower.spring.boot.reactive.async.SingleDeferredResult;
import io.reactivex.rxjava3.core.Single;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SingleReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

  @Override
  public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
    return returnValue != null && supportsReturnType(returnType);
  }

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return Single.class.isAssignableFrom(returnType.getParameterType());
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

    final Single<?> single = Single.class.cast(returnValue);
    WebAsyncUtils.getAsyncManager(webRequest)
        .startDeferredResultProcessing(new SingleDeferredResult(single), mavContainer);
  }
}
