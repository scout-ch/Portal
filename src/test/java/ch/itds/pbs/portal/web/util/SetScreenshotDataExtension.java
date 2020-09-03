package ch.itds.pbs.portal.web.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

/**
 * Writes current test class & method to seleniumHelper
 */
public class SetScreenshotDataExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String currentClass = context.getRequiredTestClass().getSimpleName();
        String currentMethod = context.getRequiredTestMethod().getName();

        Object testInstance = context.getRequiredTestInstance();
        Field seleniumHelperField = FieldUtils.getField(testInstance.getClass(), "seleniumHelper", true);
        SeleniumHelper seleniumHelper = (SeleniumHelper) seleniumHelperField.get(testInstance);
        seleniumHelper.currentTestClass = currentClass;
        seleniumHelper.currentTestMethod = currentMethod;

    }
}
