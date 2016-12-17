package com.gsd.pos.jobs;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.gsd.pos.utils.Config;

public class JobManager {
	private final ScheduledExecutorService scheduler;
	private static final Logger logger = Logger.getLogger(JobManager.class
			.getName());
	private static JobManager jobManager = new JobManager();

	private JobManager() {
		this(0);
	}

	public static JobManager getInstance() {
		return jobManager;
	}

	public JobManager(int poolSize) {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		logger.debug("Created Scheduler");
	}

	public void start() {
		logger.debug("Starting JobManager");
		final Runnable retriever = new ShiftCloseReportRetriever();
		new Thread(retriever).start();
		String frequencyValue = Config
				.getProperty("fetch.frequency.value", "4");
		logger.debug("Sechduling at a frequency of [" + frequencyValue
				+ "] hours");
		int hours = Integer.parseInt(frequencyValue);
		DateTime startAt = new DateTime().plusHours(2);
		logger.debug("Will start after 2 hours , at [" + startAt.toString() + "]");
		long delay = startAt.toDate().getTime() - System.currentTimeMillis();
		final ScheduledFuture<?> retrieverHandle = scheduler
				.scheduleAtFixedRate(retriever, (delay / (60 * 60 * 1000)),
						hours, HOURS);
		logger.debug("Created Schedule");
	}

	public void stop() {
		logger.debug("Stopping JobManager");
		if (scheduler != null) {
			scheduler.shutdownNow();

		}
	}
}
