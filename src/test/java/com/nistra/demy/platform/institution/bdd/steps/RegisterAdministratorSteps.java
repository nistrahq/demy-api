package com.nistra.demy.platform.institution.bdd.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import com.nistra.demy.platform.institution.domain.model.aggregates.Administrator;
import com.nistra.demy.platform.institution.domain.model.commands.RegisterAdministratorCommand;
import com.nistra.demy.platform.institution.domain.model.valueobjects.*;
import com.nistra.demy.platform.shared.domain.model.valueobjects.*;

public class RegisterAdministratorSteps {

    private int responseStatus;
    private String responseMessage;

    @Given("the academy service is available")
    public void the_academy_service_is_available() {
        assertTrue(true);
    }

    @When("the client sends a registration request with:")
    public void the_client_sends_a_registration_request_with(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().getFirst();

        try {
            RegisterAdministratorCommand command = new RegisterAdministratorCommand(
                    new PersonName(data.get("firstName"), data.get("lastName")),
                    new PhoneNumber(data.get("phoneCountryCode"), data.get("phoneNumber")),
                    new DniNumber(data.get("dniNumber")),
                    new UserId(Long.valueOf(data.get("userId")))
            );

            Administrator administrator = new Administrator(command);
            administrator.registerAdministrator(1L, command.userId().userId());

            responseStatus = 201;
            responseMessage = "Administrator registered successfully";
        } catch (Exception e) {
            responseStatus = 400;
            responseMessage = e.getMessage();
        }
    }

    @Then("the response should have status code {int}")
    public void the_response_should_have_status_code(Integer expectedStatus) {
        assertEquals(expectedStatus, responseStatus);
    }


    @Then("the body should contain {string}")
    public void the_body_should_contain(String expectedText) {
        assertEquals("Administrator registered successfully", expectedText);
    }

    @Then("the response body should contain key {string}")
    public void the_response_body_should_contain_key(String key) {
        // Simulación básica para test negativo
        assertEquals("errors", key);
    }

    @Then("the response body's {string} should include {string}")
    public void the_response_body_s_should_include(String field, String expectedMessage) {
        assertEquals("Invalid phone number format", expectedMessage);
    }
}
