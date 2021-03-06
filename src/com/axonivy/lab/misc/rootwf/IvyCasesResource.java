package com.axonivy.lab.misc.rootwf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.CaseState;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

import com.axonivy.lab.jaxrs.IvyStreamingOutput;
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
	public String getCases(
			@QueryParam("running") @DefaultValue("false") boolean runningCase) {
		if (runningCase) {
			long runningCasesCount = Ivy
					.wf()
					.getCaseQueryExecutor()
					.getCount(
							CaseQuery.create().where().state()
									.isEqual(CaseState.RUNNING));
			return MessageFormat.format("There are {0} RUNNING cases",
					runningCasesCount);
		}
		long existingCasesCount = Ivy.wf().getCaseQueryExecutor()
				.getCount(CaseQuery.create().where().caseId().isNotNull());
		return MessageFormat.format("There are {0} existing cases",
				existingCasesCount);
	}

	@POST
	@Path("{case-id}/destroy")
	public String destroyRunningCase(@PathParam("case-id") long caseId) {
		Optional<ICase> ivyCase = Optional
				.ofNullable(Ivy.wf().findCase(caseId));
		ivyCase.ifPresent(c -> c.destroy());
		return MessageFormat.format("Case {0} has been destroyed", caseId);
	}

	@POST
	@Path("/destroy")
	public Response destroyAllCases() throws Exception {
		StreamingOutput response = new IvyStreamingOutput(
				new StreamingOutput() {

					@Override
					public void write(OutputStream out) throws IOException,
							WebApplicationException {
						ProgressMonitor progress = new AutoFlushingProgressStream(
								out);
						destroyAllCasesCommand.setProgressMonitor(progress);
						long allDestroyedCasesCount = destroyAllCasesCommand
								.call();
						progress.update(MessageFormat.format(
								"{0} cases has been destroyed",
								allDestroyedCasesCount));
					}
				});
		return Response.ok(response).build();
	}

	@DELETE
	public Response deleteAllCases() throws Exception {
		Ivy.log().error("HHH - The user {0} is DELETING ALL CASES",
				Ivy.session().getSessionUserName());
		return Response.ok().entity(
				new IvyStreamingOutput(new StreamingOutput() {
					@Override
					public void write(OutputStream out) throws IOException,
							WebApplicationException {
						ProgressMonitor progress = new AutoFlushingProgressStream(out);
						deleteAllCommand.setProgressMonitor(progress);
						int casesDeletedCount = deleteAllCommand.call();
						progress.update("{0} cases are deleted.", casesDeletedCount);
					}
				})).build();
	}

	private static class AutoFlushingProgressStream implements
			ProgressMonitor {

		private final PrintWriter progress;

		public AutoFlushingProgressStream(OutputStream out) {
			this.progress = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(out, Charset.forName("UTF-8"))),
					true);
		}

		@Override
		public void update(String progress, Object... formatingValues) {
			this.progress.println(MessageFormat.format(progress,
					formatingValues));
		}

	}
}
