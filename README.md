# NeoLoad Xray Integration

This project is aimed to integrate NeoLoad into Xray.
This project has 2 disctinct components :
* `XrayContext` : Custom action to add in NeoLoad to send the Project Context to NeoLoad Web.
* `WebhookHandler` : Service that will receive the NeoLoad Web Test end notification ( through WebHook)
## XrayContext
This custom action will allow you to add all the project information required in XRAY :
   * `Project` (Required) : project id in Jira/Xray
   * `Version` (Optional): Version of the application in Jira/Xray
   * `Revision` (Optional) : Revision numnber of the project in Jira/Xray
   * `TestPlan` (Optional) : TestPlan id
   * `Tags` (Optional) : Tag to add in the test results in Xray/Jira
   * `FixVersions` (Optional) : FixedVersion of the project in Xray/Jira
   * `Environment` (Optional) : TestEnvironment of the test results
   * `CustomFields` (Optional) : CustomFields required to import tests results in Xray/Jira

Depending on the settings of your Xray Projet, you would have to precize more or less fields .
The customFields is a Json object containing a Map key,value of all the custom properties required to import a test results in Jira.

XrayContext will update the test results in NeoLoad web with all the information required to be able to import a NeoLoad web test in Xray/Jira

     
| Property | Value |
| -----| -------------- |
| Maturity | Experimental |
| Author   | Neotys Partner Team |
| License  | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad  | 7.0 (Enterprise or Professional Edition w/ Integration & Advanced Usage and NeoLoad Web option required)|
| Requirements | NeoLoad Web |
| Bundled in NeoLoad | No
| Download Binaries | <ul><li>[latest release]() is only compatible with NeoLoad from version 7.0</li><li> Use this [release](https://github.com/Neotys-Labs/Dynatrace/releases/tag/Neotys-Labs%2FDynatrace.git-2.0.10) for previous NeoLoad versions</li></ul>|

### Installation

1. Download the [latest release]() for NeoLoad from version 7.0
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm).

<p align="center"><img src="/screenshots/custom_action.png" alt="XrayContext Advanced Action" /></p>

### NeoLoad Set-up

Once installed, how to use in a given NeoLoad project:

1. Create a `XrayXontext` User Path.
1. Insert `XrayContext` in the `Init` block.
<p align="center"><img src="/screenshots/vu.png" alt="XrayContext User Path" /></p>


1. Create a NeoLoad Population Xraycontext having only the userPath XrayContext
<p align="center"><img src="/screenshots/population.png" alt="XrayContext Population" /></p>
1. Create a NeoLoad Scenario Using your population and the Xray Population
The XrayContext Population would need to be added to your NeoLoad scenario with the following settings :
* Duration : iteration
* Load Policy : Constant : 1 user doing 1 iteration
<p align="center"><img src="/screenshots/scenario.png" alt="XrayContext scenario" /></p>

### Parameters for XrayContext
   
| Name             | Description |
| -----            | ----- |
| `Project`      | Identifier of your Project id in Jira |
| `Version`  (Optional) |  Version of the project |
| `Revision` (optional)  |  Revision number of the project |
| `TestPlan` (Optional) |  Test Plan id to link the test results to |
| `Tags` (Optional) | Tag to reference the test result in Jira . Format : tag1,tag2..Etc |
| `FixVersions` (Optional) | Fixed Version of the project in Jira|
| `Environment` (Optional) | Environement of the application  |
| `CustomFields` (Optional) | JsonObject with all the required custom fields required to import test results in Jira |



## WebHook Handler

###Configuration
The webhook handler is a web service package in a container : `hrexed/neoload_xrayresultsync`
The container will required different type of Environement variables depending if you are using :
1. Jira Cloud
1. Jira On premise

#### Jira Cloud
To be able to import NeoLoad test results you will need to specify :
* `NL_WEB_HOST`: Hostname of the webui of NeoLoad WEB
* `NL_API_HOST` : Hostname of the rest-api of NeoLoad WEB
* `NL_API_TOKEN` : API token of NeoLoad WEB ( [how to generate an API token](https://www.neotys.com/documents/doc/nlweb/latest/en/html/#24270.htm))
* `PORT`  : Port that the service will listen to
* `logging-level` : Logging level of the service ( DEBUG, INFO, ERROR)
* `CloudWebHostname` : Hostname of the Webui of your Jira Cloud Environment
* `CloudPort` : Port of the Jira Cloud environment
* `CloudAPIHostname` : Hostname of the API of Xray on Jira CLoud
* `client_id` 
* `client_secret`
* `ssl` : True or false
* `CustomFieldRevision` ( Optional ) : if you Jira Environement requires to precise the Revision number on your test results. You will need to specify the id of the custom field corresponding to Revision

#### Jira On Premise
To be able to import NeoLoad test results you will need to specify :
* `NL_WEB_HOST`: Hostname of the webui of NeoLoad WEB
* `NL_API_HOST` : Hostname of the rest-api of NeoLoad WEB
* `NL_API_TOKEN` : API token of NeoLoad WEB
* `PORT`  : Port that the service will listen to
* `logging-level` : Logging level of the service ( DEBUG, INFO, ERROR)
* `ManagedWebHostname` : Hostname of the Webui of your Jira Cloud Environment
* `ManagedPort` : Port of the Jira Cloud environment
* `ManagedAPIHostname` : Hostname of the API of Xray on Jira CLoud
* `user` 
* `password`
* `ssl` : True or false
* `CustomFieldRevision` ( Optional ) : if you Jira Environement requires to precise the Revision number on your test results. You will need to specify the id of the custom field corresponding to Revision
* `CustomFieldEnvironement` (Optional) : if you Jira Environment requires to precise the Test environment to publish a test results. You will need to specify the id of the custom field Environment
* `CustomFieldTestPlan` (Optional) : if you Jira Environment requires to precise the Test Plan to publish a test results. You will need to specify the id of the custom field Test Plan

#### Run the webhookHandler

Requirements : Server having :
* docker installed
* acessible from NeoLoad WEB ( Saas our your managemend instance of NeoLoad WEB)

THe deployment will use either :
* `/deployment/docker-compose-Cloud` to connect to Jira Cloud
* `/deployment/docker-compose-Onpremise` to connect to your Jira on premise instance

Make sure to update the docker-compose file by specifying the Environment variables.

the deployment will be done by running the following command :
```bash
docker-compose -f <docker file> up -d
```
#### Configure the WebHook in your NeoLoad Web Account to send a notification to your WebHook service

The webhookhandler service is listenning to 2 disctinct endpoints :
* `/Health` : Get request build to check if the webhookhandler is up
* `/webhook` : POST request to receive the webhook from NeoLoad WEB

The Webhookhandler is expecting the following Json Payload :
```json
{
	"testid" : "TESTID",
	"url_graph_overview":"URL TO the GRAPH global overview of the test",
	"maxvu" :"MAX number of VU"
}
```

To configure the webhook in NeoLoad WEB you will need to :
1. Connect to NeoLoad WEB
2. Click on 
3. Click On the TAB named WebHook
4. Create a new Webhook ( [How to create a webhook](https://www.neotys.com/documents/doc/nlweb/latest/en/html/#27141.htm))
5. URL of the webhook : http://<IP of you WEBHOOKHANDLER>:8080/webhook
6. Events : Test ended
7. Payload :
```json
{
            "testid": "$(test_id)",
            "url_graph_overview": "$(url_graph_overview)",
            "maxvu" : "$(test_max_nb_vus)"
}
```
<p align="center"><img src="/screenshots/webhook.png" alt="XrayContext webhok" /></p>
