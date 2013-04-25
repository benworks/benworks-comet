package benworks.comet.socket.pkt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 便于生成文档只用
 * 
 * @author benworks
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

	/**
	 * 字段顺序，从1开始，0序号表示对整个协议的解释
	 * 
	 * @return
	 */
	int index();

	/**
	 * 字段名
	 * 
	 * @return
	 */
	String name();

	/**
	 * 是否包含通用协议头
	 * 
	 * @return
	 */
	String catalog() default "";

}
