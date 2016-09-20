package com.axonivy.luz.tools;

import java.util.List;
import java.util.concurrent.Callable;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.CaseState;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

public class DestroyAllCasesCommand implements Callable<Long> {

	@Override
	@RequiresRole("DeleteAllCasesExecutor")
	public Long call() {
		List<ICase> allRunningCases = Ivy.wf().getCaseQueryExecutor()
				.getResults(CaseQuery.create().where().state().isEqual(CaseState.RUNNING));
		allRunningCases.stream().forEach(c -> c.destroy());
		return (long) allRunningCases.size();
	}

}
