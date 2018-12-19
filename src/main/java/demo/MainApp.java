package demo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class MainApp {

	static String result;
	static String even;
	static String odd;
	static int irs;

	public static void main(String[] args) {
		demo10();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void demo1() {
		Observable observable = Observable.just(new MyObject("001", "hello"));
		observable.subscribe(System.out::println);
		observable.subscribe(s -> System.out.println(s));

	}

	public static void demo2() {
		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		Observable<String> observable = Observable.fromArray(letters);
		result = "";
		observable.subscribe(i -> result += i, // OnNext
				Throwable::printStackTrace, // OnError
				() -> {
					result += "_Completed";
					System.out.println(result);
				} // OnCompleted
		);
	}

	public static void demo3() {
		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		Observable<String> observable = Observable.fromArray(letters);
		result = "";
		observable.map(s -> s += "k").subscribe(System.out::println);
	}

	public static void demo4() {
		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		Observable<String> observable = Observable.fromArray(letters);
		result = "";
		observable.scan(new StringBuilder(), StringBuilder::append).subscribe(System.out::println);
	}

	public static void demo5() {
		Observable<Integer> observable = Observable.fromArray(1, 2, 3, 4, 5, 6, 8, 9);
		even = odd = "";
		observable.groupBy(i -> ((i % 2) == 0) ? "EVEN" : "ODD").subscribe(group -> group.subscribe((number) -> {
			if (group.getKey().toString().equals("EVEN")) {
				even += number;
			} else {
				odd += number;
			}
		}));
		System.out.println("ODD " + odd);
		System.out.println("EVEN " + even);
	}

	public static void demo6() {
		Observable<Integer> observable = Observable.fromArray(1, 2, 3, 4, 5, 6, 8, 9);
		odd = "";
		// Equivalent takeWhile
		observable.filter(i -> ((i % 2) == 1)).defaultIfEmpty(69).subscribe(num -> odd += num);
		System.out.println("ODD " + odd);
	}

	public static void demo7() {
		Single<String> single = Single.just("hello");
		single.subscribe(System.out::println, Throwable::printStackTrace);
	}

	public static void demo8() {
		irs = 0;
		Observer<Integer> obs = new Observer<Integer>() {

			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void onNext(Integer i) {
				irs += i;
				System.out.println(irs);
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.println("Complete");
			}
		};

		PublishSubject<Integer> subject = PublishSubject.create();
		subject.subscribe(obs);
		subject.onNext(1);
		subject.onNext(2);
		subject.onNext(3);
		subject.onComplete();
	}

	public static void demo9() {
		result = "";
		Observable<Character> values = Observable.using(() -> "Hello World!", r -> {
			return Observable.create(o -> {
				for (Character c : r.toCharArray()) {
					// System.out.println(c);
					o.onNext(c);
				}
				o.onComplete();
			});
		}, rs -> System.out.println("Disposed: " + rs));
		values.subscribe(v -> result += v, Throwable::printStackTrace);
		System.out.println(result);
	}

	public static void demo10() {
		List<Object> arr = new ArrayList<>();
		arr.add(1);
		arr.add(new MyObject("L15L", "Aishiteru"));
		arr.add("hi");
		arr.add("hello");
		arr.add(20.08);

		Observable<Object> observable = Observable.fromArray(arr.toArray());
		observable.subscribe(System.out::println, Throwable::printStackTrace, () -> {
			System.out.println("hehe");
		});
	}
	
	public static void demo11() {
		Observable<Integer> fibonacci =
		        Observable.just(0)
		                  .repeat()
		                  .scan(new int[]{0, 1}, (a, b) -> new int[]{a[1], a[0] + a[1]})
		                  .map(a -> a[1]);
		                  //.doOnNext(i -> System.out.println("a[1] = " + i));
		fibonacci.take(10).subscribe(System.out::println);
	}
}
