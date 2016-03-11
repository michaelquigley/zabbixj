package com.quigley.zabbixj.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

import java.util.Date;

public class CloudwatchFetcher {
	public static void main(String[] args) throws Exception {
		final String awsAccessKey = args[0];
		final String awsSecretKey = args[1];
		final String queueName = args[2];

		BasicAWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		AmazonCloudWatchClient cwClient = new AmazonCloudWatchClient(creds);
		cwClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		int minutes = 5;

		while(true) {
			long now = System.currentTimeMillis();
			GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
					.withStartTime(new Date(now - (60000 * minutes)))
					.withNamespace("AWS/SQS")
					.withPeriod(60 * minutes)
					.withDimensions(new Dimension().withName("QueueName").withValue(queueName))
					.withMetricName("ApproximateNumberOfMessagesVisible")
					.withStatistics("Minimum", "Maximum")
					.withEndTime(new Date(now));

			GetMetricStatisticsResult result = cwClient.getMetricStatistics(request);
			for(Datapoint datapoint : result.getDatapoints()) {
				System.out.println(datapoint);
			}
			System.out.println("--");

			Thread.sleep(1000 * 60 * minutes);
		}
	}
}