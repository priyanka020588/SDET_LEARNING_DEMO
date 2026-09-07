package com.company.automation.data;

import java.util.UUID;

public final class UserFactory {
    private UserFactory() {
    }

    public static User uniqueActiveUser() {
        String token = UUID.randomUUID().toString().substring(0, 8);
        return new TestDataBuilder()
                .firstName("Ada")
                .lastName("Lovelace")
                .username("ada." + token)
                .password("Passw0rd!" + token)
                .buildUser();
    }
}
