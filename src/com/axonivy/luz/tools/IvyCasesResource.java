package com.axonivy.luz.tools;

import java.text.MessageFormat;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.CaseState;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

import com.axonivy.shaded.google.inject.Inject;

@Path("{applicationName}/root/cases")
public class IvyCasesResource {

	@Inject
	private DeleteAllExistingCasesCommand deleteAllCommand;
	
	@Inject
	private DestroyAllCasesCommand destroyAllCasesCommand;
	
	
	@PostConstruct
	public void injectMe() {
		Instances.injectInto(this);
	}
	
	
	@GET
	public String getCases(@QueryParam("running") @DefaultValue("false") boolean runningCase) {
		if (runningCase) {
			long runningCasesCount = Ivy.wf().getCaseQueryExecutor().getCount(CaseQuery.create().where().state().isEqual(CaseState.RUNNING));
			return MessageFormat.format("There are {0} RUNNING cases", runningCasesCount);
		}
		long existingCasesCount = Ivy.wf().getCaseQueryExecutor().getCount(CaseQuery.create().where().caseId().isNotNull());
		return MessageFormat.format("There are {0} existing cases", existingCasesCount);
	}

	@POST
	@Path("{case-id}/destroy")
	public String destroyRunningCase(@PathParam("case-id") long caseId) {
		Optional<ICase> ivyCase = Optional.ofNullable(Ivy.wf().findCase(caseId));
		ivyCase.ifPresent(c -> c.destroy());
		return MessageFormat.format("Case {0} has been destroyed", caseId);
	}
	
	@POST
	@Path("/destroy")
	public String destroyAllCases() throws Exception {
		long allDestroyedCases = destroyAllCasesCommand.call();
		return MessageFormat.format("{0} cases has been destroyed", allDestroyedCases);
	}
	
	
	@DELETE
	public String deleteAllCases() throws Exception {
		Ivy.log().error("HHH - The user {0} is DELETING ALL CASES", Ivy.session().getSessionUserName());
		int casesDeletedCount = deleteAllCommand.call();
		return MessageFormat.format("{0} cases has been deleted", casesDeletedCount);
	}
}
