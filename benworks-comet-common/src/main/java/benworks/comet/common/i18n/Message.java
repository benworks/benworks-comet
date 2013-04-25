package benworks.comet.common.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {

	public String group() default MessageBundle.DEFALUT_GROUP_NAME;

	public String[] value() default {};
}
