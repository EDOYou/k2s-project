package com.edoyou.k2sbeauty.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Configuration class for internationalization (i18n) setup. This class implements
 * {@link WebMvcConfigurer} and provides necessary beans to handle different locales
 * and messages resources.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    /**
     * Creates a {@link LocaleResolver} bean that determines the current locale based on the user's session.
     * Default locale is set to US.
     *
     * @return {@link LocaleResolver} bean.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    /**
     * Creates a {@link LocaleChangeInterceptor} bean that intercepts the 'lang' parameter in the request and changes
     * the current locale.
     *
     * @return {@link LocaleChangeInterceptor} bean.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Creates an array of {@link HandlerInterceptor}, currently only contains the
     * {@link LocaleChangeInterceptor} bean.
     *
     * @return Array of {@link HandlerInterceptor}
     */
    @Bean
    public HandlerInterceptor[] interceptors(LocaleChangeInterceptor localeChangeInterceptor) {
        return new HandlerInterceptor[]{localeChangeInterceptor};
    }

    /**
     * Register the {@link LocaleChangeInterceptor} into the {@link InterceptorRegistry}.
     * This allows the interceptor to be invoked during the lifecycle of the request.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Creates a {@link MessageSource} bean that manages the resource bundles for different locales.
     * The resource bundles should be placed under the 'i18n' directory and should be named 'messages'.
     *
     * @return {@link MessageSource} bean.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}