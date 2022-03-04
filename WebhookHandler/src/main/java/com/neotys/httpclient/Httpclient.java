package com.neotys.httpclient;


import com.neotys.xray.Logger.NeoLoadLogger;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.multipart.MultipartForm;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Httpclient {

	public static final String UTF_8 = StandardCharsets.UTF_8.toString();
	private WebClient client;
	private final NeoLoadLogger LOGGER;
	private int serverPort;
	private String serverHost;

	public Httpclient(Vertx vertx, boolean ssl) {
		this(vertx, ssl, false);
	}

	public Httpclient(Vertx vertx, boolean ssl, boolean useProxy) {
		client = WebClient.create(vertx, generateClientOption(ssl, useProxy));
		LOGGER = new NeoLoadLogger(this.getClass().getName());

	}

	private WebClientOptions generateClientOption(boolean ssl, boolean useProxy) {
		final WebClientOptions webClientOptions = new WebClientOptions()
				.setSsl(ssl)
				.setVerifyHost(false)
				.setLogActivity(true)
				.setFollowRedirects(true)
				.setTrustAll(true);
		if (useProxy) {
			configureProxy(webClientOptions, ssl);
		}
		return webClientOptions;
	}

	private void configureProxy(final WebClientOptions webClientOptions, boolean ssl) {
		final String proxyEnvironment = ssl ? "https_proxy" : "http_proxy";
		final String proxySettings = System.getenv(proxyEnvironment);
		if (isNotNullAndNotEmpty(proxySettings)) {
			final ProxyOptions proxyOptions = new ProxyOptions();
			proxyOptions.setType(ProxyType.HTTP);
			try {
				final URI url = URI.create(proxySettings);
				decorateWithCredentials(url, proxyOptions);
				proxyOptions.setHost(url.getHost());
				if (url.getPort() >= 0) {
					proxyOptions.setPort(url.getPort());
				}
				webClientOptions.setProxyOptions(proxyOptions);
			} catch (Exception e) {
				LOGGER.warn("Exception occurs on proxy settings", e);
			}
		}
	}


	private boolean isNotNullAndNotEmpty(String proxySettings) {
		return proxySettings != null && !proxySettings.isEmpty();
	}


	private void decorateWithCredentials(URI url, final ProxyOptions proxyOptions) throws UnsupportedEncodingException {
		final String userInfo = url.getRawUserInfo();
		if (isNotNullAndNotEmpty(userInfo)) {
			final String[] split = userInfo.split(":");
			if (split.length > 1) {
				proxyOptions.setPassword(URLDecoder.decode(split[1], UTF_8));
			}
			proxyOptions.setUsername(URLDecoder.decode(split[0], UTF_8));
		}
	}


	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverport) {
		this.serverPort = serverport;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverhost) {
		this.serverHost = serverhost;
	}

	public Future<String> sendGetRequest(String serverhost, String serverport, String uri, boolean ssl) {

		Future<String> future = Future.future();

		HttpRequest<Buffer> request = client.get(Integer.parseInt(serverport), serverhost, uri).ssl(ssl);

		request.send(httpResponseAsyncResult -> {
			if (httpResponseAsyncResult.succeeded()) {
				LOGGER.debug("Request sent successfully - uri :" + uri);
				LOGGER.debug("Received the following response :" + httpResponseAsyncResult.result().bodyAsString() + " with the message " + httpResponseAsyncResult.result().statusMessage());

				future.complete(httpResponseAsyncResult.result().bodyAsString());
			} else {
				LOGGER.debug("Unable to get a successful response");
				if (httpResponseAsyncResult.result() != null) {
					future.fail("Response code :" + httpResponseAsyncResult.result().statusCode() + " and response  " + httpResponseAsyncResult.result().bodyAsString());
					LOGGER.error("Response code :" + httpResponseAsyncResult.result().statusCode() + " and response  " + httpResponseAsyncResult.result().bodyAsString());
				} else {
					LOGGER.error("Error to get the response ", httpResponseAsyncResult.cause());
					future.fail("Error to get the response " + httpResponseAsyncResult.cause().getMessage());
				}

			}
		});

		return future;
	}

	public Future<JsonObject> sendMultiPartObjects(String uri, HashMap<String, String> headers, List<MultiFormOject> object, Optional<String> user, Optional<String> password) throws HttpException {

		Future<JsonObject> future = Future.future();
		if (object == null && object.size() <= 1) {
			LOGGER.error("The files to send is null ");
			throw new HttpException("The MultiPart request needs a list one file ");
		}

		HttpRequest<Buffer> request = client.post(serverPort, serverHost, uri);


		MultipartForm form = MultipartForm.create();
		object.stream().forEach(multiFormOject -> {
			form.binaryFileUpload(multiFormOject.getParameterName(), multiFormOject.getFilename(), multiFormOject.getPath(), multiFormOject.getContentType());
		});

		MultiMap header = ((HttpRequest) request).headers();
		header.addAll(headers);
		if (user.isPresent() && password.isPresent()) {
			request.basicAuthentication(user.get(), password.get());
			LOGGER.debug("Basic auth added username" + user.get() + " pass " + password.get());
		}


		LOGGER.debug("preparing the request " + uri + " with payload" + object.toString());
		LOGGER.debug("Jira server :" + this.serverHost + " port " + String.valueOf(serverPort));
		request.putHeaders(header)
				.sendMultipartForm(form, handler -> {
					if (handler.succeeded()) {
						LOGGER.debug("Request sent successfuly - uri :" + uri + " payload :" + object.toString());
						LOGGER.debug("Received the following response :" + handler.result().bodyAsString() + " with the message " + handler.result().statusMessage());

						future.complete(handler.result().bodyAsJsonObject());
					} else {

						LOGGER.error("Issue to get response ");
						if (handler.result() != null) {
							future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
							LOGGER.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
						} else {
							LOGGER.error("Error to get the response ", handler.cause());
							future.fail("Error to get the response " + handler.cause().getMessage());
						}
					}

				});

		return future;
	}

	public Future<String> sendJsonObjectStringResult(String uri, HashMap<String, String> headers, JsonObject object) {

		Future<String> future = Future.future();
		HttpRequest<Buffer> request = client.post(serverPort, serverHost, uri);

		MultiMap header = request.headers();
		header.addAll(headers);
		request.putHeaders(header)
				.expect(ResponsePredicate.JSON)
				.expect(ResponsePredicate.status(200, 300))
				.sendJson(object, handler -> {
					if (handler.succeeded()) {
						LOGGER.debug("Request sent successfuly - uri :" + uri + " payload :" + object.toString());
						LOGGER.debug("Received the following response :" + handler.result().bodyAsString());
						future.complete(handler.result().bodyAsString());

					} else {
						LOGGER.error("Issue to receive response ");
						if (handler.result() != null) {
							LOGGER.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
							future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
						} else {
							LOGGER.error("no Response ", handler.cause());
							future.fail("no Response " + handler.cause().getMessage());

						}

					}

				});

		return future;
	}

	public Future<JsonObject> sendJsonObject(String uri, HashMap<String, String> headers, JsonObject object) {

		Future<JsonObject> future = Future.future();
		HttpRequest<Buffer> request = client.post(serverPort, serverHost, uri);

		MultiMap header = request.headers();
		header.addAll(headers);
		request.putHeaders(header)
				.expect(ResponsePredicate.JSON)
				.expect(ResponsePredicate.status(200, 300))
				.sendJson(object, handler -> {
					if (handler.succeeded()) {
						LOGGER.debug("Request sent successfuly - uri :" + uri + " payload :" + object.toString());
						future.complete(handler.result().bodyAsJsonObject());
						LOGGER.debug("Received the following response :" + handler.result().toString());
					} else {
						LOGGER.error("Issue to get response ");
						if (handler.result() != null) {
							future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
							LOGGER.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
						} else {
							future.fail("no Response " + handler.cause().getMessage());
							LOGGER.error("no Response ", handler.cause());
						}

					}

				});

		return future;
	}
}
