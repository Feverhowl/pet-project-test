package ru.feverhowl.petprojecttest.core;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.stereotype.Service;
import ru.feverhowl.petprojecttest.utils.exceptions.PetProjectTimoutException;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitrii Zolotarev
 */
@Slf4j
@Service
public class TestCore {
    public void wait(Duration duration) {
        try {
            Awaitility.await().atMost(duration).until(() -> false);
        } catch (ConditionTimeoutException ignored) {}
    }

    public void waitFor(Callable<Boolean> event, Duration duration, String exceptionMessage) throws PetProjectTimoutException {
        try {
            Awaitility.await().atMost(duration)
                    .with()
                    .pollInterval(10, TimeUnit.MILLISECONDS)
                    .ignoreExceptions()
                    .until(event);
        } catch (ConditionTimeoutException e) {
            throw new PetProjectTimoutException(exceptionMessage + " for " + duration.getSeconds() + " seconds");
        }
    }
}
