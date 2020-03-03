package com.neotys.xray.conf;

public class Constants {

    public static final String HEALTH_PATH="/health";
    public static final String WEBHOOKPATH="/webhook";
    public static final String DEFAULT_NL_SAAS_API_URL="";
    public static final String DEFAULT_NL_WEB_API_URL="";
    public static final String API_URL_VERSION="/v1";
    public static final String TESTID_KEY="testid";
    public static final String MAX_VU_KEY="maxvu";
    public static final String OVERVIEW_PICTURE_KEY="url_graph_overview";
    public static final String SECRET_API_TOKEN="NL_API_TOKEN";
    public static final String SECRET_NL_WEB_HOST="NL_WEB_HOST";
    public static final String SECRET_SSL="ssl";
    public static final String SECRET_NL_API_HOST="NL_API_HOST";
    public static final String SECRET_PORT="PORT";
    public static String LOGING_LEVEL_KEY="logging-level";
    public static int HTTP_PORT=8080;
    public static final String SECRET_MANAGED_HOST="ManagedAPIHostname";
    public static final String SECRET_MANAGED_PORT="ManagedPort";
    public static final String SECRET_CLOUD_PORT="CloudPort";
    public static final String SECRET_CLOUD_HOST="CloudAPIHostname";
    public static final String SECRET_CLOUD_WEBHOST="CloudWebHostname";
    public static final String SECRET_MANAGED_WEBHOST="ManagedWebHostname";
    public static final String SECRET_USERNAME="user";
    public static final String SECRET_PASSWORD="password";
    public static final String SECRET_CLIENTID="client_id";
    public static final String SECRET_CLIENT_SECRET="client_secret";
    public static final String DEFAULT_CLOUD_PORT="443";
    public static final String SECRET_CUSTOMFIELD_REVISION="CustomFieldRevision";
    public static final String SECRET_CUSTOMFIELD_TESTPLAN="CustomFieldTestPlan";
    public static final String SECRET_CUSTOMFIELD_ENVIRONMENT="CustomFieldEnvironement";
    public static final String SLA_TYPE_PERINTERVAL="PerTimeInterval";
    public static final String SLA_TYPE_PERTEST="PerRun";

    public static final String CLOUD_FIELDNAME_ENVIRONEMNT="environments";
    public static final String CLOUD_FIELDNAME_TESTPLAN="testPlanKey";

    public static final String DEFAULT_MANAGED_PORT="80";
    public static final String NEOLOAD="neoload";


    public static final String HTTPS="https://";
    public static final String NEOLAOD_WEB_URL="/#!result/";
    public static final String NEOLAOD_WEB_LASTPART_URL="/overview";

    //-----url path to interact with XRAY----$
    public static final String XRAY_URL_CLOUD_AUTH="/api/v1/authenticate";
    public static final String XRAY_URL_ROBOT_CLOUD_MULTIPART="/api/v1/import/execution/robot/multipart";
    public static final String XRAY_URL_ONPREM_MULTIPART="/rest/raven/1.0/import/execution/robot/multipart";

    ///----path for get the results
    public static final String XRAY_URL_BROWSE="/browse/";

    //-----SLA Status
    public static final String NEOLOAD_PASS_STATUS="PASSED";
    public static final String NEOLOAD_FAIL_STATUS="FAILED";
    public static final String JIRA_PASS_STATUS="PASS";
    public static final String JIRA_FAIL_STATUS="FAIL";
}
