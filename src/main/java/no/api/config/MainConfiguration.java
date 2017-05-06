package no.api.config;

import no.api.service.BasketOrderService;
import no.api.service.BasketProductService;
import no.api.service.BasketService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;

//import org.springframework.hateoas.config.EnableHypermediaSupport;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "no.api")
public class MainConfiguration {

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public StringHttpMessageConverter responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converter.setWriteAcceptCharset(false);
        return converter;
    }

//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix="datasource.primary")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }


//    @Bean
//    public FilterRegistrationBean corsFilterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new CORSFilter());
//        registrationBean.setName("CORS");
//        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
//        registrationBean.setMatchAfter(true);
//        registrationBean.setInitParameters(new HashMap<String, String>() {{
//            put("Access-Control-Allow-Origin", "*");
//            put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
//        }});
//        registrationBean.setUrlPatterns(Collections.singletonList("*"));
//
//        return registrationBean;
//    }

//    @Bean
//    public FreeMarkerConfigurationFactory freeMarkerConfigurationFactory(){
//        FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
//        factory.setTemplateLoaderPath("classpath:templates");
//        factory.setPreferFileSystemAccess(false);
//        factory.setDefaultEncoding("utf-8");
//        return factory;
//    }

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate(clientHttpRequestFactory());
//    }
//
//    private ClientHttpRequestFactory clientHttpRequestFactory() {
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setReadTimeout(Ints.checkedCast(TimeUnit.SECONDS.toMillis(90)));
//        factory.setConnectTimeout(Ints.checkedCast(TimeUnit.SECONDS.toMillis(90)));
//        return factory;
//    }
//
//
//    @Bean
//    public GroupController groupController(){
//        return new GroupController();
//    }

    @Bean
    public BasketProductService basketProductService(){
        return new BasketProductService();
    }

    @Bean
    public BasketService basketService(){
        return new BasketService();
    }

    @Bean
    public BasketOrderService basketOrderService(){
        return new BasketOrderService();
    }

}
