package ch.itds.pbs.portal;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;

@TestConfiguration
public class WithMockedWebConversion {

    @SpyBean(name = "mvcConversionService")
    FormattingConversionService webConversionService;

    @Bean
    public MockedWebConversion mockedWebConversion() {
        return new MockedWebConversion();
    }
}
