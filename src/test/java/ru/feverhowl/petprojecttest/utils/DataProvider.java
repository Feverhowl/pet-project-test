package ru.feverhowl.petprojecttest.utils;

import ru.feverhowl.tools.data.context.TDMTestCaseContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author Dmitrii Zolotarev
 */
public class DataProvider {
    @org.testng.annotations.DataProvider
    public Iterator<Object[]> getTestCaseContext(Method method) throws IOException {
        return TDMTestCaseContext.getDataProvider(method).iterator();
    }
}
