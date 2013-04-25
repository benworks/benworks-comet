package benworks.comet.common.model;

import benworks.comet.common.data.ErrorCodes;

/**
 * 没有权限异常
 * 
 * @author benworks
 * 
 */
public class PermissionException extends GeneralException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2658414064047517502L;

	public PermissionException() {
		super(ErrorCodes.NO_PERMISSION);
	}

}
