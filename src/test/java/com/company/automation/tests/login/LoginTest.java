package com.company.automation.tests.login;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.automation.api.UserApiHelper;
import com.company.automation.base.BaseTest;
import com.company.automation.constants.AppConstants;
import com.company.automation.data.User;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.LoginPage;
import com.company.automation.utils.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(LoginTest.class);

    @Test(groups = {"smoke"})
    public void validUserCanLogIn() {
        User user = UserApiHelper.createActiveUser();
        HomePage home = new LoginPage(getDriver()).loginAs(user);
        assertThat(home.isLoaded()).isTrue();
        assertThat(home.welcomeText()).contains(user.getFirstName());
        assertThat(home.header().userLabel()).isEqualTo(user.getFirstName());
    }

    @Test(groups = {"regression"})
    public void wrongPasswordShowsError() {
        User user = UserApiHelper.createActiveUser();
        String invalidPassword = JsonReader.read("testdata/users.json").get("invalidPassword").asText();
        LoginPage page = new LoginPage(getDriver())
                .loginExpectingFailure(user.withPassword(invalidPassword));
        assertThat(page.errorMessage()).isEqualTo(AppConstants.LOGIN_ERROR);
    }

    @Test(groups = {"regression"})
    public void invalidUsernameShowsError() {
        String invalidUsername = JsonReader.read("testdata/users.json").get("invalidUsername").asText();
        LoginPage page = new LoginPage(getDriver())
                .loginExpectingFailure(new User("Any", "User", invalidUsername, "any-password"));
        assertThat(page.errorMessage()).isEqualTo(AppConstants.LOGIN_ERROR);
    }

    @Test(groups = {"regression"})
    public void emptyUsernameShowsError() {
        String emptyUsername = JsonReader.read("testdata/users.json").get("emptyUsername").asText();
        LoginPage page = new LoginPage(getDriver())
                .loginExpectingFailure(new User("Any", "User", emptyUsername, "any-password"));
        assertThat(page.errorMessage()).isEqualTo(AppConstants.LOGIN_ERROR);
    }

    @Test(groups = {"smoke"})
    public void invalidCredentialsShowsError() {
        String wrongUsername = JsonReader.read("testdata/users.json").get("wrongUsername").asText();
        String wrongPassword = JsonReader.read("testdata/users.json").get("wrongPassword").asText();
        LoginPage page = new LoginPage(getDriver())
                .loginExpectingFailure(new User("Any", "User", wrongUsername, wrongPassword));
        assertThat(page.errorMessage()).isEqualTo(AppConstants.LOGIN_ERROR);
    }

    @DataProvider(name = "invalidPasswords")
    public Object[][] invalidPasswords() {
        return new Object[][] {
            {"definitely-wrong"},
            {"12345"},
            {""}
        };
    }

    @Test(dataProvider = "invalidPasswords", groups = {"regression"})
    public void loginRejectsInvalidPasswords(String badPassword) {
        LOG.info("Testing bad password: {}", badPassword);

        User user = UserApiHelper.createActiveUser();
        LoginPage page = new LoginPage(getDriver())
                .loginExpectingFailure(user.withPassword(badPassword));

        assertThat(page.errorMessage()).isEqualTo(AppConstants.LOGIN_ERROR);
    }
}
