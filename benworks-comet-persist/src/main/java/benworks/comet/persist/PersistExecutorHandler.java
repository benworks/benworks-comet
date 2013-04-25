package benworks.comet.persist;

import org.springframework.stereotype.Service;

import benworks.comet.broadcast.RequestHandler;
import benworks.comet.common.timer.ScheduleTaskHandler;

@Service
public class PersistExecutorHandler implements RequestHandler,
		ScheduleTaskHandler {

	@Override
	public void onException() {
		PersistSession.rollback();
	}

	@Override
	public void onFinally() {
		PersistSession.clean();
	}

	@Override
	public void onFinish() {
		PersistSession.commit();
	}

}
