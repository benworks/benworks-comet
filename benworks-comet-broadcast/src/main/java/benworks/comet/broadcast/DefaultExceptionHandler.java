package benworks.comet.broadcast;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import benworks.comet.common.data.ErrorCode;
import benworks.comet.common.data.ErrorCodes;
import benworks.comet.common.model.GeneralException;

@Service
public class DefaultExceptionHandler {

	private final Log errorLog = LogFactory.getLog("error.log");

	private AtomicInteger serialCode = new AtomicInteger(0);

	public GeneralResponse processException(GeneralRequest request, Exception ex) {
		Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
		ErrorResponse errorResponse = null;
		if (cause instanceof GeneralException) {
			errorResponse = new ErrorResponse((GeneralException) cause);
		} else {
			errorResponse = new ErrorResponse(
					ErrorCode.get(ErrorCodes.UNKNOWN_REQUEST_ERROR));
		}
		errorResponse.setSerialCode(serialCode.incrementAndGet());
		errorLog.error("******** Request:" + request);
		errorLog.error(errorResponse, cause);
		return errorResponse;
	}

}
