package ru.feverhowl.petprojecttest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import ru.feverhowl.petprojecttest.model.Pet;
import ru.feverhowl.tools.data.dependency.TDMDependencyObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitrii Zolotarev
 */
@Slf4j
public class DependTest extends BaseTest {
    private static final String DEPENDENCIES = "dependencies/all.json";

    @Value("${dependencies.load}")
    private boolean LOAD_DEPENDENCIES;

    @Value("${dependencies.clear}")
    private boolean CLEAR_DEPENDENCIES;

    private TDMDependencyObject<Map> dependencyObject;

    {
        try {
            dependencyObject = new TDMDependencyObject<>(DEPENDENCIES, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeGroups(groups = "dependsOnPet")
    protected void loadPets() {
        if (LOAD_DEPENDENCIES) {
            List<Pet> dependencies = dependencyObject.getContext("PET").getObjects(Pet.class);

            log.debug("Removing dependent data that may have remained from previous tests from PET");
            dependencies.forEach(aPet -> petRepository.deleteAllById(aPet.getId()));

            log.info("Loading dependent data into PET");
            dependencies.forEach(petRepository::save);
        }
    }

    @AfterGroups(groups = "dependsOnPet", alwaysRun = true)
    protected void clearPets() {
        if (CLEAR_DEPENDENCIES) {
            log.info("Removing dependent data from PET");
            dependencyObject.getContext("PET").getObjects(Pet.class).forEach(petRepository::delete);
        }
    }
}
