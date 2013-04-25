package benworks.comet.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在GeneralException 中引入多个enum，用于代码生成时静态变量的生成
 * 
 * @author benworks
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface IncludeEnums {

	IncludeEnum[] value();
}
