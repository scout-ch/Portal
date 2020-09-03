package ch.itds.pbs.portal;

import ch.itds.pbs.portal.domain.BaseEntity;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Component;

import static org.mockito.ArgumentMatchers.*;

@Component
public class MockedWebConversion {

    // must by spy from WithMockedWebConversion!
    @Autowired
    FormattingConversionService mvcConversionService;

    public <T extends BaseEntity> void register(T obj) {
        // workaround for missing DomainClassConverter in reduced test setup
        Mockito.doReturn(true)
                .when(mvcConversionService)
                .canConvert(
                        argThat(argument
                                -> argument.getObjectType().equals(String.class)),
                        argThat((ArgumentMatcher<TypeDescriptor>) argument
                                -> argument.getObjectType().equals(obj.getClass())));

        Mockito.doReturn(obj)
                .when(mvcConversionService)
                .convert(eq(obj.getId().toString()), any(TypeDescriptor.class), any(TypeDescriptor.class));
    }

}
