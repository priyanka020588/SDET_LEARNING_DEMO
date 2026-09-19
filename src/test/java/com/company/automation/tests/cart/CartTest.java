package com.company.automation.tests.cart;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.automation.api.UserApiHelper;
import com.company.automation.base.BaseTest;
import com.company.automation.data.User;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.LoginPage;
import com.company.automation.utils.ExcelReader;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Test(groups = {"regression"})
    public void addingTwoProductsUpdatesCartCount() {
        User user = UserApiHelper.createActiveUser();
        List<Map<String, String>> products = ExcelReader.readSheet("testdata/products.xlsx");

        HomePage home = new LoginPage(getDriver()).loginAs(user);
        home.addProductToCart(products.get(0).get("name"));
        home.addProductToCart(products.get(1).get("name"));

        assertThat(home.cart().itemCount()).isEqualTo(2);
        assertThat(home.header().userLabel()).isEqualTo(user.getFirstName());
    }
}
