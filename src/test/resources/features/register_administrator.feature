Feature: Administrator Registration

  Scenario: Successful administrator registration
    Given the academy service is available
    When the client sends a registration request with:
      | firstName | lastName | phoneCountryCode | phoneNumber | dniNumber | academyId | userId |
      | Paul      | Sulca    | +51              | 987654321   | 12345678  | 1         | 10     |
    Then the response should have status code 201
    And the body should contain "Administrator registered successfully"

  Scenario: Failed registration with invalid data
    Given the academy service is available
    When the client sends a registration request with:
      | firstName | lastName | phoneCountryCode | phoneNumber | dniNumber | academyId | userId |
      | (empty)   | Sulca    | +51              | abcde       | 00000000  | 1         | 10     |
    Then the response should have status code 400
    And the response body should contain key "errors"
    And the response body's "errors" should include "Invalid phone number format"
