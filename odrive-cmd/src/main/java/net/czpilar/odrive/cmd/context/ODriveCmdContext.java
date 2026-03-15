package net.czpilar.odrive.cmd.context;

import net.czpilar.odrive.cmd.credential.PropertiesODriveCredential;
import net.czpilar.odrive.core.context.ODriveCoreContext;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.springframework.context.annotation.*;

import static net.czpilar.odrive.cmd.runner.impl.ODriveCmdRunner.*;

@Configuration
@ComponentScan(basePackages = "net.czpilar.odrive.cmd")
@Import(ODriveCoreContext.class)
@PropertySource("classpath:odrive-core.properties")
public class ODriveCmdContext {

    public static final String UPLOAD_DIR_PROPERTY_KEY = "odrive.uploadDir";
    public static final String REFRESH_TOKEN_PROPERTY_KEY = "odrive.refreshToken";
    public static final String DEFAULT_UPLOAD_DIR = "odrive-uploads";

    @Bean
    public PropertiesODriveCredential propertiesODriveCredential() {
        return new PropertiesODriveCredential(UPLOAD_DIR_PROPERTY_KEY,
                REFRESH_TOKEN_PROPERTY_KEY, DEFAULT_UPLOAD_DIR);
    }

    @Bean
    public HelpFormatter helpFormatter() {
        return HelpFormatter.builder().setShowSince(false).get();
    }

    @Bean
    public DefaultParser defaultParser() {
        return new DefaultParser();
    }

    @Bean
    public Options options() {
        return new Options()
                .addOption(toOption(OPTION_VERSION, "show oDrive version"))
                .addOption(toOption(OPTION_HELP, "show this help"))
                .addOption(toOption(OPTION_LINK, "display authorization link"))
                .addOption(toOptionalOption(OPTION_AUTHORIZATION, "process authorization; waits for code if not provided", "code"))
                .addOption(toUnlimitedOption(toOption(OPTION_FILE, "upload file(s)", "file")))
                .addOption(toOption(OPTION_DIRECTORY, "directory for upload; creates new one if no directory exists; default is odrive-uploads", "dir"))
                .addOption(toOption(OPTION_PROPERTIES, "path to oDrive properties file", "props"));
    }

    private Option toOption(String opt, String description) {
        return new Option(opt, description);
    }

    private Option toOption(String opt, String description, String argName) {
        Option option = new Option(opt, true, description);
        option.setArgName(argName);
        return option;
    }

    private Option toOptionalOption(String opt, String description, String argName) {
        Option option = new Option(opt, true, description);
        option.setArgName(argName);
        option.setOptionalArg(true);
        return option;
    }

    private Option toUnlimitedOption(Option option) {
        option.setArgs(Option.UNLIMITED_VALUES);
        return option;
    }
}
