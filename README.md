# Spring MVC Reactive handlers

> A Spring Boot starter for Reactor3 and RxJava3 integration


## Setup


maven : 
```xml
<dependency>
	<groupId>io.iceflower</groupId>
	<artifactId>reactive-spring-boot-starter</artifactId>
	<version>1.0.0</version>
</dependency>
```

gradle : 
```groovy
implementation 'io.iceflower:reactive-spring-boot-starter:1.0.0'
```

## Usage

### Basic

아래 객체에 대한 Spring MVC 반환값 핸들러가 등록되어 있습니다.
이 라이브러리를 사용하시면, 아래 객체를 사용시 더이상 블로킹 작업이나 DeferredResult, ListenableFuture 등을 사용하실 필요 없이, REST Endpoint에서 바로 반환하실 수 있습니다.

- `io.reactivex.rxjava3.core.Observable`
- `io.reactivex.rxjava3.core.Single`
- `reactor.core.publisher.Mono`

RxJava3 예제:

```java
@RestController
public static class InvoiceResource {

    @RequestMapping(method = RequestMethod.GET, value = "/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Observable<Invoice> getInvoices() {

        return Observable.just(
                new Invoice("Acme", new Date()),
                new Invoice("Oceanic", new Date())
        );
    }
}
```
```java
@RestController
public static class InvoiceResource {

    @RequestMapping(method = RequestMethod.GET, value = "/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<Invoice> getInvoices() {

        return Single.just(
                new Invoice("Acme", new Date()),
                new Invoice("Oceanic", new Date())
        );
    }
}
```

Reactor3 예제:
```java
@RestController
public static class InvoiceResource {

    @RequestMapping(method = RequestMethod.GET, value = "/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Invoice> getInvoices() {

        return Mono.just(
                new Invoice("Acme", new Date()),
                new Invoice("Oceanic", new Date())
        );
    }
}
```

`Observable`, `Single`, `Mono` 로 결과가 생산(produced) 되면, 그 결과를 목록으로 정리한 후, 스프링의 메시지 변환기를 통해 처리합니다.

정확히 하나의 결과를 반환하는 경우 `io.reactivex.rxjava3.core.Single` 과 `reactor.core.publisher.Mono` 를 사용하시면 되며, 이것은 스프링의 `DeferredResult` 나 `ListenableFuture` 와 같은 방식으로 처리됩니다.

HTTP 헤더 또는 상테코드를 제어하기 위해 `ResponseEntity` 도 같이 사용하실 수 있습니다.
 
참고: Reactive Streams를 처리하는 `HandlerReturnValueHandler` 객체는 'toList' 연산자를 사용하여 결과를 모아 처리합니다. 
이에 따라 개별 Reactive Streams 의 구독을 취소(unsubscribe)할 수 없기 때문에, 매우 오랫동안 작동하는 객체의 취급에는 적합하지 않습니다.

비동기 처리를 보다 효과적으로 제어하고 싶으시다면 (예 : 타임아웃 설정 ), 아래에 명시된 `DeferredResult`의 확장 구현체를 사용하실 수 있습니다.
- `ObservableDeferedResult`
- `SingleDeferedResult`
- `MonoDeferedResult`

### Server-sent events

스프링은 버전 4.2 부터 `ResponseBodyEmitter` 객체를 활용한 데이터 스트리밍 기능을 도입하였습니다.
이에따라, 이 라이브러리는 상기한 객체를 확장 구현한 `ObservableSseEmitter`, `FlowableSseEmitter`, `FluxSseEmitter` 를 추가해놓았습니다.

상기한 객체 덕분에 `io.reactivex.rxjava3.core.Observable`, `io.reactivex.rxjava3.core.Flowable`, `reactor.core.publisher.Flux` 를 활용한 Server-Sent-Events 기능을 구현할 수 있습니다.

`ObservableSseEmitter` 예제 :
```java
@RestController
public static class Events {

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    public ObservableSseEmitter<String> messages() {
        return new ObservableSseEmitter<String>(
            Observable.just(
                "message 1", "message 2", "message 3"
            )
        );
    }
}
```

`FlowableSseEmitter` 예제 :
```java
@RestController
public static class Events {

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    public FlowableSseEmitter<String> messages() {
        return new FlowableSseEmitter<String>(
            Flowable.just(
                "message 1", "message 2", "message 3"
            )
        );
    }
}
```

`FluxSseEmitter` 예제 :
```java
@RestController
public static class Events {

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    public FluxSseEmitter<String> messages() {
        return new FluxSseEmitter<String>(
            Flux.just(
                "message 1", "message 2", "message 3"
            )
        );
    }
}
```

상기한 예제는 아래와 같은 값을 출력합니다 :

```
data: message 1

data: message 2

data: message 3
```


## Properties

이 라이브러리가 제공하는 확장기능을 비활성화 하고 싶으시다면 `reactive.mvc.enabled` 속성을 설정하시면 됩니다.

```
reactive.mvc.enabled=true # (기본값: true)
```

## License

Apache 2.0

참고 : 이 리포지토리의 소스코드는 하기한 레포지토리의 코드를 유지관리 및 확장하기 위해 수정 및 재배포한 것임을 밝혀둡니다.

원본코드 : https://github.com/jmnarloch/rxjava-spring-boot-starter 
