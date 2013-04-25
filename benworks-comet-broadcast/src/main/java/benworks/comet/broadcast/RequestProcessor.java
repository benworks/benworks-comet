/**
 * 
 */
package benworks.comet.broadcast;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import benworks.comet.common.data.ErrorCodes;
import benworks.comet.common.model.GeneralException;
import benworks.comet.socket.ConnectorManager;

/**
 * 请求处理器
 * 
 * @author benworks
 * 
 */
@Service
public class RequestProcessor {

	private final static Log timeLog = LogFactory.getLog("request-time.log");

	private final static Log log = LogFactory.getLog(RequestProcessor.class);

	@Autowired
	private ClassNameHandlerMapping classNameHandlerMapping;
	@Autowired
	private DefaultExceptionHandler defaultExceptionHandler;
	@Autowired
	private ConnectorManager connectorManager;
	@Autowired
	private Set<RequestHandler> requestHandlers;
	private ThreadPoolExecutor executor;

	public RequestProcessor() {
		this.executor = new RequestThreadPoolExecutor();
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void process(GeneralRequest request) {
		executor.execute(new RequestTask(request));
	}

	private void doResponse(GeneralRequest request, GeneralResponse response) {
		if (response != null) {
			response.setSerial(request.getSerial());
			connectorManager.singleBroadcast(request.getSid(),
					Amf3Utils.getAmf3ByteArray(response));
		}
	}

	private GeneralController getController(String action) {
		int end = action.lastIndexOf("/");
		return classNameHandlerMapping.getController(action.substring(1, end));
	}

	class RequestTask implements Runnable {

		private GeneralRequest request;

		public RequestTask(GeneralRequest request) {
			this.request = request;
		}

		public GeneralRequest getRequest() {
			return request;
		}

		public void run() {
			if (log.isDebugEnabled()) {
				log.debug("### Request:" + request);
			}
			long beginTime = System.currentTimeMillis();
			SerialUtils.set(request.getSerial());
			try {
				GeneralController controller = getController(request
						.getAction());
				if (controller == null) {
					throw new GeneralException(ErrorCodes.HANDLE_NOT_FOUND);
				}
				GeneralResponse response = controller.handle(request);
				doResponse(request, response);
				for (RequestHandler handler : requestHandlers) {
					handler.onFinish();
				}
			} catch (Exception e) {
				try {
					GeneralResponse response = defaultExceptionHandler
							.processException(request, e);
					doResponse(request, response);
					for (RequestHandler handler : requestHandlers) {
						handler.onException();
					}
				} catch (RuntimeException e1) {
					log.error("### Request:" + request + ";Thread:"
							+ Thread.currentThread().getName());
					e1.printStackTrace();
				}
			}
			SerialUtils.clear();
			timeLog.info(request.getAction() + "|"
					+ (System.currentTimeMillis() - beginTime));
		}
	}

	class RequestRejectedExecutionHandler implements RejectedExecutionHandler {

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			log.error("### Reject request:" + r);
			Exception e = new GeneralException(ErrorCodes.TOO_MANY_REQUEST);
			if (r instanceof RequestTask) {
				RequestTask task = (RequestTask) r;
				GeneralResponse response = defaultExceptionHandler
						.processException(task.getRequest(), e);
				doResponse(task.getRequest(), response);
			}
		}
	}

	class RequestThreadPoolExecutor extends ThreadPoolExecutor {
		public final static int MAX_WAIT_MS = 10000;
		public final static int MAX_THREAD_COUNT = 1000;
		public final static int CORE_THREAD_COUNT = 10;

		public RequestThreadPoolExecutor() {
			super(CORE_THREAD_COUNT, MAX_THREAD_COUNT, 60L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(MAX_WAIT_MS),
					new RequestRejectedExecutionHandler());
		}

		protected void afterExecute(Runnable r, Throwable t) {
			for (RequestHandler handler : requestHandlers) {
				handler.onFinally();
			}
			if (t != null) {
				t.printStackTrace();
			}
		}

	}
}
