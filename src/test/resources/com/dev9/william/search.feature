@requires_browser
Feature: Multiple features tested

  Scenario: Multiple tests around WilliamHill's game page
    Given WilliamHill's games page
    When Exploring the elements for the maincss file
    Then It should match with the current page version
    When Displaying all the cookies available
    Then We can access the cookie STACK and its value
    When Login to the lobby using my credentials
    Then We are able to access with no problems
    When Counting the games in the A-Z list
    Then We should see 371 games, and list them in the console


