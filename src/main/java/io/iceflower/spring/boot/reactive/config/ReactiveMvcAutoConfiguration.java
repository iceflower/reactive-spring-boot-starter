package io.iceflower.spring.boot.reactive.config;

import io.iceflower.spring.boot.reactive.mvc.MonoReturnValueHandler;
import io.iceflower.spring.boot.reactive.mvc.ObservableReturnValueHandler;
import io.iceflower.spring.boot.reactive.mvc.SingleReturnValueHandler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnProperty(value = "reactive.mvc.enabled", matchIfMissing = true)
public class ReactiveMvcAutoConfiguration {

  @Bean
  @ReactiveJava
  @ConditionalOnMissingBean
  @ConditionalOnClass(Observable.class)
  public ObservableReturnValueHandler observableReturnValueHandler() {
    return new ObservableReturnValueHandler();
  }

  @Bean
  @ReactiveJava
  @ConditionalOnMissingBean
  @ConditionalOnClass(Single.class)
  public SingleReturnValueHandler singleReturnValueHandler() {
    return new SingleReturnValueHandler();
  }

  @Bean
  @ReactiveJava
  @ConditionalOnMissingBean
  @ConditionalOnClass(Mono.class)
  public MonoReturnValueHandler monoReturnValueHandler() {
    return new MonoReturnValueHandler();
  }

  @Configuration
  public static class RxJavaWebConfiguration {

    @ReactiveJava
    @Autowired
    private List<AsyncHandlerMethodReturnValueHandler> handlers =
        new ArrayList<>();

    /**
     * reactiveWebMvcConfiguration.
     *
     * @return WebMvcConfigurer
     */
    @Bean
    public WebMvcConfigurer reactiveWebMvcConfiguration() {
      return new WebMvcConfigurer() {
        @Override
        public void addReturnValueHandlers(
            List<HandlerMethodReturnValueHandler> returnValueHandlers) {
          if (handlers != null) {
            returnValueHandlers.addAll(handlers);
          }
        }
      };
    }
  }
}
