package benworks.comet.broadcast.annotation;

/**
 * 响应标注，此标注暂时只用于代码生成之用
 * 
 * @author benworks
 * 
 */
public @interface Response {

	/**
	 * 是否为广播信息，只对成功的信息有效
	 * 
	 * @return
	 */
	boolean broadcast() default false;

	/**
	 * 成功响应类型
	 * 
	 * @return
	 */
	Class<?> type();

	/**
	 * 描述信息
	 * 
	 * @return
	 */
	String desc() default "";

}
