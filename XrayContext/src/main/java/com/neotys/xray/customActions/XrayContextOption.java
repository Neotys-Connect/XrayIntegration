package com.neotys.xray.customActions;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.ActionParameter;

import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

enum  XrayContextOption implements Option {

    Project("Project", Required, True, TEXT,
            "Project id of your Jira project",
                    "Project id of your Jira project",
          NON_EMPTY),
    Version("Version", Optional, True, TEXT,
            "Version of your project (in Jira)",
                    "Version of your project in jira",
                NON_EMPTY),
    Revision("Revision", Optional, False, TEXT,
            "Revision number ",
                    "Revision Number",
               NON_EMPTY),
    TestPlan("TestPlan", Optional, True, TEXT,
            "Test Plan Id stored in Jira",
                    "Test Plan id stored in Jira ",
                NON_EMPTY),
    Tags("Tags", Optional, True, TEXT,
            "Tags to register your test results in JIRA, you can add as many tags as you want but seperated by ,",
            "Tags to register your test",
            NON_EMPTY),
    FixVersions("FixVersions", Optional, False, TEXT,
            "FixVersion reference in JIRA",
            "FixVersion freference in JIRA",
            NON_EMPTY),
    CustomFields("CustomFields", Optional, False, TEXT,
            "JIRA CustomFields required to report a test run in Jira. Create a Json PaylOad with all the required fields",
            "{custofiled_120:value,customfileds_1561:value,custofiled_5121:value}",
            NON_EMPTY),
    TestEnvironment("Environment", Optional, True, TEXT,
            "Reference of the Environment in Jira ",
                    "Reference of the Environmnet in JIRA",
              NON_EMPTY);

    private final String name;
    private final Option.OptionalRequired optionalRequired;
    private final Option.AppearsByDefault appearsByDefault;
    private final ActionParameter.Type type;
    private final String defaultValue;
    private final String description;
    private final ArgumentValidator argumentValidator;

    XrayContextOption(final String name, final Option.OptionalRequired optionalRequired,
                              final Option.AppearsByDefault appearsByDefault,
                              final ActionParameter.Type type, final String defaultValue, final String description,
                              final ArgumentValidator argumentValidator) {
        this.name = name;
        this.optionalRequired = optionalRequired;
        this.appearsByDefault = appearsByDefault;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
        this.argumentValidator = argumentValidator;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Option.OptionalRequired getOptionalRequired() {
        return optionalRequired;
    }

    @Override
    public Option.AppearsByDefault getAppearsByDefault() {
        return appearsByDefault;
    }

    @Override
    public ActionParameter.Type getType() {
        return type;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ArgumentValidator getArgumentValidator() {
        return argumentValidator;
    }

}