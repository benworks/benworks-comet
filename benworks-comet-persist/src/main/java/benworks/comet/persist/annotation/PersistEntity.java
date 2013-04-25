package benworks.comet.persist.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记实体的存储设置
 * 
 * @author benworks
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PersistEntity {

	/**
	 * 标记对象是否需要cache
	 * 
	 * @return
	 */
	boolean cache() default true;

	/**
	 * 标记实体是是否为不可变的，不可改变的实体不用保留副本来做动态更新，性能会更好
	 * 
	 * @return
	 */
	boolean immutable() default false;

}
