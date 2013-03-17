package org.wolfsvl.activiti.bpmn.demo;

import java.io.File;
import java.io.InputStream;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.io.FileUtils;

class FileCreator {

	void createFiles(org.activiti.bpmn.model.Process process, String processId, String dir, ActivitiRule activitiRule)
			throws Exception {
		String resourceName = "dynamic-model.bpmn";

		BpmnModel model = new BpmnModel();

		model.addProcess(process);

		new BpmnAutoLayout(model).execute();

		Deployment deployment = activitiRule.getRepositoryService().createDeployment().addBpmnModel(resourceName, model)
				.name("myDeployment").deploy();

		String processDefinitionId = activitiRule.getRepositoryService().createProcessDefinitionQuery().list().get(0).getId();

		InputStream processBpmn = activitiRule.getRepositoryService().getResourceAsStream(deployment.getId(), resourceName);
		FileUtils.copyInputStreamToFile(processBpmn, new File(dir + "/" + processId + ".bpmn"));

		InputStream processDiagram = activitiRule.getRepositoryService().getProcessDiagram(processDefinitionId);

		FileUtils.copyInputStreamToFile(processDiagram, new File(dir + "/" + processId + ".png"));
	}

}