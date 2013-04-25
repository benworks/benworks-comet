/**
 * 
 */
package benworks.comet.persist.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 列表查询的时候忽略的属性，当属性对应大数据字段时，作此标记可在获取list的数据接口中忽略此字段
 * 
 * @author benworks
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ListIgnore {

}
