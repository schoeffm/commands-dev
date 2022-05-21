package de.bender.commandsdev.boundary;

import de.bender.commandsdev.control.OutputFormatter;
import de.bender.commandsdev.control.OutputFormatter.OutputFormat;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import static java.net.http.HttpClient.Redirect.NORMAL;
import static java.net.http.HttpClient.Version.HTTP_1_1;
import static java.nio.charset.StandardCharsets.UTF_8;

@Command(name = "cmd",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = """
                CLI tool to integrate with www.commands.dev in order to quickly lookup commands-snippets.
                Can be integrated with Alfred (a MacOS AppLauncher) or used from the terminal (default-output).
                """)
public class Commands implements Callable<Integer> {

    private static final Integer EXIT_CODE_OK = 0;
    private static final Integer EXIT_CODE_EMPTY_BODY = 123;
    private static final Integer EXIT_CODE_NO_INPUT = 120;

    private static final String API_KEY="236074d2f014dfd574193aaa0d2b7f33";
    private static final String APP_ID="9TPVXC5U2N";

    @Inject
    @Any
    Instance<OutputFormatter> outputFormatter;

    @Parameters(description = "Lookup term")
    private List<String> queryTerms;


    @CommandLine.Option(names = {"-o", "--output"}, defaultValue = "raw",
            required = true,
            description = "Determines the output-format (currently supported: raw, json, alfred)")
    private OutputFormat outputFormat;

    @CommandLine.Option(names = {"--proxy-host"},
            description = "The Proxy-Host to be used (i.e. proxy.muc)")
    private String proxyHost;

    @CommandLine.Option(names = {"--proxy-port"},
            description = "The Proxy-Port to be used (i.e. 8080)")
    private Integer proxyPort;

    @CommandLine.Option(names = {"--proxy-user"},
            description = "The username to be used for proxy-authentication")
    private String proxyUser;

    @CommandLine.Option(names = {"--proxy-pass"},
            description = "The password to be used for proxy-authentication")
    private String proxyPassword;

    @Override
    public Integer call() throws Exception {
        if (Objects.isNull(queryTerms)) { return EXIT_CODE_NO_INPUT; }
        // curl 'https://9tpvxc5u2n-dsn.algolia.net/1/indexes/*/queries?x-algolia-api-key=236074d2f014dfd574193aaa0d2b7f33&x-algolia-application-id=9TPVXC5U2N' --data-raw '{"requests":[{"indexName":"workflow_specs","params":"highlightPreTag=%3Cais-highlight-0000000000%3E&highlightPostTag=%3C%2Fais-highlight-0000000000%3E&query=diff&facets=%5B%5D&tagFilters="}]}'

        String requestBody = String.format("{\"requests\":[{\"indexName\":\"workflow_specs\",\"params\":\"query=%s\"}]}", URLEncoder.encode(String.join(" ", queryTerms), UTF_8));

        var request = HttpRequest
                .newBuilder(URI.create(String.format("https://9tpvxc5u2n-dsn.algolia.net/1/indexes/*/queries?x-algolia-api-key=%s&x-algolia-application-id=%s", API_KEY, APP_ID)))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("User-agent", "Mozilla/6.0")
                .build();

        var response = createHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        var result = parseResult(response.body());
        outputFormatter.stream()
                .filter(f -> f.canHandle(outputFormat))
                .map(f -> f.format(result))
                .forEach(System.out::println);

        return 0;
    }

    private List<de.bender.commandsdev.entity.Command> parseResult(String body) {
        var jsonb = JsonbBuilder.create();
        var jsonObject = jsonb.fromJson(body, JsonObject.class);
        var hits = jsonObject.getJsonArray("results").getJsonObject(0).getJsonArray("hits");
        var result = new ArrayList<de.bender.commandsdev.entity.Command>();
        for (int i = 0; i < hits.size(); i++) {
            JsonObject hit = hits.getJsonObject(i);

            result.add(new de.bender.commandsdev.entity.Command(
                    hit.getString("name"),
                    hit.getString("command"),
                    hit.getString("description"),
                    hit.getString("objectID"),
                    hit.getJsonArray("tags").getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toSet())));
        }
        return result;
    }


    HttpClient createHttpClient() {
        var clientBuilder = HttpClient.newBuilder()
                .followRedirects(NORMAL)
                .version(HTTP_1_1);

        if (Objects.nonNull(this.proxyHost) && Objects.nonNull(this.proxyPort)) {
            clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(this.proxyHost, this.proxyPort)));
            if (Objects.nonNull(this.proxyUser) && Objects.nonNull(this.proxyPassword)) {
                clientBuilder.authenticator(basicAuthAuthenticator(this.proxyUser, this.proxyPassword.toCharArray()));
            }
        } else if (Objects.nonNull(System.getenv("HTTPS_PROXY"))) {
            URI proxyUri = URI.create(System.getenv("HTTPS_PROXY"));
            clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort())));
            if (Objects.nonNull(proxyUri.getUserInfo())) {
                var proxyAuth = proxyUri.getUserInfo().split(":");
                clientBuilder.authenticator(basicAuthAuthenticator(proxyAuth[0], proxyAuth[1].toCharArray()));
            }
        }

        return clientBuilder.build();
    }

    /*
     * provides a basic-auth Authenticator for authentication against a web-proxy (in case it was revealed)
     */
    Authenticator basicAuthAuthenticator(String username, char[] password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }
}
