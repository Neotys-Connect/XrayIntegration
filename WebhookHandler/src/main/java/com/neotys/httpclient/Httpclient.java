package com.neotys.httpclient;


import com.neotys.xray.Logger.NeoLoadLogger;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.multipart.MultipartForm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicReference;

public class Httpclient {

    private WebClient client;
    private Vertx vertx;
    private NeoLoadLogger logger;
    private int serverport;
    private String serverhost;
    private Optional<String> api_path;

    public Httpclient(Vertx vertx,boolean ssl) {
        this.vertx=vertx;

        client=WebClient.create(vertx,new WebClientOptions().setSsl(ssl).setLogActivity(true));
        logger=new NeoLoadLogger(this.getClass().getName());

    }

    public void setSsl(boolean ssl)
    {

    }

    public int getServerport() {
        return serverport;
    }

    public void setServerport(int serverport) {
        this.serverport = serverport;
    }

    public String getServerhost() {
        return serverhost;
    }

    public void setServerhost(String serverhost) {
        this.serverhost = serverhost;
    }

    public Future<JsonObject> sendMultiPartObjects(String uri, HashMap<String,String> headers, List<MultiFormOject> object, Optional<String> user, Optional<String> password) throws HttpException {

        Future<JsonObject> future=Future.future();
        if(object==null && object.size()<=1 )
        {
            logger.error("The files to send is null ");
            throw new HttpException("The MultiPart request needs a list one file ");
        }

        HttpRequest<Buffer> request = client.post(serverport,serverhost,uri);


        MultipartForm form=MultipartForm.create();
        object.stream().forEach(multiFormOject -> {
            form.binaryFileUpload(multiFormOject.getParameterName(),multiFormOject.getFilename(),multiFormOject.getPath(),multiFormOject.getContentType());
                  });

        MultiMap header=((HttpRequest) request).headers();
        header.addAll(headers);
        if(user.isPresent()&& password.isPresent())
        {
            request.basicAuthentication(user.get(),password.get());
        }
        request.putHeaders(header)
                .sendMultipartForm(form,handler->{
                    if(handler.succeeded())
                    {
                        logger.debug("Request sent successfuly - uri :"+uri+" payload :"+object.toString());
                        logger.debug("Received the following response :"+ handler.result().bodyAsString() +" with the message "+handler.result().statusMessage() );

                        future.complete(handler.result().bodyAsJsonObject());
                    }
                    else
                    {

                        logger.error("Issue to get response ");
                        if(handler.result()!=null) {
                            future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                            logger.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                        }
                        else
                        {
                            logger.error("Error to get the response " ,handler.cause());
                            future.fail("Error to get the response " + handler.cause().getMessage());
                        }
                    }

                });

        return future;
    }
    public Future<String> sendJsonObjectStringResult(String uri, HashMap<String,String> headers, JsonObject object)
    {

        Future<String> future=Future.future();
        HttpRequest<Buffer> request = client.post(serverport,serverhost,uri);

        MultiMap header=((HttpRequest) request).headers();
        header.addAll(headers);
        request.putHeaders(header)
                .expect(ResponsePredicate.JSON)
                .expect(ResponsePredicate.status(200,300))
                .sendJson(object,handler->{
                    if(handler.succeeded())
                    {
                        logger.debug("Request sent successfuly - uri :"+uri+" payload :"+object.toString());
                        logger.debug("Received the following response :"+ handler.result().bodyAsString());
                        future.complete(handler.result().bodyAsString());

                    }
                    else
                    {
                        logger.error("Issue to receive response ");
                        if(handler.result()!=null) {
                            logger.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                            future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                        }
                        else {
                            logger.error("no Response ", handler.cause());
                            future.fail("no Response " + handler.cause().getMessage());

                        }

                    }

                });

        return future;
    }

    public Future<JsonObject> sendJsonObject(String uri, HashMap<String,String> headers, JsonObject object)
    {

        Future<JsonObject> future=Future.future();
        HttpRequest<Buffer> request = client.post(serverport,serverhost,uri);

        MultiMap header=((HttpRequest) request).headers();
        header.addAll(headers);
        request.putHeaders(header)
                .expect(ResponsePredicate.JSON)
                .expect(ResponsePredicate.status(200,300))
                .sendJson(object,handler->{
                    if(handler.succeeded())
                    {
                        logger.debug("Request sent successfuly - uri :"+uri+" payload :"+object.toString());
                        future.complete(handler.result().bodyAsJsonObject());
                        logger.debug("Received the following response :"+ handler.result().toString());
                    }
                    else
                    {
                        logger.error("Issue to get response ");
                        if(handler.result()!=null) {
                            future.fail("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                            logger.error("Response code :" + handler.result().statusCode() + " and response  " + handler.result().bodyAsString());
                        }
                        else {
                            future.fail("no Response " + handler.cause().getMessage());
                            logger.error("no Response ", handler.cause());
                        }

                    }

                });

        return future;
    }
}
