package com.neotys.httpclient;


import com.neotys.xray.Logger.NeoLoadLogger;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.multipart.MultipartForm;

import javax.net.ssl.TrustManagerFactory;
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

        client=WebClient.create(vertx,new WebClientOptions().setSsl(ssl).setVerifyHost(false).setLogActivity(true).setFollowRedirects(true).setTrustAll(true));
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

    public Future<String> sendGetRequest(String serverhost, String serverport ,String uri,boolean ssl)
    {

        Future<String> future=Future.future();

        HttpRequest<Buffer> request=client.get(Integer.parseInt(serverport),serverhost,uri).ssl(ssl);

        request.send(httpResponseAsyncResult -> {
           if(httpResponseAsyncResult.succeeded())
           {
               logger.debug("Request sent successfuly - uri :"+uri);
               logger.debug("Received the following response :"+ httpResponseAsyncResult.result().bodyAsString() +" with the message "+httpResponseAsyncResult.result().statusMessage() );

               future.complete(httpResponseAsyncResult.result().bodyAsString());
           }
           else
           {
               logger.debug("Unable to get a sucessful response");
               if(httpResponseAsyncResult.result()!=null) {
                   future.fail("Response code :" + httpResponseAsyncResult.result().statusCode() + " and response  " + httpResponseAsyncResult.result().bodyAsString());
                   logger.error("Response code :" + httpResponseAsyncResult.result().statusCode() + " and response  " + httpResponseAsyncResult.result().bodyAsString());
               }
               else
               {
                   logger.error("Error to get the response " ,httpResponseAsyncResult.cause());
                   future.fail("Error to get the response " + httpResponseAsyncResult.cause().getMessage());
               }

           }
       });

        return future;
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
            logger.debug("Basic auth added username" +user.get()+" pass "+password.get());
        }


        logger.debug("preparing the request " +uri +" with payload"+object.toString());
        logger.debug("Jira server :"+this.serverhost+" port " + String.valueOf(serverport));
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
