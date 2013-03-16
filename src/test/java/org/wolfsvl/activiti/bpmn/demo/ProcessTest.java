package org.wolfsvl.activiti.bpmn.demo;

import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;



public class ProcessTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();


	@Test
	public void testProcess() throws Exception {
		String processId = "wolfsvlProcess";
		PNGCreator pngCreator = new PNGCreator();
		pngCreator.createPNG(createBPMNProcess(processId),processId,"target",activitiRule);
	
	}


	private  org.activiti.bpmn.model.Process createBPMNProcess(String processId) {
		Process process = new Process(processId);
		// Business Rule drools only!?
		process.appendStartEvent("mainStart")
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