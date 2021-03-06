/* Copyright 2017 Telstra Open Source
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.bitbucket.kilda.controller.heartbeat;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Heartbeat is a simple mechanism for monitoring health.
 *
 * It sends a record to a kafka topic every period (the period is configurable).
 *
 * REFERENCES - Producer:
 * https://kafka.apache.org/0101/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
 * - Consumer:
 * https://kafka.apache.org/0101/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
 */
@Named
public class Heartbeat {
	
	@Inject
	private Consumer hbc;
	
	@Inject
	private Producer hbp;

	public void start() {
		hbp.run();
		hbc.run();
	}

}
