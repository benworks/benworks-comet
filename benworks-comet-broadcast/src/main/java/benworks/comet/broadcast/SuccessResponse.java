/**
 * 
 */
package benworks.comet.broadcast;

/**
 * @author benworks
 * 
 */
public class SuccessResponse extends GeneralResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3852729057809659100L;

	private static SuccessResponse instance = new SuccessResponse();

	private SuccessResponse() {

	}

	public static SuccessResponse getInstance() {
		return instance;
	}

}
