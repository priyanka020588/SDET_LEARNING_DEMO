package com.company.automation.api;

import com.company.automation.data.User;
import com.company.automation.data.UserFactory;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserApiHelper {
    private static final Logger LOG = LogManager.getLogger(UserApiHelper.class);

    private UserApiHelper() {
    }

    public static User createActiveUser() {
        User payload = UserFactory.uniqueActiveUser();
        LOG.info("Creating user via API: {}", payload.getUsername());
        Response response = ApiClient.post("/api/users", payload);
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Failed to create user via API: " + response.asString());
        }
        return response.as(User.class);
    }
}
