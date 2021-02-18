package com.neotys.xray.HttpResult;

import com.google.gson.*;
import com.neotys.ascode.swagger.client.ApiClient;
import com.neotys.ascode.swagger.client.ApiException;
import com.neotys.ascode.swagger.client.api.ResultsApi;
import com.neotys.ascode.swagger.client.model.*;
import com.neotys.httpclient.HttpException;
import com.neotys.httpclient.Httpclient;
import com.neotys.httpclient.MultiFormOject;
import com.neotys.xray.Logger.NeoLoadLogger;
import com.neotys.xray.conf.NeoLoadException;
import com.neotys.xray.datamodel.NeoLoadXrayDescription;
import com.neotys.xray.datamodel.result.CloudAuth;
import com.neotys.xray.datamodel.result.testexecjson.Fields;
import com.neotys.xray.datamodel.result.Robotxml.Robot;
import com.neotys.xray.datamodel.result.testexecjson.NeoLoadTestDetails;
import com.neotys.xray.datamodel.result.testjson.Field;
import com.neotys.xray.datamodel.result.testjson.NeoLoadRunDetails;
import com.sun.org.apache.xpath.internal.operations.Mult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.multipart.MultipartForm;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import sun.net.www.http.HttpClient;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.neotys.xray.conf.Constants.*;

public class NeoLoadHttpHandler {
    private String testid;
    private Optional<String> neoload_Web_Url;
    private Optional<String> neoload_API_Url;
    private Optional<String> neoload_API_PORT;
    private String neoload_API_key;
    private NeoLoadLogger logger;
    private ApiClient apiClient;
    private Optional<String> managedHost;
    private Optional<String> managedport;
    private Optional<String> managedwebHost;
    private Optional<String> cloudport;
    private Optional<String> cloudhost;
    private Optional<String> cloudWebhost;
    private Optional<String> clien_id;
    private Optional<String> client_secret;
    private Optional<String> user;
    private Optional<String> password;
    private Optional<String> jira_API_PATH;
    private ResultsApi resultsApi;
    private Optional<String> customfield_Testplan;
    private Optional<String> customfield_Environment;
    private Optional<String> customfield_Revision;
    private boolean isCloud;
    private String projectName;
    private String scenarioName;
    private String testname;
    private long testStart;
    private long testEnd;
    private String status;
    private  TestStatistics statistics;
    private String maxVu;
    private String testoverviewpng;
    private boolean ssl;
    private String nl_api_host;

    public NeoLoadHttpHandler(String testid, String maxVu, String urlpngoverview) throws NeoLoadException {
        this.testid=testid;
        logger=new NeoLoadLogger(this.getClass().getName());
        logger.setTestid(testid);
        this.maxVu=maxVu;
        getEnvVariables();
        generateApiUrl();

        apiClient=new ApiClient();
        apiClient.setBasePath(HTTPS+neoload_API_Url.get());

        apiClient.setApiKey(neoload_API_key);
        resultsApi=new ResultsApi(apiClient);
        testoverviewpng=urlpngoverview;
    }

    private Future<String> generateCloudAuth(HashMap<String,String> header,Httpclient client)
    {
        logger.debug("Auth to the Jira Cloud");
        //----generate authentification token-------------
        Future<String> stringFuture=Future.future();
        CloudAuth cloudAuth=new CloudAuth(clien_id.get(),client_secret.get());
        header.put("Content-Type","application/json");
        io.vertx.core.json.JsonObject authjson = new io.vertx.core.json.JsonObject(Json.encode(cloudAuth));
        client.setServerhost(cloudhost.get());

        client.setServerport(Integer.parseInt(cloudport.get()));
        Future<String> response=client.sendJsonObjectStringResult(XRAY_URL_CLOUD_AUTH,header,authjson);
        String token;
        response.setHandler(result-> {
            if(result.succeeded())
            {
                logger.debug("Auth REsponse received : " +result.result());
                stringFuture.complete(result.result());

            }
            else
            {
                stringFuture.fail("No token find in the response "+result.cause().getMessage());
                logger.error("No token find in the response "+result.cause().getMessage(),result.cause());
            }
        });

        return stringFuture;
    }


    public Future<Boolean> sendResult(Vertx vertx) throws ApiException, JAXBException, IOException, NeoLoadException, HttpException,JsonSyntaxException {
        Future<Boolean> futureresult=Future.future();

        Httpclient client=new Httpclient(vertx,ssl);
        HashMap<String,String> header=new HashMap<>();

        Future<Boolean> testapi=testAPIConnectivity(vertx);
        testapi.setHandler(booleanAsyncResult -> {
           if(booleanAsyncResult.succeeded())
           {
               try{

                   List<MultiFormOject> multiFormOjects=generateResultsFiles();
                   if(isCloud)
                   {
                       logger.debug("Interacting with JIRA CLOUD");
                       //----genereate the auth token
                       Future<String> token=generateCloudAuth(header,client);
                       token.setHandler(result->{
                           if(result.succeeded())
                           {

                               header.put("Authorization","Bearer "+result.result().replaceAll("\"",""));
                               header.put("Content-Type","multipart/form-data");
                               //----sending resutls to xray----------
                               Future<JsonObject> response = Future.future();
                               try {
                                   response = client.sendMultiPartObjects(XRAY_URL_ROBOT_CLOUD_MULTIPART,header,multiFormOjects,user,password);
                               } catch (HttpException e) {
                                   futureresult.fail(e);
                               }
                               //--------------------------------------
                               if(response!=null) {
                                   response.setHandler(obj -> {
                                       if (obj.succeeded()) {
                                           JsonObject data = obj.result();
                                           if (data.containsKey("key")) {
                                               //-----updating neoload test witht he jira link-----------
                                               String key = data.getString("key");
                                               logger.debug("Issue id foud : "+key);
                                               TestUpdateRequest updateRequest = new TestUpdateRequest();
                                               updateRequest.description("Jira results : " + generateJiraURL(key));
                                               logger.debug("URL to reach issue : "+updateRequest.getDescription());
                                               try {
                                                   resultsApi.updateTest(updateRequest, testid);
                                                   futureresult.complete(true);
                                               } catch (ApiException e) {
                                                   futureresult.fail(e);
                                               }

                                           } else {
                                               logger.error("No issue id has been created in Jira");
                                               futureresult.fail(
                                                       new NeoLoadException("No Issue has been created in Jira - " + obj.result().toString()))
                                               ;
                                           }
                                       } else {
                                           logger.error("The test results has not been processed");
                                           futureresult.fail(  new NeoLoadException("The test results has not been processed"))
                                           ;

                                       }
                                   });
                               }
                               else
                               {
                                   futureresult.fail("Issue to send the files");
                               }
                           }
                           else
                           {
                               futureresult.fail(result.cause());
                           }
                       });
                   }
                   else
                   {
                       logger.debug("Interacting with JIRA on prem");
                       client.setServerport(Integer.parseInt(managedport.get()));
                       client.setServerhost(managedHost.get());
                       header.put("Content-Type","multipart/form-data");

                       String path;
                       if(jira_API_PATH.isPresent())
                           path=jira_API_PATH.get()+XRAY_URL_ONPREM_MULTIPART;
                       else
                           path=XRAY_URL_ONPREM_MULTIPART;
                       //----sending resutls to xray----------
                       Future<JsonObject> response=client.sendMultiPartObjects(path,header,multiFormOjects,user,password);
                       //--------------------------------------
                       response.setHandler(result->{
                           if(result.succeeded())
                           {
                               JsonObject data=result.result();
                               if(data.containsKey("testExecIssue")) {
                                   JsonObject issueObject=data.getJsonObject("testExecIssue");
                                   if (issueObject.containsKey("key")) {
                                       //-----updating neoload test witht he jira link-----------
                                       String key = issueObject.getString("key");
                                       logger.debug("Issue id found : " + key);
                                       TestUpdateRequest updateRequest = new TestUpdateRequest();
                                       updateRequest.description("Jira results : " + generateJiraURL(key));
                                       logger.debug("URL to reach issue : " + updateRequest.getDescription());
                                       try {
                                           resultsApi.updateTest(updateRequest, testid);
                                           futureresult.complete(true);
                                       } catch (ApiException e) {
                                           futureresult.fail(e);
                                       }

                                   }
                                   else
                                   {
                                       logger.error("No issue id has been created in Jira");
                                       futureresult.fail( new NeoLoadException("No Issue has been created in Jira - "+ response.toString()));
                                   }
                               }
                               else
                               {
                                   logger.error("No issue id has been created in Jira");
                                   futureresult.fail( new NeoLoadException("No Issue has been created in Jira - "+ response.toString()));
                               }
                           }
                           else
                           {
                               logger.error("The test results has not been processed");
                               futureresult.fail( new NeoLoadException("The test results has not been processed"));

                           }
                       });
                   }

               }
               catch (Exception e)
               {
                   futureresult.fail(e);
               }
           }
           else
           {
               futureresult.fail(booleanAsyncResult.cause());
           }
        });



        return futureresult;
    }

    private String generateJiraURL(String jiraid)
    {
        String url="https://";
        if(isCloud)
        {
            url+=cloudWebhost.get();
        }
        else
        {
            url+=managedwebHost.get();
        }
        url+=XRAY_URL_BROWSE+jiraid;

        return url;
    }

    private Future<Boolean> testAPIConnectivity(Vertx vertx)
    {
        Future<Boolean> future=Future.future();
        Httpclient client=new Httpclient(vertx,ssl);
        Future<String> stringFuture=client.sendGetRequest(nl_api_host,neoload_API_PORT.get(),"/explore/",ssl);
        stringFuture.setHandler(stringAsyncResult -> {
            if(stringAsyncResult.succeeded())
            {
                logger.debug("Able to get response from the api "+stringAsyncResult.result());
                future.complete(true);
            }
            else
                logger.error("Unable to reach the api ",stringAsyncResult.cause());
                future.fail(stringAsyncResult.cause());
        });

        return future;
    }

    private List<MultiFormOject> generateResultsFiles() throws ApiException, JAXBException, IOException, JsonSyntaxException, NeoLoadException {
        logger.debug("Starting to generate Results files");
        List<MultiFormOject> resultfiles=new ArrayList<>();
        logger.debug("Geting test statistics of "+testid);

        try {


            statistics = this.resultsApi.getTestStatistics(testid);

        }
        catch (ApiException e)
        {
            logger.error("Getting API error  "+e.getCode()+" body + "+e.getResponseBody() ,e);
            throw new NeoLoadException("Getting API error  "+e.getCode()+" body + "+e.getResponseBody() );
        }
        catch (Exception e)
        {
            logger.error("Technical error " ,e);
            throw new NeoLoadException("Techncial error ");

        }

        logger.debug("Geting test description field  of test "+testid);

        NeoLoadXrayDescription jsondesription=getXrayDescriptionFromTest(getTestDescription());
        if(jsondesription==null)
        {
            throw new NeoLoadException("There is no description of this test");
        }
        logger.debug("XrayContext found : "+jsondesription.getProject());
        NeoLoadTestContext context=new NeoLoadTestContext(testid,projectName,scenarioName,testname,testStart,testEnd,Optional.ofNullable(getGlobalSLAIndicators(testid)),Optional.ofNullable(getSLAPerTest(testid)), Optional.ofNullable(getSLAPerInterval(testid)),jsondesription,status,neoload_Web_Url.get(),statistics,customfield_Revision,customfield_Testplan,customfield_Environment,logger);
        Robot robotxml=new Robot(context,resultsApi);
        logger.info("Robot object generated");

        //----create the tempory folder----------
        logger.debug("Creating tempory folder");
        Path folder=createTempFolder();
        logger.debug("Folder created :"+folder.toAbsolutePath().toString());

        //-----generating the robot xml---
        logger.debug("Generating robot.xml file");
        JAXBContext jaxbContext = JAXBContext.newInstance(Robot.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        logger.debug("Robot conent" + robotxml.toString());
        marshaller.marshal(robotxml, new File(folder.toAbsolutePath().toString()+"/robot.xml"));
        String parametername;
        if(isCloud)
        {
            parametername=CLOUD_ROBOT_PARAMETERNAME;
        }
        else
            parametername=ONPREM_ROBOT_PARAMETERNAME;

        resultfiles.add(new MultiFormOject(parametername,"robot.xml",folder.toAbsolutePath().toString()+"/robot.xml","application/xml"));

        //----log for debuging the content of the files
        logger.debugfileContent(folder.toAbsolutePath().toString()+"/robot.xml");
        //----generating the descirption json file-----
        NeoLoadTestDetails fields=new NeoLoadTestDetails(context,isCloud,maxVu,testoverviewpng);
        String infoFilename;
        if(isCloud)
        {
            infoFilename=CLOUD_TEST_INFO_NAME;
        }
        else
            infoFilename=ONPREM_TEST_INFO_NAME;

        Writer writer = new FileWriter(folder.toAbsolutePath().toString()+"/"+infoFilename);
        Gson gsontofile=new GsonBuilder().create();
        writer.write(updateJsonfile(gsontofile.toJsonTree(fields),context));
        writer.flush();
        writer.close();
        logger.debug("teste info : "+fields.toString());
        resultfiles.add(new MultiFormOject("info",infoFilename,folder.toAbsolutePath().toString()+"/"+infoFilename,"application/octet-stream"));
        //-----------------------------------------------
        //----log for debuging the content of the files
        logger.debugfileContent(folder.toAbsolutePath().toString()+"/"+infoFilename);
        //----generating test json file--------------
        NeoLoadRunDetails neoLoadRunDetails=new NeoLoadRunDetails(new Field("This Test corresponds to the "+context.getProjectName() +" scenario "+context.getScenarioName() ));
        Writer writer2 = new FileWriter(folder.toAbsolutePath().toString()+"/test_info.json");
        gsontofile.toJson(neoLoadRunDetails,writer2);
        writer2.flush();
        writer2.close();
        logger.debug("teste info : "+neoLoadRunDetails.toString());
        resultfiles.add(new MultiFormOject("testInfo","test_info.json",folder.toAbsolutePath().toString()+"/test_info.json","application/octet-stream"));
        //---------------------------------------
        logger.debugfileContent(folder.toAbsolutePath().toString()+"/test_info.json");

        logger.debug("Results files generated");
        return resultfiles;

    }


    private Path createTempFolder() throws IOException {
        Path localPath = Files.createTempDirectory("Neoloadtest" + testid);

        return localPath.toAbsolutePath();

    }
    private String updateJsonfile(JsonElement jsonElement,NeoLoadTestContext context)
    {
        com.google.gson.JsonObject fieldsobject = jsonElement.getAsJsonObject().getAsJsonObject("fields");
        //----addd revistion field----
        if(customfield_Revision.isPresent()&&context.getDescription().getRevision()!=null&&context.getDescription().getRevision().isPresent()) {
            fieldsobject.addProperty(customfield_Revision.get(), context.getDescription().getRevision().get());
        }
         //---

        if(!isCloud)
        {
            if(customfield_Environment.isPresent()&&context.getDescription().getTestEnvironment()!=null&&context.getDescription().getTestEnvironment().isPresent())
            {
                JsonArray env_array=new JsonArray();
                env_array.add(context.getDescription().getTestEnvironment().get());
                fieldsobject.add(customfield_Environment.get(),env_array);

            }
            if(customfield_Testplan.isPresent()&&context.getDescription().getTestPlan()!=null&&context.getDescription().getTestPlan().isPresent())
            {
                JsonArray array=new JsonArray();
                array.add(context.getDescription().getTestPlan().get());
                fieldsobject.add(customfield_Testplan.get(),array);
            }

        }

        fieldsobject.remove("customfields");
        if(context.getDescription().getCustomFields()!=null && context.getDescription().getCustomFields().isPresent())
        {
            context.getDescription().getCustomFields().get().forEach( (s, s2) ->
            {
                fieldsobject.addProperty(s,s2);
            });
        }

        return jsonElement.toString();
    }
    private String getTestDescription() throws ApiException {
        String description;

        logger.debug("getting the description of the test");
        TestDefinition definition=resultsApi.getTest(testid);
        TestStatistics statistics=resultsApi.getTestStatistics(testid);
        if(definition!=null)
        {
            projectName=definition.getProject();
            scenarioName=definition.getScenario();
            testname=definition.getName();
            testStart=definition.getStartDate();
            status=definition.getQualityStatus().getValue();
            testEnd=definition.getEndDate();
            if(!definition.getDescription().isEmpty())
            {
                logger.debug("description of the test : "+ definition.getDescription());
                return definition.getDescription();
            }
            else
            {
                logger.info("The description field is empty");
                return null;
            }
        }
        else {
            logger.error("test not found");
            return null;
        }
    }

    private ArrayOfSLAGlobalIndicatorDefinition getGlobalSLAIndicators(String testid) throws ApiException {
        ArrayOfSLAGlobalIndicatorDefinition arrayOfSLAGlobalIndicatorDefinition=resultsApi.getTestSLAGlobalIndicators(testid,null);

        return arrayOfSLAGlobalIndicatorDefinition;
    }

    private ArrayOfSLAPerTestDefinition getSLAPerTest(String testid) throws ApiException {
        return resultsApi.getTestSLAPerTest(testid,null,null);
    }

    private ArrayOfSLAPerIntervalDefinition getSLAPerInterval(String testid) throws ApiException {
        return resultsApi.getTestSLAPerInterval(testid,null,null);
    }

    private NeoLoadXrayDescription getXrayDescriptionFromTest(String description) throws JsonSyntaxException
    {
        if(description!=null)
        {


            logger.debug("Converting Description into java Object");
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();
            NeoLoadXrayDescription xrayDescription = gson.fromJson(description, NeoLoadXrayDescription.class);
            return xrayDescription;
        }
        else
            return null;
    }

    private void generateApiUrl()
    {
        if(neoload_API_Url.isPresent()&&neoload_API_PORT.isPresent())
        {
            nl_api_host=neoload_API_Url.get();
            if(!neoload_API_Url.get().contains(API_URL_VERSION))
                neoload_API_Url=Optional.of(neoload_API_Url.get()+":"+neoload_API_PORT.get()+API_URL_VERSION);
        }
        logger.debug("API url for nl web : "+neoload_API_Url.get());
    }
    private void getEnvVariables() throws NeoLoadException {

        logger.debug("retrieve the environement variables for neoload  neoload service ");
        neoload_API_key=System.getenv(SECRET_API_TOKEN);
        if(neoload_API_key==null)
        {
            logger.error("No API key defined");
            throw new NeoLoadException("No API key is defined");
        }
        neoload_API_PORT=Optional.ofNullable(System.getenv(SECRET_NL_API_PORT)).filter(o->!o.isEmpty());
        if(!neoload_API_PORT.isPresent())
            neoload_API_PORT=Optional.of(DEFAULT_NL_API_PORT);

        neoload_API_Url= Optional.ofNullable(System.getenv(SECRET_NL_API_HOST)).filter(o->!o.isEmpty());
        if(!neoload_API_Url.isPresent())
            neoload_API_Url=Optional.of(DEFAULT_NL_SAAS_API_URL);

        neoload_Web_Url=Optional.ofNullable(System.getenv(SECRET_NL_WEB_HOST)).filter(o->!o.isEmpty());
        if(!neoload_Web_Url.isPresent())
            neoload_Web_Url=Optional.of(SECRET_NL_WEB_HOST);

        if(System.getenv(SECRET_SSL)!=null&& !System.getenv(SECRET_SSL).isEmpty())
        {
            ssl=Boolean.parseBoolean(System.getenv(SECRET_SSL));

        }
        else
            ssl=false;

        this.isCloud=false;

        jira_API_PATH=Optional.ofNullable(System.getenv(SECRET_JIRA_API_PATH)).filter(o->!o.isEmpty());

        managedHost=Optional.ofNullable(System.getenv(SECRET_MANAGED_HOST)).filter(o->!o.isEmpty());
        if(managedHost.isPresent())
        {
            logger.debug("A Managed hostname is defined");
            managedport=Optional.ofNullable(System.getenv(SECRET_MANAGED_PORT)).filter(o->!o.isEmpty());
            if(!managedport.isPresent())
                managedport=Optional.of(DEFAULT_MANAGED_PORT);

            managedwebHost=Optional.ofNullable(System.getenv(SECRET_MANAGED_WEBHOST)).filter(o->!o.isEmpty());
            if(!managedwebHost.isPresent())
                throw new NeoLoadException("The Web Host is required");

            user=Optional.ofNullable(System.getenv(SECRET_USERNAME)).filter(o->!o.isEmpty());
            if(!user.isPresent())
                throw new NeoLoadException("The user environment varaible is missing");

            password=Optional.ofNullable(System.getenv(SECRET_PASSWORD)).filter(o->!o.isEmpty());
            if(!password.isPresent())
                throw new NeoLoadException("The password environment varaible is missing");

            isCloud=false;

            customfield_Environment=Optional.ofNullable(System.getenv(SECRET_CUSTOMFIELD_ENVIRONMENT)).filter(o->!o.isEmpty());
            if(!customfield_Environment.isPresent())
                throw new NeoLoadException("The custom Field Environment id needs to be defined");

            customfield_Testplan=Optional.ofNullable(System.getenv(SECRET_CUSTOMFIELD_TESTPLAN)).filter(o->!o.isEmpty());
            if(!customfield_Testplan.isPresent())
                throw new NeoLoadException("The custom Field Test Plan id needs to be defined");

            customfield_Revision=Optional.ofNullable(System.getenv(SECRET_CUSTOMFIELD_REVISION)).filter(o->!o.isEmpty());
            isCloud=false;

        }
        else
        {
            user=Optional.empty();
            password=Optional.empty();
            logger.debug("A Cloud hostname is defined");

            //----case of a cloud environment----
            cloudhost=Optional.ofNullable(System.getenv(SECRET_CLOUD_HOST)).filter(o->!o.isEmpty());
            this.isCloud=true;

            if(!cloudhost.isPresent())
                throw new NeoLoadException("The cloud Host environment variable is missing");

            cloudWebhost=Optional.ofNullable(System.getenv(SECRET_CLOUD_WEBHOST)).filter(o->!o.isEmpty());
            if(!cloudWebhost.isPresent())
                throw new NeoLoadException("The Web Host is required");

            cloudport=Optional.ofNullable(System.getenv(SECRET_CLOUD_PORT)).filter(o->!o.isEmpty());
            if(!cloudport.isPresent())
                cloudport=Optional.of(DEFAULT_CLOUD_PORT);

            clien_id=Optional.ofNullable(System.getenv(SECRET_CLIENTID)).filter(o->!o.isEmpty());
            if(!clien_id.isPresent())
                throw new NeoLoadException("The client_id environment varaible is missing");

            client_secret=Optional.ofNullable(System.getenv(SECRET_CLIENT_SECRET)).filter(o->!o.isEmpty());
            if(!client_secret.isPresent())
                throw new NeoLoadException("The client_secret environment varaible is missing");

            customfield_Revision=Optional.ofNullable(System.getenv(SECRET_CUSTOMFIELD_REVISION)).filter(o->!o.isEmpty());



        }


    }


}
