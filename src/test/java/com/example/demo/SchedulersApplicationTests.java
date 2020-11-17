package com.example.demo;

import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Any;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class SchedulersApplicationTests {

	@Test
	void defaultThreadingModel() {
		Mono.just("1").log().map(data -> Integer.parseInt(data)).subscribe(System.out::println);
	}
	/*
	Result:
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	 */

	@Test
	void defaultThreadingModelwithLog() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.map(data -> log("map", data)).subscribe(System.out::println);
	}

	private Object log(String operatorName, String data) {
		System.out.println("Inside operator "+operatorName+" data is "+data+" Thread: "+Thread.currentThread().getName());
		return operatorName+" : "+data;
	}

	private int Intlog(String operatorName, int data) {
		System.out.println("Inside operator "+operatorName+" data is "+data+" Thread: "+Thread.currentThread().getName());
		return data;
	}

	/*
	Result :
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	Inside operator map data is 1 Thread: main
	onComplete()
	 */
	@Test
	void defaultThreadingModeltimeOperator() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.delayElement(Duration.ofSeconds(1))
				.map(data -> log("map", data))
				.subscribe(System.out::println);
	}

	/*
	Result:
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	It did not went to log method
	 */

	@Test
	void defaultThreadingModeltimeOperatorThread() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.delayElement(Duration.ofSeconds(1))
				.map(data -> log("map", data))
				.subscribe(System.out::println);
		Thread.sleep(2000);
	}
	/*
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	Inside operator map data is 1 Thread: parallel-1
	map : 1
	 */

	@Test
	void schedulerImmediateExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.publishOn(Schedulers.immediate())
				.map(data -> log("map", data)).subscribe(System.out::println);
	}

	/*
	Result :
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	Inside operator map data is 1 Thread: main
	onComplete()
	 */

	@Test
	void schedulerImmediateExampleThread() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.delayElement(Duration.ofSeconds(1))
				.publishOn(Schedulers.immediate())
				.delayElement(Duration.ofSeconds(1))
				.map(data -> log("map", data)).subscribe(System.out::println);
		Thread.sleep(2000);
	}
	/*
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	Inside operator map data is 1 Thread: parallel-2
	map : 1
	 */

	@Test
	void schedulerSingleExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.publishOn(Schedulers.single())
				.map(data -> log("map", data)).subscribe(System.out::println);
	}

	/*
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	Inside operator map data is 1 Thread: single-1
	true
	 */

	@Test
	void schedulerboundedElasticExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.publishOn(Schedulers.boundedElastic())
				.map(data -> log("map", data)).subscribe(System.out::println);
	}
	/*
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	Inside operator map data is 1 Thread: boundedElastic-1
	true
	 */

	@Test
	void schedulerWithExecutorServiceExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.publishOn(Schedulers.fromExecutorService(executorService()))
				.map(data -> log("map", data)).subscribe(System.out::println);
	}

	public ExecutorService executorService() {
			return Executors.newFixedThreadPool(10);
	}

	/*
	onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	request(unbounded)
	onNext(1)
	onComplete()
	Inside operator map data is 1 Thread: pool-3-thread-1
	true
	 */

	@Test
	void schedulerWithExecutorServiceFluxExample() {
		Flux.just("1","2","3").log()/*.map(data -> Integer.parseInt(data))*/
				.publishOn(Schedulers.fromExecutorService(executorService()))
				.map(data -> log("map", data)).subscribe(System.out::println);
	}
	/*
	onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
	request(256)
	onNext(1)
	onNext(2)
	onNext(3)
	onComplete()
	Inside operator map data is 1 Thread: pool-3-thread-2
	map : 1
	Inside operator map data is 2 Thread: pool-3-thread-2
	map : 2
	Inside operator map data is 3 Thread: pool-3-thread-2
	map : 3
	 */

	@Test
	void SubscribeOnschedulerSingleExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.single())
				.map(data -> log("map", data)).subscribe(System.out::println);
	}

	/*
	single-1] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	single-1] reactor.Mono.Just.1        : | request(unbounded)
	single-1] reactor.Mono.Just.1        : | onNext(1)
	Inside operator map data is 1 Thread: single-1
	map : 1
	single-1] reactor.Mono.Just.1        : | onComplete()
	 */

	@Test
	void SubscribeOnWithMultipleMapExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 2", data+" 2"))
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	single-1] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	single-1] reactor.Mono.Just.1        : | request(unbounded)
	single-1] reactor.Mono.Just.1        : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: single-1
	Inside operator map 3 data is map 2 : 1 2 3 Thread: single-1
	map 3 : map 2 : 1 2 3
	single-1] reactor.Mono.Just.1        : | onComplete()
	 */

	@Test
	void SubscribeOnandPublishOnExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.boundedElastic())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	single-1] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	single-1] reactor.Mono.Just.1        : | request(unbounded)
	single-1] reactor.Mono.Just.1        : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: single-1
	single-1] reactor.Mono.Just.1        : | onComplete()
	Inside operator map 3 data is map 2 : 1 2 3 Thread: boundedElastic-1
	map 3 : map 2 : 1 2 3
	 */

	@Test
	void PublishOnWithMultipleMapExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.boundedElastic())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	main] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	main] reactor.Mono.Just.1        : | request(unbounded)
	main] reactor.Mono.Just.1        : | onNext(1)
	Inside operator map 2 data is 1 2 Thread: main
	main] reactor.Mono.Just.1        : | onComplete()
	Inside operator map 3 data is map 2 : 1 2 3 Thread: boundedElastic-1
	map 3 : map 2 : 1 2 3
	 */

	/*The first Scheduler of SubscribeOn takes Priority*/
	@Test
	void SubscribeOnWithSingleElasticMultipleMapExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.map(data -> log("map 2", data+" 2"))
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	[oundedElastic-1] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	[oundedElastic-1] reactor.Mono.Just.1        : | request(unbounded)
	[oundedElastic-1] reactor.Mono.Just.1        : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: boundedElastic-1
	Inside operator map 3 data is map 2 : 1 2 3 Thread: boundedElastic-1
	map 3 : map 2 : 1 2 3
	[oundedElastic-1] reactor.Mono.Just.1        : | onComplete()
	 */

	@Test
	void SubscribeOnWithSingleElasticParallelMultipleMapExample() {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.parallel())
				.map(data -> log("map 2", data+" 2"))
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	parallel-1] reactor.Mono.Just.1        : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	parallel-1] reactor.Mono.Just.1        : | request(unbounded)
	parallel-1] reactor.Mono.Just.1        : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: parallel-1
	Inside operator map 3 data is map 2 : 1 2 3 Thread: parallel-1
	map 3 : map 2 : 1 2 3
	parallel-1] reactor.Mono.Just.1        : | onComplete()
	 */

	@Test
	void SubscribeOnPublishOnWithSingleElasticParallelMultipleMapExample() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.parallel())
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.single())
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	parallel-1] reactor.Mono.Just.1      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	parallel-1] reactor.Mono.Just.1      : | request(unbounded)
	parallel-1] reactor.Mono.Just.1      : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: parallel-1
	parallel-1] reactor.Mono.Just.1      : | onComplete()
	Inside operator map 3 data is map 2 : 1 2 3 Thread: single-1
	map 3 : map 2 : 1 2 3
	 */

	@Test
	void SubscribeOnPublishOnWithParallelElasticParallelMultipleMapExample() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.parallel())
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.parallel())
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	parallel-1] reactor.Mono.Just.1      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	parallel-1] reactor.Mono.Just.1      : | request(unbounded)
	parallel-1] reactor.Mono.Just.1      : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: parallel-1
	 */

	@Test
	void SubscribeOnPublishOnWithParallelElasticParallelMultipleMapWithThreadExample() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.parallel())
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.parallel())
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
		Thread.sleep(2000);
	}

	/*
	parallel-1] reactor.Mono.Just.1      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	parallel-1] reactor.Mono.Just.1      : | request(unbounded)
	parallel-1] reactor.Mono.Just.1      : | onNext(1)
	Inside operator map 2 data is 1 2 Thread: parallel-1
	parallel-1] reactor.Mono.Just.1      : | onComplete()
	Inside operator map 3 data is map 2 : 1 2 3 Thread:  parallel-2
	map 3 : map 2 : 1 2 3
	 */



	@Test
	void SubscribeOnPublishOnWithElasticParallelMultipleMapExample() throws InterruptedException {
		Mono.just("1").log()/*.map(data -> Integer.parseInt(data))*/
				.subscribeOn(Schedulers.parallel())
				.map(data -> log("map 2", data+" 2"))
				.publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.single())
				.map(data -> log("map 3", data+" 3"))
				.subscribe(System.out::println);
	}
	/*
	parallel-1] reactor.Mono.Just.1      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
	parallel-1] reactor.Mono.Just.1      : | request(unbounded)
	parallel-1] reactor.Mono.Just.1      : | onNext(1)

	Inside operator map 2 data is 1 2 Thread: parallel-1
	parallel-1] reactor.Mono.Just.1      : | onComplete()
	Inside operator map 3 data is map 2 : 1 2 3 Thread: boundedElastic-2
	map 3 : map 2 : 1 2 3
	 */

	/* Threads of count less than 10 will be operated.*/
	@Test
	void runOnPublishOnWithParallelElasticParallelMultipleMapWithThreadExample(){
		System.out.println("Number of cores "+Runtime.getRuntime().availableProcessors());
		Flux.range(1,10).log()/*.map(data -> Integer.parseInt(data))*/
				.parallel()
				.runOn(Schedulers.parallel())
				.map(data -> Intlog("map 2", data))
				.subscribe(System.out::println);
	}
	/*
	Number of cores 8
	main] reactor.Flux.Range.1      : | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
	main] reactor.Flux.Range.1      : | request(256)
	main] reactor.Flux.Range.1      : | onNext(1)
	main] reactor.Flux.Range.1      : | onNext(2)
	main] reactor.Flux.Range.1      : | onNext(3)
	main] reactor.Flux.Range.1      : | onComplete()
	Inside operator map 2 data is 1 Thread: parallel-1
	1
	Inside operator map 2 data is 3 Thread: parallel-3
	3
	Inside operator map 2 data is 2 Thread: parallel-2
	2
	 */


}
