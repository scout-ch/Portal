package ch.itds.pbs.portal;


import ch.itds.pbs.portal.conf.AvoidJdkProxies;
import ch.itds.pbs.portal.conf.ThymeleafConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(value = {ElementType.TYPE})
@Retention(value = RUNTIME)
@Inherited
@Import({ThymeleafConfig.class, AvoidJdkProxies.class})
public @interface TestAnnotationHolder {
}
