package metaWeather;

import io.reactivex.Flowable;
import io.vertx.core.http.RequestOptions;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientRequest;
import io.vertx.reactivex.core.http.HttpClientResponse;

public class MetaWeatherClient {
	
	private static RequestOptions metawether = new RequestOptions()
		      .setHost("www.metaweather.com")
		      .setPort(443)
		      .setSsl(true);
	
	private static Flowable<HttpClientResponse> autoPerformingReq(HttpClient httpClient, String uri) {
		HttpClientRequest req = httpClient.get(new RequestOptions(metawether).setURI(uri));
		return req.toFlowable().doOnSubscribe(subscription -> req.end());
	}
	
	public static Flowable<HttpClientResponse> searchByCityName(HttpClient httpClient, String cityName) {
		HttpClientRequest req = httpClient.get(new RequestOptions()
				.setHost("www.metaweather.com")
				.setPort(443)
				.setSsl(true)
				.setURI(String.format("/api/location/search/?query=%s", cityName)));
		return req
				.toFlowable()
				.doOnSubscribe(subscription -> req.end());
	}
	
	public static Flowable<HttpClientResponse> getDataByPlaceId(HttpClient httpClient, long placeId) {
		return autoPerformingReq(httpClient, String.format("/api/location/%s/", placeId));
	}
}
