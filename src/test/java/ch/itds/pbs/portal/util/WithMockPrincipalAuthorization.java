package ch.itds.pbs.portal.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPrincipalAuthorizationSecurityContextFactory.class)
public @interface WithMockPrincipalAuthorization {

    String[] roles() default {"USER"};

    long userId();

}
