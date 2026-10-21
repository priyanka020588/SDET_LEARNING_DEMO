package com.company.automation.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.automation.api.ApiClient;
import com.company.automation.base.BaseTest;
import com.company.automation.data.User;
import com.company.automation.data.UserFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class ApiTest extends BaseTest {

    @Test(groups = {"smoke"})
    public void apiCanCreateAndRetrieveUser() {
        User expected = UserFactory.uniqueActiveUser();

        Response createResponse = ApiClient.post("/api/users", expected);

        assertThat(createResponse.statusCode()).isEqualTo(201);

        User created = createResponse.as(User.class);
        assertThat(created.getUsername()).isEqualTo(expected.getUsername());
        assertThat(created.getFirstName()).isEqualTo(expected.getFirstName());

        assertThat(ApiClient.get("/api/products").statusCode()).isEqualTo(200);
    }
}
