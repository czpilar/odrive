package net.czpilar.odrive.cmd.runner.impl;

import net.czpilar.odrive.cmd.credential.PropertiesODriveCredential;
import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.service.IAuthorizationService;
import net.czpilar.odrive.core.service.IFileService;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ODriveCmdRunnerTest {

    private final ODriveCmdRunner runner = new ODriveCmdRunner();

    @Mock
    private CommandLineParser commandLineParser;
    @Mock
    private Options options;
    @Mock
    private HelpFormatter helpFormatter;
    @Mock
    private IAuthorizationService authorizationService;
    @Mock
    private IFileService fileService;
    @Mock
    private ODriveSetting oDriveSetting;
    @Mock
    private PropertiesODriveCredential propertiesODriveCredential;
    @Mock
    private CommandLine commandLine;
    @Mock
    private AuthorizationCodeWaiter codeWaiter;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        runner.setCommandLineParser(commandLineParser);
        runner.setOptions(options);
        runner.setHelpFormatter(helpFormatter);
        runner.setAuthorizationService(authorizationService);
        runner.setFileService(fileService);
        runner.setODriveSetting(oDriveSetting);
        runner.setPropertiesODriveCredential(propertiesODriveCredential);
        runner.setCodeWaiter(codeWaiter);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    private static DriveItem driveItem(String name, String eTag) {
        return new DriveItem(null, name, null, eTag, null, null, null, null);
    }

    @Test
    void testRunWhereCommandLineParsingFails() throws ParseException, IOException {
        String appName = "application-name";
        String[] args = {"arg1", "arg2"};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenThrow(ParseException.class);
        when(oDriveSetting.getApplicationName()).thenReturn(appName);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(helpFormatter).printHelp(appName, null, options, null, true);
        verify(oDriveSetting).getApplicationName();

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(fileService);
        verifyNoInteractions(propertiesODriveCredential);
        verifyNoInteractions(commandLine);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasEmptyOptions() throws ParseException, IOException {
        String appName = "application-name";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(oDriveSetting.getApplicationName()).thenReturn(appName);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(helpFormatter).printHelp(appName, null, options, null, true);
        verify(commandLine).getOptions();
        verify(oDriveSetting).getApplicationName();

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(fileService);
        verifyNoInteractions(propertiesODriveCredential);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasOnlyPropertiesOption() throws ParseException, IOException {
        String appName = "application-name";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(oDriveSetting.getApplicationName()).thenReturn(appName);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(helpFormatter).printHelp(appName, null, options, null, true);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(oDriveSetting).getApplicationName();

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(fileService);
        verifyNoInteractions(propertiesODriveCredential);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndVersionOptions() throws ParseException {
        String appVersion = "application-version";
        String propertiesValue = "test-properties-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("v").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(oDriveSetting.getApplicationVersion()).thenReturn(appVersion);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(oDriveSetting).getApplicationVersion();
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(fileService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndHelpOptions() throws ParseException, IOException {
        String appName = "application-name";
        String propertiesValue = "test-properties-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("h").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(oDriveSetting.getApplicationName()).thenReturn(appName);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(helpFormatter).printHelp(appName, null, options, null, true);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(oDriveSetting).getApplicationName();
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(fileService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndLinkOptions() throws ParseException {
        String propertiesValue = "test-properties-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("l").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(authorizationService.getAuthorizationURL()).thenReturn("test-authorization-url");

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(authorizationService).getAuthorizationURL();

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(authorizationService);

        verifyNoInteractions(options);
        verifyNoInteractions(fileService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndAuthorizationOptionsAndReturnNullCredential() throws ParseException {
        String propertiesValue = "test-properties-value";
        String authorizationValue = "test-authorization-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("a").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(authorizationValue);
        when(authorizationService.authorize(authorizationValue)).thenReturn(null);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(authorizationService).authorize(authorizationValue);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(authorizationService);

        verifyNoInteractions(options);
        verifyNoInteractions(fileService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndAuthorizationOptionsAndReturnCredential() throws ParseException {
        String propertiesValue = "test-properties-value";
        String authorizationValue = "test-authorization-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("a").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(authorizationValue);
        when(authorizationService.authorize(authorizationValue)).thenReturn(new Credential("token", "refresh"));

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(authorizationService).authorize(authorizationValue);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(authorizationService);

        verifyNoInteractions(options);
        verifyNoInteractions(fileService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndAuthorizationOptionsNoValueAndReturnCredential() throws ParseException {
        String propertiesValue = "test-properties-value";
        String authorizationValue = "test-authorization-value";
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("a").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(null);
        when(authorizationService.authorize(authorizationValue)).thenReturn(new Credential("token", "refresh"));
        when(codeWaiter.getCode()).thenReturn(Optional.of(authorizationValue));

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(authorizationService).authorize(authorizationValue);
        verify(codeWaiter).getCode();

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(codeWaiter);
        verifyNoMoreInteractions(authorizationService);

        verifyNoInteractions(options);
        verifyNoInteractions(fileService);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndFileOptionsAndNoDirectory() throws ParseException {
        String propertiesValue = "test-properties-value";
        String optionFile = "test-file-value";
        List<String> optionFiles = List.of(optionFile);
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("f").build()};
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_DIRECTORY)).thenReturn(false);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(commandLine.getOptionValues(ODriveCmdRunner.OPTION_FILE)).thenReturn(new String[]{optionFile});

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_DIRECTORY);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).getOptionValues(ODriveCmdRunner.OPTION_FILE);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(fileService).uploadFiles(optionFiles, (String) null);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(fileService);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(codeWaiter);
    }

    @Test
    void testRunWhereCommandLineHasPropertiesAndFileOptionsAndDirectory() throws ParseException {
        String propertiesValue = "test-properties-value";
        String optionFile = "test-file-value";
        String optionDirectory = "test-directory";
        List<String> optionFiles = List.of(optionFile);
        String[] args = {"arg1", "arg2"};
        Option[] optionList = {Option.builder("p").build(), Option.builder("f").build()};
        DriveItem file1 = driveItem("file1.txt", "etag1");
        DriveItem file2 = driveItem("file2.txt", "etag2");
        List<DriveItem> files = Arrays.asList(file1, file2);
        when(commandLineParser.parse(any(Options.class), any(String[].class))).thenReturn(commandLine);
        when(commandLine.getOptions()).thenReturn(optionList);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_VERSION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_HELP)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_LINK)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION)).thenReturn(false);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_FILE)).thenReturn(true);
        when(commandLine.hasOption(ODriveCmdRunner.OPTION_DIRECTORY)).thenReturn(true);
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES)).thenReturn(propertiesValue);
        when(commandLine.getOptionValues(ODriveCmdRunner.OPTION_FILE)).thenReturn(new String[]{optionFile});
        when(commandLine.getOptionValue(ODriveCmdRunner.OPTION_DIRECTORY)).thenReturn(optionDirectory);
        when(fileService.uploadFiles(anyList(), anyString())).thenReturn(files);

        runner.run(args);

        verify(commandLineParser).parse(options, args);
        verify(commandLine).getOptions();
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_VERSION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_HELP);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_LINK);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_AUTHORIZATION);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).hasOption(ODriveCmdRunner.OPTION_DIRECTORY);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_PROPERTIES);
        verify(commandLine).getOptionValues(ODriveCmdRunner.OPTION_FILE);
        verify(commandLine).getOptionValue(ODriveCmdRunner.OPTION_DIRECTORY);
        verify(propertiesODriveCredential).setPropertyFile(propertiesValue);
        verify(fileService).uploadFiles(optionFiles, optionDirectory);

        verifyNoMoreInteractions(commandLineParser);
        verifyNoMoreInteractions(helpFormatter);
        verifyNoMoreInteractions(oDriveSetting);
        verifyNoMoreInteractions(commandLine);
        verifyNoMoreInteractions(propertiesODriveCredential);
        verifyNoMoreInteractions(fileService);

        verifyNoInteractions(options);
        verifyNoInteractions(authorizationService);
        verifyNoInteractions(codeWaiter);
    }
}
