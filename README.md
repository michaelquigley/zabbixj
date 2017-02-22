# Zabbix/J

Zabbix/J is a framework for integrating Zabbix monitoring into Java applications. Zabbix is an “enterprise-class open source distributed monitoring solution,” which provides telemetry and triggers across entire infrastructures. Zabbix/J makes it simple to expose metrics from your Java applications, giving them visibility as first class citizens in a Zabbix deployment.

The requirements for Zabbix/J are minimal. There is no need for any sort of JMX or application container—although it integrates well with these. Your application data is provided to Zabbix/J through a simple, one-method Java interface. When you start your application, you'll just instantiate, configure and start a Zabbix/J singleton—if you're using the Spring Framework, you can do this from a Spring context. That's all there is to it.

You can include Zabbix/J into your Maven-based Java projects with the following coordinates:

    <dependency>
    	<groupId>com.quigley</groupId>
    	<artifactId>zabbixj</artifactId>
    	<version>3</version>
    </dependency>

## Using Zabbix/J

I'll explain parts of Zabbix (from Zabbix/J's point of view), but I assume that you have a basic understanding of how Zabbix works. If you do not, I would recommend starting with their website—see the Zabbix manual for more information.

#### Exposing Metrics

Zabbix refers to metrics through named “keys.” There is no requirement that keys are structured in any particular way—it's just a unique string, which refers to a piece of information that a Zabbix agent can provide. For example, the Zabbix agent for Linux uses the key net.if.total[eth0] to refer to the total number of bytes transferred across the eth0 interface.

Even though there is no hard requirement about their structure, the keys do follow a convention. Components of the key are separated by periods (net.if.total), and “parameters” are provided within brackets ([eth0]). Multiple parameters are separated by commas inside the brackets. Zabbix/J provides the MetricsKey class to encapsulate this convention.

To expose metrics from your application, you will need to implement a “metrics provider.” You do this by implementing the (unsurprisingly named) MetricsProvider interface, which implements a single method:

	public interface MetricsProvider {
		/**
	 	* @param mKey a MetricsKey instance describing the metric to retrieve.
	 	* @return the value of the key.
	 	* @throws MetricsException when a problem is encountered retrieving a value
		 *         for the specified key; typically when a key is not found.
	 	*/
		public Object getValue(MetricsKey mKey) throws MetricsException;
	}

Your metrics provider implementation is free to do practically anything imaginable to retrieve data to be exposed to Zabbix. The getValue method will be called with the key as requested from the Zabbix infrastructure. Your code is free to interpret that key however you wish. The MetricsKey provided in the call makes it easy to access the key and its parameters.

Data in Zabbix is represented as either an integer, a floating point number or a string. Zabbix/J will simply call toString on whatever Object you return from the getValue call when communicating the requested metric to Zabbix.

My advice would be to do any “heavy lifting” in a separate thread. The calls to getValue on your metrics provider should return very quickly—so don't do expensive operations from that method. It works well to use a thread to compute the metrics values, and then store them in a data structure (like a Map), which the MetricsProvider queries. You can go crazy with expensive computation and accessing slow resources to generate your metrics, as long as it's done in the background.

#### Embedding the Agent

The framework provides an “agent,” intended to be used as a singleton in your application, to interface with Zabbix. Your application will need to create an instance of the ZabbixAgent class, configure it, and then start it up. And like I mentioned previously, it works well to do this declaratively through a Spring context definition.

The agent supports two modes of operation, “passive” (the traditional Zabbix agent style) and “active.” Your metrics providers work exactly the same way in either of the agent modes. The difference lies in how the application is integrated into your Zabbix infrastructure. The passive agent opens a TCP socket, and then listens for requests from your Zabbix server—typically the server will poll the agent periodically to request data updates. The active agent connects directly to the service, requests a list of metrics keys and update intervals, and then the agent is responsible for sending the metrics to the server on those intervals.

Here's an example of configuring the ZabbixAgent class as a passive agent. There's very little configuration, as the default behavior is to operate as a passively:

	ZabbixAgent agent = new ZabbixAgent();
	agent.setListenPort(10050);
	agent.addProvider("example", new ExampleMetricsProvider());
	agent.addProvider("java", new JVMMetricsProvider());
	agent.start();

That reminds me—did you notice the “example” and “java” in the calls to addProvider? Those are arbitrary names given to the providers configured in the agent. If your metrics provider is expecting a key named random and the provider is named example, then Zabbix will need to request the key example.random. The Zabbix/J framework will use example to refer to the metrics provider, and will then pass the remainder of the key along in the call to getValue as a MetricsKey instance.

Configuring Zabbix/J for passive operation is straightforward—we instantiate the agent, set a port for the passive agent socket, add some metrics providers and start the agent.

Here is how an active agent is configured:

	ZabbixAgent agent = new ZabbixAgent();
	agent.setEnableActive(true);
	agent.setEnablePassive(false);
	agent.setHostName(hostName);
	agent.setServerAddress(InetAddress.getByName(serverAddress));
	agent.setServerPort(serverPort);
	agent.addProvider("example", new ExampleMetricsProvider());
	agent.addProvider("java", new JVMMetricsProvider());
	agent.start();

Configuring an active agent is slightly more involved, but not much more complicated. The agent is instantiated, and then we enable active operation and disable passive operation (you could configure the agent for both passive and active operation simultaneously). Then, we set the “host name”—active agents in Zabbix require a name, which uniquely identifies the agent to Zabbix. The server address and port are pretty simple—those are just the address and port of your listening Zabbix server. Providers are added just like in the passive example, and the agent is started.

#### The Client

Zabbix/J includes a client component, useful for querying metrics from passive agents. It couldn't be much simpler to use—instantiate the PassiveAgentClient class, providing the address and port of the Zabbix agent you want to query. Then, just call the getValues method, providing a List of metrics keys. The call will return a Map containing the values returned from the agent.

#### The Examples

The repository includes a handful of examples that you can experiment with in the process of getting started. The zabbixj-examples Maven module makes it easy to build a single jar containing the examples and their dependencies.

	zabbixj-examples$ mvn package

The ExampleActiveAgent and ExamplePassiveAgent examples both use the ExampleMetricsProvider to expose a random number to Zabbix. Here's how I run them:

	zabbixj-examples$ java -cp target/zabbixj-examples-1.0.1-jar-with-dependencies.jar com.quigley.zabbixj.examples.ExamplePassiveAgent
	Usage: ExamplePassiveAgent <listenPort>

	zabbixj-examples$ java -cp target/zabbixj-examples-1.0.1-jar-with-dependencies.jar com.quigley.zabbixj.examples.ExampleActiveAgent
	Usage: ExampleActiveAgent <hostName> <serverAddress> <serverPort>

The examples also provide an ExamplePassiveAgentClient, which makes it easy to query and monitor passive Zabbix agents:

	zabbixj-examples$ java -cp target/zabbixj-examples-1.0.1-jar-with-dependencies.jar com.quigley.zabbixj.examples.ExamplePassiveAgentClient
	Usage: ExamplePassiveAgentClient <address> <port> [-d<sec>] <key> ... <keyN>
	
Try out the -d option—it'll repeatedly poll the agent on the specified interval. That can be pretty handy.