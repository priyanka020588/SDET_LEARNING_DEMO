package com.company.automation.tests.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.automation.api.ApiClient;
import com.company.automation.api.UserApiHelper;
import com.company.automation.base.BaseTest;
import com.company.automation.data.User;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.LoginPage;
import org.testng.annotations.Test;

public class SmokeTest extends BaseTest {

    @Test(groups = {"smoke"})
    public void catalogApiAndHomePageAreUp() {
        assertThat(ApiClient.get("/api/products").statusCode()).isEqualTo(200);

        User user = UserApiHelper.createActiveUser();
        HomePage home = new LoginPage(getDriver()).loginAs(user);
        assertThat(home.isLoaded()).isTrue();
        assertThat(home.welcomeText()).contains("Welcome");
    }
}
