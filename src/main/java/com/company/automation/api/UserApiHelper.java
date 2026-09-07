package com.company.automation.api;

import com.company.automation.data.User;
import com.company.automation.data.UserFactory;
import io.restassured.response.Response;

public final class UserApiHelper {
    private UserApiHelper() {
    }

    public static User createActiveUser() {
        User payload = UserFactory.uniqueActiveUser();
        Response response = ApiClient.post("/api/users", payload);
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Failed to create user via API: " + response.asString());
        }
        return response.as(User.class);
    }
}
