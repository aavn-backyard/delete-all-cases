package com.axonivy.luz.tools;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.CaseState;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

public class DeleteAllExistingCasesCommand implements Callable<Integer> {

	@Override
	@RequiresRole("DeleteAllCasesExecutor")
	public Integer call() throws Exception {
		List<ICase> allCases = Ivy.wf().getCaseQueryExecutor().getResults(CaseQuery.create().where().caseId().isNotNull());
		allCases.stream().forEach(c -> {
			if (!EnumSet.of(CaseState.DESTROYED, CaseState.DONE).contains(c.getState())) {
				c.destroy();
			}
			Ivy.wf().deleteCompletedCase(c);
		});
		
		return allCases.size();
	}
}
