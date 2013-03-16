package org.wolfsvl.activiti.bpmn.demo;

import java.io.File;
import java.io.InputStream;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.io.FileUtils;

class PNGCreator {

	void createPNG(org.activiti.bpmn.model.Process process, String processId, String dir, ActivitiRule activitiRule)
			throws Exception {
		BpmnModel model = new BpmnModel();

		model.addProcess(process);

		new BpmnAutoLayout(model).execute();

		activitiRule.getRepositoryService().createDeployment().addBpmnModel("dynamic-model.bpmn", model).name("myDeployment")
				.deploy();

		String processDefinitionId = activitiRule.getRepositoryService().createProcessDefinitionQuery().list().get(0).getId();
		InputStream processDiagram = activitiRule.getRepositoryService().getProcessDiagram(processDefinitionId);

		FileUtils.copyInputStreamToFile(processDiagram, new File(dir + "/" + processId + ".png"));
	}

}