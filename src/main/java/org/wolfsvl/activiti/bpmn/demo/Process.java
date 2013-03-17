package org.wolfsvl.activiti.bpmn.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.SendTask;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;

public class Process {

	public static final boolean SEQUENTIAL = true;

	public static final boolean PARALLEL = false;

	private org.activiti.bpmn.model.Process process = null;

	private String lastId = null;

	private int flowCounter = 0;

	private String condition;

	private Stack<SubProcess> processStack = new Stack<SubProcess>();

	private FlowElementsContainer currentContainer = null;

	public Process(String processID) {
		this.process = new org.activiti.bpmn.model.Process();
		this.process.setId(processID);
		this.currentContainer = process;
	}

	public Process insertStartEvent(String id) {
		StartEvent startEvent = new StartEvent();
		startEvent.setId(id);
		startEvent.setName(id);
		lastId = id;
		this.currentContainer.addFlowElement(startEvent);
		return this;
	}

	public Process appendEndEvent(String id) {
		addActivity(id, new EndEvent());
		return this;
	}

	public org.activiti.bpmn.model.Process getBPMNProcess() {
		return this.process;
	}

	public Process appendManualTask(String id) {
		ManualTask manualTask = new ManualTask();
		manualTask.setId(id);
		manualTask.setName(id);
		addActivity(id, manualTask);
		return this;
	}

	public Process appendCallActivity(String id) {
		CallActivity callActivity = new CallActivity();
		callActivity.setId(id);
		callActivity.setName(id);
		addActivity(id, callActivity);
		return this;
	}

	public Process appendReceiveTask(String id) {
		ReceiveTask receiveTask = new ReceiveTask();
		receiveTask.setId(id);
		receiveTask.setName(id);
		addActivity(id, receiveTask);
		return this;
	}

	public Process appendScriptTask(String id) {
		ScriptTask scriptTask = new ScriptTask();
		scriptTask.setId(id);
		scriptTask.setName(id);
		addActivity(id, scriptTask);
		return this;
	}

	public Process appendSendTask(String id) {
		SendTask sendTask = new SendTask();
		sendTask.setId(id);
		sendTask.setName(id);
		sendTask.setType("mail");
		List<FieldExtension> fieldExtensions = new ArrayList<FieldExtension>(3);
		FieldExtension to = new FieldExtension();
		to.setFieldName("to");
		to.setStringValue("wolf@gmx.tv");
		FieldExtension text = new FieldExtension();
		text.setFieldName("text");
		text.setStringValue("Hello World!");
		fieldExtensions.add(to);
		fieldExtensions.add(text);
		sendTask.setFieldExtensions(fieldExtensions);
		addActivity(id, sendTask);
		return this;
	}

	public Process appendServiceTask(String id) {
		ServiceTask serviceTask = new ServiceTask();
		serviceTask.setId(id);
		serviceTask.setName(id);
		serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
		serviceTask.setImplementation("xx");
		// serviceTask.setOperationRef("noOperation");
		// IMPLEMENTATION_TYPE_WEBSERVICE
		// serviceTask.setType("expression");
		// serviceTask.setType("mail");
		addActivity(id, serviceTask);
		return this;
	}

	public Process openExlusiveGateway(String id) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(id);
		gateway.setName(id);
		addActivity(id, gateway);
		return this;
	}

	public Process closeExlusiveGateway(String id) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(id);
		gateway.setName(id);
		addActivity(id, gateway);
		return this;
	}

	public Process beginCondition(String condition, String gatewayId) {
		this.lastId = gatewayId;
		this.condition = condition;
		return this;
	}

	public Process endCondition(String targetId) {
		addActivity(targetId, null);
		return this;
	}

	public Process beginSubProcess(String id) {
		return beginSubProcess(id, null, Process.SEQUENTIAL);
	}

	public Process beginSubProcess(String id, String loopCardinality, boolean sequential) {
		SubProcess subProcess = new SubProcess();
		subProcess.setId(id);
		subProcess.setName(id);
		MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
		if (loopCardinality != null) {
			loopCharacteristics.setLoopCardinality("1");
		}
		loopCharacteristics.setSequential(sequential);
		subProcess.setLoopCharacteristics(loopCharacteristics);
		addActivity(id, subProcess);
		if (this.currentContainer instanceof SubProcess) {
			processStack.push((SubProcess) this.currentContainer);
		}
		this.currentContainer = subProcess;
		this.lastId = null;
		insertStartEvent("__start" + id);
		return this;
	}

	public Process endSubProcess() {
		SubProcess currentSubProcess = (SubProcess) this.currentContainer;
		appendEndEvent("__end" + currentSubProcess.getId());
		this.lastId = currentSubProcess.getId();
		if (this.processStack.isEmpty()) {
			this.currentContainer = this.process;
		} else {
			this.currentContainer = this.processStack.pop();
		}
		return this;
	}

	public Process appendGoto(String targetId) {
		addActivity(targetId, null);
		return this;
	}

	private void addActivity(String id, FlowElement flowElement) {
		if (flowElement != null) {
			flowElement.setId(id);
			this.currentContainer.addFlowElement(flowElement);
		}
		flowCounter++;
		SequenceFlow flow = new SequenceFlow();
		flow.setId("flow" + flowCounter);
		if (this.condition != null) {
			flow.setConditionExpression(this.condition);
			flow.setName(this.condition);
			this.condition = null;
		}
		flow.setSourceRef(lastId);
		flow.setTargetRef(id);
		this.lastId = id;
		this.currentContainer.addFlowElement(flow);

	}

	public Process appendUserTask(String id) {
		UserTask userTask = new UserTask();
		userTask.setId(id);
		userTask.setName(id);
		addActivity(id, userTask);
		return this;
	}

}
