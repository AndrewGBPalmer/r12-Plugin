
 /**
  * The template for file was generated by Dynatrace client.
  * 
  * This Action Plugin automatically logs tickets with Service Desk r12 when an incident is detected in Dynatrace
  * 
  * @author Andrew Palmer
  **/ 

package com.datacom.servicedeskAction;
import com.dynatrace.diagnostics.pdk.*;
import java.util.Collection;
import java.util.logging.Logger;


public class ActionPlugin implements ActionV2 {
	
	private String tenant;
	private String user;
	private String requestor;
	private String area;
	private String status = "In Progress";
	private String priority;
	private String configItem;
	private String reportMethod = "Other";
	private String group;
	private String summary;
	private String description;

	private static final Logger log = Logger.getLogger(ActionPlugin.class.getName());
	/**
	 * Initializes the Plugin. This method is called in the following cases:
	 * <ul>
	 * <li>before <tt>execute</tt> is called the first time for this
	 * scheduled Plugin</li>
	 * <li>before the next <tt>execute</tt> if <tt>teardown</tt> was called
	 * after the last execution</li>
	 * </ul>
	 * 
	 * <p>
	 * If the returned status is <tt>null</tt> or the status code is a
	 * non-success code then {@link #teardown(ActionEnvironment)} will be called
	 * next.
	 * 
	 * <p>
	 * Resources like sockets or files can be opened in this method.
	 * Resources like sockets or files can be opened in this method.
	 * @param env
	 *            the configured <tt>ActionEnvironment</tt> for this Plugin
	 * @see #teardown(ActionEnvironment)
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 */
	@Override
	public Status setup(ActionEnvironment env) throws Exception {
		try{
			//TODO: open any required connections
		}
		catch(Exception e){
			//TODO: Handle exceptions from opening the connection
		}
		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Executes the Action Plugin to process incidents.
	 * 
	 * <p>
	 * This method may be called at the scheduled intervals, but only if incidents
	 * occurred in the meantime. If the Plugin execution takes longer than the
	 * schedule interval, subsequent calls to
	 * {@link #execute(ActionEnvironment)} will be skipped until this method
	 * returns. After the execution duration exceeds the schedule timeout,
	 * {@link ActionEnvironment#isStopped()} will return <tt>true</tt>. In this
	 * case execution should be stopped as soon as possible. If the Plugin
	 * ignores {@link ActionEnvironment#isStopped()} or fails to stop execution in
	 * a reasonable timeframe, the execution thread will be stopped ungracefully
	 * which might lead to resource leaks!
	 * 
	 * @param env
	 *            a <tt>ActionEnvironment</tt> object that contains the Plugin
	 *            configuration and incidents
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 */
	@Override
	public Status execute(ActionEnvironment env) throws Exception {
		// this sample shows how to receive and act on incidents
		Collection<Incident> incidents = env.getIncidents();
		for (Incident incident : incidents) {
			//Gather required Data
			tenant = env.getConfigString("tenant");
			area = env.getConfigString("area");
			priority = env.getConfigString("priority");
			configItem = env.getConfigString("configItem");
			group = env.getConfigString("group");
			summary = incident.getMessage();
			description = getDescription(incident);
			//Log data gathered for debugging
			log.info("Incident " + summary + " triggered.");
			log.info("Sending ticket to Service Desk for Tenant:" + tenant);
			log.info(" Incident area:" + area);
			log.info(" Priority:p" + priority);
			log.info(" Configuration Item:" + configItem);
			log.info(" Group:" +group);
			log.info(" Description:\n"+description);
			//send Data off to Service Desk, returns a success status if successful, and an error status if not
			return logTicket(tenant,requestor,user,area,status,priority,configItem,reportMethod,group,summary,description);
		}
		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Generates the ticket in Service Desk using the given parameters
	 * @param tenant
	 * @param requestor
	 * @param user
	 * @param area
	 * @param status
	 * @param priority
	 * @param configItem
	 * @param reportMethod
	 * @param group
	 * @param urgency
	 * @param summary
	 * @return Status: Success if it logged, Error if not
	 */
	private Status logTicket(String tenant,
							  String requestor,
							  String user,
							  String area,
							  String status,
							  String priority,
							  String configItem,
							  String reportMethod,
							  String group,
							  String summary,
							  String description){
		try{
			//TODO: Send ticket to service desk
			return new Status(Status.StatusCode.Success);
		}
		catch(Exception e){//change to whatever exceptions can be generated by trying to log the ticket
			//TODO: handle correct exceptions
			log.info("Ticket not logged, exception: "+e.getMessage());
			return new Status(Status.StatusCode.ErrorTargetServiceExecutionFailed);
		}
	}
	
	/**
	 * Builds a description of the incident
	 * @param Incident incident
	 * @return String Description 
	 */
	private String getDescription(Incident incident){
		StringBuilder desc = new StringBuilder();
		desc.append("Incident from Dynatrace:"+summary);
		desc.append("\n    Tenant:" + tenant);
		desc.append("\n    Priority:"+priority);
		desc.append("\n    Start Time:"+incident.getStartTime().toString());
		desc.append("\n    End Time:"+incident.getEndTime().toString());
		desc.append("\n\nIncident Rule:");
		desc.append("\n    Name:"+incident.getIncidentRule().getName());
		desc.append("\n    Description:"+incident.getIncidentRule().getDescription());
		desc.append("\n\nViolations:");
		for(Violation v: incident.getViolations()){
			desc.append("\n    Violated Measure:"+v.getViolatedMeasure().getName());
			desc.append("\n        Description:"+v.getViolatedMeasure().getDescription());
			desc.append("\n        Source:"+v.getViolatedMeasure().getSource().toString());
		}
		return desc.toString();
	}

	/**
	 * Shuts the Plugin down and frees resources. This method is called in the
	 * following cases:
	 * <ul>
	 * <li>the <tt>setup</tt> method failed</li>
	 * <li>the Plugin configuration has changed</li>
	 * <li>the execution duration of the Plugin exceeded the schedule timeout</li>
	 * <li>the schedule associated with this Plugin was removed</li>
	 * </ul>
	 * <p>
	 * The Plugin methods <tt>setup</tt>, <tt>execute</tt> and
	 * <tt>teardown</tt> are called on different threads, but they are called
	 * sequentially. This means that the execution of these methods does not
	 * overlap, they are executed one after the other.
	 * 
	 * <p>
	 * Examples:
	 * <ul>
	 * <li><tt>setup</tt> (failed) -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, configuration changes, <tt>execute</tt>
	 * ends -&gt; <tt>teardown</tt><br>
	 * on next schedule interval: <tt>setup</tt> -&gt; <tt>execute</tt> ...</li>
	 * <li><tt>execute</tt> starts, execution duration timeout,
	 * <tt>execute</tt> stops -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, <tt>execute</tt> ends, schedule is
	 * removed -&gt; <tt>teardown</tt></li>
	 * </ul>
	 * Failed means that either an unhandled exception is thrown or the status
	 * returned by the method contains a non-success code.
	 * 
	 * <p>
	 * All by the Plugin allocated resources should be freed in this method.
	 * Examples are opened sockets or files.
	 * 
	 * @see #setup(ActionEnvironment)
	 */
	@Override
	public void teardown(ActionEnvironment env) throws Exception {
		//TODO: close any connections
	}
}
