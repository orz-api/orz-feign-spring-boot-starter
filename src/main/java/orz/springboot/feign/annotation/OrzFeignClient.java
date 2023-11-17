package orz.springboot.feign.annotation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AliasFor;
import orz.springboot.feign.OrzFeignConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@FeignClient(configuration = OrzFeignConfiguration.class)
public @interface OrzFeignClient {
    @AliasFor(annotation = FeignClient.class, value = "value")
    String value() default "";

    @AliasFor(annotation = FeignClient.class, value = "contextId")
    String contextId() default "";

    @AliasFor(annotation = FeignClient.class, value = "name")
    String name() default "";

    @AliasFor(annotation = FeignClient.class, value = "qualifiers")
    String[] qualifiers() default {};

    @AliasFor(annotation = FeignClient.class, value = "url")
    String url() default "";

    @AliasFor(annotation = FeignClient.class, value = "dismiss404")
    boolean dismiss404() default false;

    @AliasFor(annotation = FeignClient.class, value = "fallback")
    Class<?> fallback() default void.class;

    @AliasFor(annotation = FeignClient.class, value = "fallbackFactory")
    Class<?> fallbackFactory() default void.class;

    @AliasFor(annotation = FeignClient.class, value = "path")
    String path() default "";

    @AliasFor(annotation = FeignClient.class, value = "primary")
    boolean primary() default true;
}
