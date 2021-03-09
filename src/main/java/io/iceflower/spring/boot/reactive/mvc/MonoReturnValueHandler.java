package io.iceflower.spring.boot.reactive.mvc;

import io.iceflower.spring.boot.reactive.async.MonoDeferredResult;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.core.publisher.Mono;

public class MonoReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

  @Override
  public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
    return returnValue != null && supportsReturnType(returnType);
  }

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return Mono.class.isAssignableFrom(returnType.getParameterType());
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

    final Mono<?> mono = Mono.class.cast(returnValue);
    WebAsyncUtils.getAsyncManager(webRequest)
        .startDeferredResultProcessing(new MonoDeferredResult(mono), mavContainer);
  }
}
