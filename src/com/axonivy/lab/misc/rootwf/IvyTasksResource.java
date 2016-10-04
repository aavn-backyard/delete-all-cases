package com.axonivy.lab.misc.rootwf;

import java.text.MessageFormat;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.TaskQuery;


@Path("{applicationName}/root/tasks")
public class IvyTasksResource {

	@GET
	public String getAllTasks(@QueryParam("running") boolean isRunning) {
		if (isRunning) {
			long runningTasksCount = Ivy.wf().getTaskQueryExecutor()
						.getCount(TaskQuery.create().where()
								.state().isEqual(TaskState.SUSPENDED)
								.or().state().isEqual(TaskState.RESUMED));
			return MessageFormat.format("There are {0} running tasks (either SUSPENDED or RESUMED)", runningTasksCount);
		}
		
		long existingTasksCount = Ivy.wf().getTaskQueryExecutor()
					.getCount(TaskQuery.create().where()
							.taskId().isNotNull());
		return MessageFormat.format("There are {0} existing tasks", existingTasksCount);
		
	}
	
	@POST
	@Path("{task-id}/destroy")
	public String destroyTask(@PathParam("taskId") long taskId) {
		Optional<ITask> task = Optional.ofNullable(Ivy.wf().findTask(taskId));
		task.ifPresent(t -> t.destroy());
		return MessageFormat.format("Task {0} has been destroyed", taskId);
	}
	
	
}
