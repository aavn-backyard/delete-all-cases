package com.axonivy.lab.misc.rootwf;

import static ch.ivyteam.ivy.workflow.CaseState.DESTROYED;
import static ch.ivyteam.ivy.workflow.CaseState.DONE;


import java.util.*;
import java.util.concurrent.Callable;


import javax.inject.Inject;


import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

public class DeleteAllExistingCasesCommand implements Callable<Integer> {

	private ProgressMonitor progress;
	
	@Inject
	public DeleteAllExistingCasesCommand(ProgressMonitor progressMonitor) {
		this.progress = progressMonitor;
	}
	
	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progress = progressMonitor;
	}

	@Override
	@RequiresRole("DeleteAllCasesExecutor")
	public Integer call() {
		List<ICase> allCases = Ivy.wf().getCaseQueryExecutor()
				.getResults(CaseQuery.create().where().caseId().isNotNull());

		allCases.stream().forEach(ivyCase -> {
			if (!EnumSet.of(DESTROYED, DONE).contains(ivyCase.getState())) {
				ivyCase.destroy();
				// refresh the instance of ivyCase
				ivyCase = Ivy.wf().findCase(ivyCase.getId());
				progress.update("{0} is {1}", ivyCase, ivyCase.getState());
			}
			Optional.of(ivyCase)
				.filter(i -> EnumSet.of(DESTROYED, DONE).contains(i.getState()))
				.ifPresent(i -> Ivy.wf().deleteCompletedCase(i));
			progress.update("{0} is deleted permanently", ivyCase);
		});

		return allCases.size();
	}
}
