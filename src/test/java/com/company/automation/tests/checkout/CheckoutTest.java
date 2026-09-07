package com.company.automation.tests.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.automation.api.UserApiHelper;
import com.company.automation.base.BaseTest;
import com.company.automation.data.User;
import com.company.automation.pages.CheckoutPage;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.LoginPage;
import com.company.automation.utils.ExcelReader;
import java.util.Map;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Test(groups = {"regression"})
    public void userCanCheckoutACatalogItem() {
        User user = UserApiHelper.createActiveUser();
        Map<String, String> product = ExcelReader.readSheet("testdata/products.xlsx").get(0);

        HomePage home = new LoginPage(getDriver()).loginAs(user);
        home.addProductToCart(product.get("name"));
        assertThat(home.cart().itemCount()).isEqualTo(1);

        CheckoutPage checkout = home.goToCheckout();
        assertThat(checkout.isLoaded()).isTrue();
        assertThat(checkout.summaryText()).contains(product.get("name"));

        checkout.fillShipping(user, "12 Automation Way").placeOrder();
        assertThat(checkout.confirmationMessage())
                .contains(user.getFirstName())
                .contains(product.get("price"));
    }
}
