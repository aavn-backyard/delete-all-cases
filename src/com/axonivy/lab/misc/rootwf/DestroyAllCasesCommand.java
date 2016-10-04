package com.axonivy.lab.misc.rootwf;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.CaseState;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

public class DestroyAllCasesCommand implements Callable<Long> {

	private ProgressMonitor progressMonitor;

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	@Override
	@RequiresRole("DeleteAllCasesExecutor")
	public Long call() {
		List<ICase> allRunningCases = Ivy
				.wf()
				.getCaseQueryExecutor()
				.getResults(
						CaseQuery.create().where().state()
								.isEqual(CaseState.RUNNING));

		allRunningCases.stream().forEach(
				c -> {
					c.destroy();
					ICase fetch = Ivy.wf().findCase(c.getId());
					progressMonitor.update(MessageFormat.format(
							"{0} is {1}", fetch, fetch.getState()));
				});

		return (long) allRunningCases.size();
	}

}
