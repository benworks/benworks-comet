package benworks.comet.common.timer;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import benworks.comet.common.utils.RandomUtils;

/**
 * ScheduleTask管理器，所有的ScheduleTask需要一个Schedule来排程，所以有此Manager
 * 
 * @author benworks
 * 
 */
@Service
public class ScheduleManager {

	private static final Log errorLog = LogFactory.getLog("error.log");
	public static boolean statable = true;

	@Autowired
	private Set<ScheduleTaskHandler> scheduleTaskHandlers;

	private ScheduledThreadPoolExecutor[] schedulers;

	private TaskScheduler taskScheduler;

	private final static int SCHEDULER_COUNT = 5;
	private final static int SCHEDULER_THREAD_COUNT = 10;

	public ScheduleManager() {
		schedulers = new Scheduler[SCHEDULER_COUNT];
		for (int i = 0; i < SCHEDULER_COUNT; i++) {
			schedulers[i] = new Scheduler();
		}
		taskScheduler = new SpringTaskScheduler();
	}

	public ScheduledThreadPoolExecutor[] getSchedulers() {
		return schedulers;
	}

	private ScheduledThreadPoolExecutor getScheduler() {
		return schedulers[RandomUtils.nextInt(SCHEDULER_COUNT)];
	}

	/**
	 * 
	 * @param command
	 * @param cron
	 *            表达式 "* 15 9-17 * * MON-FRI *"<br>
	 *            一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。<br>
	 *            按顺序依次为 <br>
	 *            秒（0~59） <br>
	 *            分钟（0~59） <br>
	 *            小时（0~23） <br>
	 *            天（月）（0~31，但是你需要考虑你月的天数） <br>
	 *            月（0~11） <br>
	 *            天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT） <br>
	 *            年份（1970－2099）
	 * @return
	 */
	public ScheduledFuture<?> schedule(Runnable command, String cron) {
		if (statable)
			command = new StatEnableTask(command);
		return taskScheduler.schedule(command, new CronTrigger(cron));
	}

	public ScheduledFuture<?> schedule(Runnable command, Date date) {
		if (statable)
			command = new StatEnableTask(command);
		return taskScheduler.schedule(command, date);
	}

	public ScheduledFuture<?> schedule(Runnable command, long delayms) {
		if (statable)
			command = new StatEnableTask(command);
		return getScheduler().schedule(command, delayms, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
			long delayms, long periodms) {
		if (statable)
			command = new StatEnableTask(command);
		return getScheduler().scheduleAtFixedRate(command, delayms, periodms,
				TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
			Date startTime, long period) {
		if (statable)
			command = new StatEnableTask(command);
		return taskScheduler.scheduleAtFixedRate(command, startTime, period);
	}

	private class StatEnableTask implements Runnable {
		// private final static Log timeLog =
		// LogFactory.getLog("schedule-time.log");

		private final Runnable task;

		public StatEnableTask(Runnable task) {
			this.task = task;
		}

		public void run() {
			// long beginTime = System.nanoTime();
			if (task == null)
				return;
			try {
				task.run();
				for (ScheduleTaskHandler handler : scheduleTaskHandlers) {
					handler.onFinish();
				}
			} catch (Throwable t) {
				errorLog.error(task);
				errorLog.error(t, t);
				try {
					for (ScheduleTaskHandler handler : scheduleTaskHandlers) {
						handler.onException();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				for (ScheduleTaskHandler handler : scheduleTaskHandlers) {
					handler.onFinally();
				}
				// timeLog.info(task.getClass().getSimpleName() + "|" +
				// (System.nanoTime() - beginTime));
			}

		}

	}

	private static class Scheduler extends ScheduledThreadPoolExecutor {

		public Scheduler() {
			super(SCHEDULER_THREAD_COUNT);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			if (t != null)
				t.printStackTrace();

		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
		}
	}

	private class SpringTaskScheduler extends ThreadPoolTaskScheduler {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6845869115437347960L;

		@Override
		protected ScheduledExecutorService createExecutor(int poolSize,
				ThreadFactory threadFactory,
				RejectedExecutionHandler rejectedExecutionHandler) {
			return schedulers[0];
		}

		@Override
		public ScheduledExecutorService getScheduledExecutor()
				throws IllegalStateException {
			return getScheduler();
		}

	}
}
