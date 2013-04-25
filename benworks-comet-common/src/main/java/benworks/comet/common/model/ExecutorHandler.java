package benworks.comet.common.model;

/**
 * 
 * @author benworks
 * 
 */
public interface ExecutorHandler {

	void onFinish();

	void onException();

	void onFinally();
}
