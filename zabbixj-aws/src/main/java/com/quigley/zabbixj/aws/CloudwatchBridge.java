package com.quigley.zabbixj.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quigley.filesystem.FilesystemPath;
import com.quigley.zabbixj.sender.ZabbixSender;
import com.quigley.zabbixj.sender.ZabbixSenderDataItem;
import com.quigley.zabbixj.sender.ZabbixSenderRequest;
import com.quigley.zabbixj.sender.ZabbixSenderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CloudWatchBridge {
	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.err.println("Usage: CloudwatchBridge <configurationFile>");
			System.exit(1);
		}

		FilesystemPath configurationFile = new FilesystemPath(args[0]);
		log.info("Loading configuration from [" + configurationFile + "].");
		Configuration cfg = new ObjectMapper().readValue(configurationFile.asFile(), Configuration.class);

		runs = new HashMap<Mapping, Long>();
		for(Mapping mapping : cfg.getMappings()) {
			runs.put(mapping, 0L);
		}

		BasicAWSCredentials credentials = new BasicAWSCredentials(cfg.getAwsAccessKey(), cfg.getAwsSecretKey());
		AmazonCloudWatchClient cwClient = new AmazonCloudWatchClient(credentials);
		cwClient.setRegion(Region.getRegion(Regions.fromName(cfg.getAwsRegion())));

		ZabbixSender zabbixSender = new ZabbixSender(cfg.getZabbixHost(), cfg.getZabbixPort());

		while(true) {
			Long now = System.currentTimeMillis();
			List<Mapping> runList = new ArrayList<Mapping>();
			for(Mapping mapping : runs.keySet()) {
				Long lastRun = runs.get(mapping);
				long deltaMs = now - lastRun;
				int deltaMinutes = (int) (deltaMs / 60000);
				if(deltaMinutes >= mapping.getPeriodMinutes()) {
					runList.add(mapping);
					log.debug("Added mapping.");
				}
			}

			if(runList.size() > 0) {
				log.info("[" + runList.size() + "] mappings to run.");
				now = System.currentTimeMillis();
				ZabbixSenderRequest zabbixSenderRequest = new ZabbixSenderRequest();
				for(Mapping mapping : runList) {
					GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
							.withStartTime(new Date(now - (60000 * mapping.getPeriodMinutes())))
							.withNamespace(mapping.getSource().getNamespace())
							.withPeriod(60 * mapping.getPeriodMinutes())
							.withDimensions(new Dimension()
									.withName(mapping.getSource().getDimensionName())
									.withValue(mapping.getSource().getDimensionValue())
							)
							.withMetricName(mapping.getSource().getMetricName())
							.withStatistics(mapping.getSource().getStatistic())
							.withEndTime(new Date(now));

					try {
						log.debug("Requesting CloudWatch data.");
						GetMetricStatisticsResult result = cwClient.getMetricStatistics(request);
						log.debug("Received [" + result.getDatapoints().size() + "] datapoints.");
						for(Datapoint datapoint : result.getDatapoints()) {
							if(mapping.getSource().getStatistic().equals("Maximum")) {
								double value = datapoint.getMaximum();
								ZabbixSenderDataItem item = new ZabbixSenderDataItem(mapping.getDestination().getHost(), mapping.getDestination().getKey(), "" + value);
								zabbixSenderRequest.addData(item);
							}
						}

					} catch(Exception ex) {
						log.error("Failure.", ex);

					} finally {
						runs.put(mapping, now);
					}
				}
				if(zabbixSenderRequest.getData() != null && zabbixSenderRequest.getData().size() > 0) {
					try {
						ZabbixSenderResponse zabbixSenderResponse = zabbixSender.send(zabbixSenderRequest);
						log.info("Received [" + zabbixSenderResponse + "].");

					} catch(Exception ex) {
						log.error("Failure.", ex);
					}
				}
			}

			Thread.sleep(15000);
		}
	}

	private static Map<Mapping, Long> runs;
	private static final Logger log = LoggerFactory.getLogger(CloudWatchBridge.class);
}