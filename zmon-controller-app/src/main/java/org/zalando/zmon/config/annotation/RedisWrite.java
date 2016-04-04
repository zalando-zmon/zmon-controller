package org.zalando.zmon.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * NamedQualifier.
 * 
 * @author jbellmann
 *
 */
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("redisWrite")
public @interface RedisWrite {
}
