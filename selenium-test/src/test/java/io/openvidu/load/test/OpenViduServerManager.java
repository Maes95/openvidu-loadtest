/*
 * (C) Copyright 2017-2018 OpenVidu (https://openvidu.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.openvidu.load.test;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import io.openvidu.load.test.utils.MonitoringStats;
import io.openvidu.load.test.utils.OpenViduServerMonitor;

/**
 * Manager class for OpenVidu Server node. Collects monitoring information from
 * the machine running OpenVidu Server
 *
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class OpenViduServerManager {

	final static Logger log = getLogger(lookup().lookupClass());

	private Thread pollingThread;
	private AtomicBoolean isInterrupted = new AtomicBoolean(false);
	private OpenViduServerMonitor monitor;

	public OpenViduServerManager() {
		String openViduUrl = OpenViduLoadTest.OPENVIDU_URL.replace("https://", "");
		openViduUrl = openViduUrl.replaceAll(":[0-9]+/$", "");
		openViduUrl = openViduUrl.replaceAll("/$", "");
		this.monitor = new OpenViduServerMonitor(OpenViduLoadTest.SERVER_SSH_USER, openViduUrl);
	}

	public void startMonitoringPolling() {
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread th, Throwable ex) {
				log.error("OpenVidu Server monitoring poll error");
			}
		};
		this.pollingThread = new Thread(() -> {
			while (!this.isInterrupted.get()) {
				MonitoringStats stats = this.monitor.getMonitoringStats();
				if (stats != null) {
					log.info(stats.toString());
					OpenViduLoadTest.logHelper.logServerMonitoringStats(stats);
				}
				try {
					Thread.sleep(OpenViduLoadTest.SERVER_POLL_INTERVAL);
				} catch (InterruptedException e) {
					log.debug("OpenVidu Server monitoring polling thread interrupted");
				}
			}
		});
		this.pollingThread.setUncaughtExceptionHandler(h);
		this.pollingThread.start();
		log.info("OpenVidu Server net, cpu and mem usage is now being monitored (in an interval of {} ms)",
				OpenViduLoadTest.SERVER_POLL_INTERVAL);
	}

	public void stopMonitoringPolling() {
		this.isInterrupted.set(true);
		this.pollingThread.interrupt();
		log.info("OpenVidu Server monitoring poll stopped");
	}

}