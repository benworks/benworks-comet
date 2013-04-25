package benworks.comet.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应标注，此标注暂时只用于代码生成之用
 * 
 * @author benworks
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Responses {

	Response[] value();
}
