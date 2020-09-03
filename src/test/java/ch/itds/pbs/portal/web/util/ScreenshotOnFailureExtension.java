package ch.itds.pbs.portal.web.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

/**
 * Create screenshot after exception
 */
public class ScreenshotOnFailureExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (extensionContext.getExecutionException().isPresent()) {
            Object testInstance = extensionContext.getRequiredTestInstance();

            Field seleniumHelperField = FieldUtils.getField(testInstance.getClass(), "seleniumHelper", true);
            SeleniumHelper seleniumHelper = (SeleniumHelper) seleniumHelperField.get(testInstance);
            seleniumHelper.screenshot("exception-end");
        }
    }
}

