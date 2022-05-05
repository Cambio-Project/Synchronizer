package de.unistuttgart.cambio.synchronizer.test;

import de.datev.httploadgenerator.profile.definitions.*;
import de.datev.httploadgenerator.runner.Director;
import de.datev.httploadgenerator.runner.ProfileCreator;
import lombok.val;
import org.json.JSONObject;
import org.junit.jupiter.api.Assumptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Lion Wagner
 */
public class TestUtils {

    public static String grabLocalIP() throws Exception {
        //TODO: if on linux skip call of wsl
        val builder = new ProcessBuilder("wsl hostname -I | xargs".split(" "));
        val process = builder.start();
        val ret = process.waitFor();
        val reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        val result = reader.readLine();
        Assumptions.assumeFalse(ret != 0 || result == null, "Skipped example execution: Did not find wsl.exe or something went wrong retrieving the wsl IP");
        return result;
    }

    public static RunSettingsDto createExampleRunSettingsDto() throws Exception {
        var lines = Files.readString(Path.of("example/example_run_settings.json"));
        JSONObject root = new JSONObject(lines);
        JSONObject wrapped_settings = root.getJSONObject("wrapped_settings");


        RunSettingsDto dto = new RunSettingsDto();
        val userProfile = ProfileCreator.parseVirtualUserProfile("example/example_user_profile.yml", Director.getYamlMapper());
        val arrivalRates = ProfileCreator.parseArrivalRateFile(new File("example/ExampleSynchronizerArrivalRates.csv"));

        String wslIP = grabLocalIP();
        for (HttpServiceInstanceEndpoints service : userProfile.getServices()) {
            service.setHosts(new String[]{wslIP + ":50100"});
        }

        dto.setArrivalRates(arrivalRates);
        dto.setRequestProfile(userProfile);
        dto.setNumUsers(wrapped_settings.getInt("num_users"));
        dto.setRandomizeUsers(wrapped_settings.getBoolean("randomize_users"));
        dto.setNumSendingThreads(wrapped_settings.getInt("num_sending_threads"));
        dto.setRandomBatchTimes(wrapped_settings.getBoolean("random_batch_times"));
        dto.setRandomSeed(wrapped_settings.getInt("random_seed"));
        dto.setWarmupDurationS(wrapped_settings.getInt("warmup_duration_s"));
        dto.setWarmupRate(wrapped_settings.getDouble("warmup_rate"));
        dto.setWarmupPauseS(wrapped_settings.getInt("warmup_pause_s"));
        dto.setTimeoutMs(wrapped_settings.getInt("timeout_ms"));
        dto.setLogResponses(wrapped_settings.getBoolean("log_responses"));
        dto.setLogResponseValidityErrors(wrapped_settings.getBoolean("log_response_validity_errors"));

        return dto;
    }

    /**
     * Creates an Example <code>RunSettingsDto</code> from the loadgenerator package
     *
     * @author Joakim von Kistowski
     */
    public static RunSettingsDto createRunSettingsDto() {
        HashMap<String, String> commonHeaders = new HashMap<>();
        commonHeaders.put("header0", "value0");
        HashMap<String, DynamicExpressionDefinition> requestHeaders = new HashMap<>();
        requestHeaders.put("header1", DynamicExpressionDefinition.ofConstText("value1"));
        HttpServiceInstanceEndpoints endpoints = new HttpServiceInstanceEndpoints();
        endpoints.setServiceName("test");
        endpoints.setProtocol("http");
        endpoints.setResponseContentType(HttpResponseContentType.JSON);
        endpoints.setHosts(new String[]{"localhost"});
        endpoints.setCommonHeaders(commonHeaders);

        DynamicExpressionDefinition rootExpression = new DynamicExpressionDefinition();
        rootExpression.setType(DynamicExpressionDefinition.ExpressionType.CONST);
        rootExpression.setText("/");

        DynamicExpressionDefinition emptyJsonExpression = new DynamicExpressionDefinition();
        emptyJsonExpression.setType(DynamicExpressionDefinition.ExpressionType.CONST);
        emptyJsonExpression.setText("{\"name\":\"test\"}");

        HttpLoadRequestDefinition loadRequestDefinition0 = new HttpLoadRequestDefinition();
        loadRequestDefinition0.setServiceName("test");
        loadRequestDefinition0.setMethod("GET");
        loadRequestDefinition0.setUri(rootExpression);
        loadRequestDefinition0.setExpectedResponseCodes(new int[]{200});
        loadRequestDefinition0.setHeaders(requestHeaders);

        HttpLoadRequestDefinition loadRequestDefinition1 = new HttpLoadRequestDefinition();
        loadRequestDefinition1.setServiceName("test");
        loadRequestDefinition1.setMethod("POST");
        loadRequestDefinition1.setUri(rootExpression);
        loadRequestDefinition1.setBody(emptyJsonExpression);
        loadRequestDefinition1.setExpectedResponseCodes(new int[]{201});

        VirtualUserProfileDefinition profileDefinition = new VirtualUserProfileDefinition();
        profileDefinition.setServices(Collections.singletonList(endpoints));
        profileDefinition.setUserRequests(
                new HttpLoadRequestDefinition[]{loadRequestDefinition0, loadRequestDefinition1});

        RunSettingsDto settingsDto = new RunSettingsDto();
        settingsDto.setRandomBatchTimes(true);
        settingsDto.setNumUsers(16);
        settingsDto.setNumSendingThreads(16);
        settingsDto.setTimeoutMs(5000);
        settingsDto.setWarmupDurationS(0);
        settingsDto.setWarmupRate(0);
        settingsDto.setWarmupPauseS(0);
        settingsDto.setRandomizeUsers(false);
        settingsDto.setRequestProfile(profileDefinition);
        settingsDto.setLogResponseValidityErrors(false);
        settingsDto.setLogResponses(false);

        return settingsDto;
    }
}
