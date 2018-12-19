package metaWeather;

import java.time.ZonedDateTime;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.file.FileSystem;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientResponse;

public class Rx2AndVert {
	
	public static void main(String[] args) throws InterruptedException {
		Vertx vertx = Vertx.vertx();
		HttpClient httpClient = vertx.createHttpClient();
		FileSystem fileSystem = vertx.fileSystem();
		fileSystem
			.rxReadFile("cities.txt").toFlowable()
			.flatMap(buffer ->Flowable.fromArray(buffer.toString().split("\\r?\\n")))
				//.doOnNext(city -> System.out.println(city))
			.flatMap(city -> MetaWeatherClient.searchByCityName(httpClient, city))
			.flatMap(HttpClientResponse::toFlowable)
			.map(extractingWoeid())
				//.doOnNext(id -> System.out.println(id))
			.flatMap(id -> MetaWeatherClient.getDataByPlaceId(httpClient, id))
			.flatMap(toBufferFlowable())
			.map(Buffer::toJsonObject)
			.map(toCityAndDayLength())
			.subscribe(System.out::println, Throwable::printStackTrace,
					() -> System.out.println("Done!"));
		
		Thread.sleep(5000);
		
		vertx.close();
	}
	
	static Function<Buffer, Long> extractingWoeid() {
		return cityBuffer -> cityBuffer.toJsonArray().getJsonObject(0).getLong("woeid");
	}

	static Function<HttpClientResponse, Publisher<? extends Buffer>> toBufferFlowable() {
		return response -> response.toObservable().reduce(Buffer.buffer(), Buffer::appendBuffer).toFlowable();
	}

	static Function<JsonObject, DayLength> toCityAndDayLength() {
		return json -> {
			ZonedDateTime sunRise = ZonedDateTime.parse(json.getString("sun_rise"));
			ZonedDateTime sunSet = ZonedDateTime.parse(json.getString("sun_set"));
			String cityName = json.getString("title");
			return new DayLength(cityName, sunSet.toEpochSecond() - sunRise.toEpochSecond());
		};
	}
}
