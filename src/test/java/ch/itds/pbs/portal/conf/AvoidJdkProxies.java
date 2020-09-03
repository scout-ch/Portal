package ch.itds.pbs.portal.conf;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AvoidJdkProxies {

    // SpringSecurity / MethodSecurity (@Secured/@PreAuthorize) requires proxies on controllers
    // AbstractAutowireCapableBeanFactory wraps a proxy around the bean using BeanPostProcessor's
    // By default or for some unknown reason com.sun.proxy... is used
    // If a controller is wrapped with com.sun.proxy it's request mapping is not recognized on startup
    // Therefore @WebMvcTest does not work because the mockMvc requests do not find the request mapping -> 404

}
