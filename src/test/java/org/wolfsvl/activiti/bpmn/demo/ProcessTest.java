package org.wolfsvl.activiti.bpmn.demo;

import org.activiti.engine.test.ActivitiRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;



public class ProcessTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();


	@Test
	public void testProcess() throws Exception {
		String processId = "wolfsvlProcess";
		FileCreator fileCreator = new FileCreator();
		fileCreator.createFiles(createBPMNwolfsvlProcess(processId),processId,"target",activitiRule);
	
	}
	
	@Test
	public void testJavaProcess() throws Exception {
		String processId = "badProcess";
		FileCreator fileCreator = new FileCreator();
		fileCreator.createFiles(createBPMNBadProcess(processId),processId,"target",activitiRule);
	
	}


	@Test
	public void testDispatcherProcess() throws Exception {
		String processId = "dipatcherProcess";
		FileCreator fileCreator = new FileCreator();
		fileCreator.createFiles(createBPMNDispatcherProcess(processId),processId,"target",activitiRule);
	
	}


	private  org.activiti.bpmn.model.Process createBPMNDispatcherProcess(String processId) {
		Process process = new Process(processId);
		process
			.insertStartEvent("mainStart")
			.appendScriptTask("preparParallelSubprocesses")
			.beginSubProcess("forNumberOfSubprocesses","forNumberOfSubprocesses", Process.SEQUENTIAL)
				.appendScriptTask("prepareInvoke")
				.appendCallActivity("invokeSubprocess")
			.endSubProcess()
			.appendCallActivity("receiveFromSubworkflow")
			.beginSubProcess("correlationIds", "correlationIds", Process.SEQUENTIAL)
				.appendScriptTask("processResponse")
			.endSubProcess()
			.appendScriptTask("updateStatistic")
			.appendEndEvent("mainEnd")
		;
		return process.getBPMNProcess();
	}

	private  org.activiti.bpmn.model.Process createBPMNBadProcess(String processId) {
		Process process = new Process(processId);
		process
			.insertStartEvent("mainStart")
			.appendScriptTask("preparParallelSubprocesses")
			.beginSubProcess("forNumberOfSubprocesses")
				.appendScriptTask("prepareInvoke")
				.beginSubProcess("invokeSubprocess")
					.appendScriptTask("dummy")
				.endSubProcess()
			.endSubProcess()
			.appendEndEvent("mainEnd")
		;
		return process.getBPMNProcess();
	}

	private  org.activiti.bpmn.model.Process createBPMNwolfsvlProcess(String processId) {
		Process process = new Process(processId);
		// Business Rule drools only!?
		process.insertStartEvent("mainStart")
			.appendScriptTask("scriptTask")
			.openExlusiveGateway("gatewayOpen")
				.beginCondition("${1==0}", "gatewayOpen")
					.appendManualTask("manualTask22")
				.endCondition("gatewayClose")
				.beginCondition("${1==1}","gatewayOpen")
					.appendManualTask("manualTask11")
					.beginSubProcess("simpleSubProcss")
						.appendServiceTask("serviceTask2")
					.endSubProcess()
					.appendManualTask("manualTask12")
			.closeExlusiveGateway("gatewayClose")
			.beginSubProcess("multiInstanceSubprocess", "3",Process.SEQUENTIAL)
				.appendManualTask("manualTask31")
			.endSubProcess()
			.appendCallActivity("callActivity")
			.appendServiceTask("serviceTask")
			.openExlusiveGateway("gatewayOpen2")
				.beginCondition("${1==0}", "gatewayOpen2")
					.appendSendTask("sendTask")
					.appendReceiveTask("receiveTask")
				.endCondition("gatewayClose2")
				.beginCondition("${1==1}","gatewayOpen2")
					.appendSendTask("sendTask2")
					.appendUserTask("userTask")
			.closeExlusiveGateway("gatewayClose2")
//	bad layout		.addGoto("manualTask1")
			.appendEndEvent("mainEnd")
		;
		return process.getBPMNProcess();
	}

}