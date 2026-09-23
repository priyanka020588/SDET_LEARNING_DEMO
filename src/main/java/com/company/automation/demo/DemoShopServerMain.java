package com.company.automation.demo;

import com.company.automation.config.ConfigReader;
import java.io.IOException;

/**
 * Keeps the embedded demo shop running for manual browser exploration.
 *
 * <p>Examples:
 * <pre>
 *   ./mvnw -q compile exec:java -Dexec.mainClass=com.company.automation.demo.DemoShopServerMain
 *   ./mvnw -q compile exec:java -Dexec.mainClass=com.company.automation.demo.DemoShopServerMain -Dexec.args=9080
 * </pre>
 */
public final class DemoShopServerMain {

    private DemoShopServerMain() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : portFromConfig();
        String origin = "http://127.0.0.1:" + port;
        System.setProperty("baseUrl", origin + "/login");
        System.setProperty("apiBaseUrl", origin);

        Runtime.getRuntime().addShutdownHook(new Thread(DemoShopServer::stop));

        DemoShopServer.start();

        System.out.println("Demo shop running continuously at " + origin);
        System.out.println("  Login:  " + origin + "/login");
        System.out.println("  Home:   " + origin + "/home  (requires login)");
        System.out.println("  API:    " + origin + "/api/products");
        System.out.println("Press Ctrl+C to stop.");

        Thread.currentThread().join();
    }

    private static int portFromConfig() {
        String baseUrl = ConfigReader.get("baseUrl");
        int colon = baseUrl.lastIndexOf(':');
        int slash = baseUrl.indexOf('/', colon);
        String portText = slash > colon ? baseUrl.substring(colon + 1, slash) : baseUrl.substring(colon + 1);
        return Integer.parseInt(portText);
    }
}
