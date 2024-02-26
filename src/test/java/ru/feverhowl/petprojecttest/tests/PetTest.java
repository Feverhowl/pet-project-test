package ru.feverhowl.petprojecttest.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import ru.feverhowl.petprojecttest.DependTest;
import ru.feverhowl.petprojecttest.model.Pet;
import ru.feverhowl.petprojecttest.utils.DataProvider;
import ru.feverhowl.tools.annotation.TDMConfiguration;
import ru.feverhowl.tools.data.context.TDMTestCaseContext;

import java.io.IOException;
import java.util.*;

/**
 * @author Dmitrii Zolotarev
 */
@Slf4j
@SpringBootTest
@Story("Pet Tests")
public class PetTest extends DependTest {
    private static final String TEST_CASES_DIR = "cases/pet/";

    @BeforeTest
    public void beforeTest() {
        this.restTemplate = new RestTemplate();
    }

    @Feature("Testing Pets - GET")
    @TDMConfiguration(datapath = TEST_CASES_DIR + "petTestCases.json", group = "GET", typeOfExpected = Pet.class)
    @Test(dataProviderClass = DataProvider.class, dataProvider = "getTestCaseContext", groups = "dependsOnPet")
    public void petGetTest(TDMTestCaseContext<Pet, Map<String, String>> context) throws IOException {
        configureTestCase(context);
        List<Pet> expectedPets = context.getExpected().getData();
        String URI = BASE_URL + context.getConfiguration().getParameters().get("ENDPOINT");
        String requestBody = context.getConfiguration().getParameters().get("BODY").toString();

        try {
            log.info("URL: " + URI);
            log.info("BODY: " + requestBody);
            log.info("Sending request");
            response = getResponse(URI);
            List<Pet> actualPets = getObjectsFromResponseBody(responseBody, Pet.class);

            log.info("Checking the response received from the server");
            Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assert.assertEquals(actualPets, expectedPets);
        } catch (Exception | Error e) {
            log.error("Test failed => " + e.getMessage());
            throw e;
        } finally {
            log.info("There is no need to remove test data after each test from this group");
        }
    }

    @Feature("Testing Pets - POST")
    @TDMConfiguration(datapath = TEST_CASES_DIR + "petTestCases.json", group = "POST", typeOfExpected = Pet.class)
    @Test(dataProviderClass = DataProvider.class, dataProvider = "getTestCaseContext", groups = "dependsOnPet")
    public void petPostTest(TDMTestCaseContext<Pet, Map<String, String>> context) throws IOException {
        configureTestCase(context);
        Pet expectedPet = context.getExpected().getData().get(0);
        String URI = BASE_URL + context.getConfiguration().getParameters().get("ENDPOINT");
        String requestBody = context.getConfiguration().getParameters().get("BODY").toString();
        Pet actualPet = new Pet();

        try {
            //log.info("Removing test data that may have remained from previous tests");
            //clearAfterPetTest(expectedPets);

            log.info("URL: " + URI);
            log.info("BODY: " + requestBody);
            log.info("Sending request");
            response = getResponse(URI, requestBody);
            actualPet = getObjectFromResponseBody(responseBody, Pet.class);

            log.info("Checking the response received from the server");
            Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
            if (URI.contains("update")) {
                Assert.assertEquals(actualPet, expectedPet);
            } else {
                Assert.assertEquals(actualPet.getName(), expectedPet.getName());
                Assert.assertEquals(actualPet.getSpecies(), expectedPet.getSpecies());
                Assert.assertEquals(actualPet.getBirthDay(), expectedPet.getBirthDay());
                Assert.assertEquals(actualPet.getId().length(), 36);
                Assert.assertNotNull(actualPet.getCreatedAt());
            }
        } catch (Exception | Error e) {
            log.error("Test failed => " + e.getMessage());
            throw e;
        } finally {
            log.info("Removing test data");
            clearAfterPetPostTest(actualPet);
        }
    }

    private void clearAfterPetPostTest(Pet pet) {
        log.debug("Removing from postgres (pet)");
        petRepository.deleteAllById(pet.getId());
    }
}
