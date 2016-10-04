package com.axonivy.lab.jaxrs;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.request.IProcessModelVersionRequest;
import ch.ivyteam.ivy.request.RequestFactory;
import ch.ivyteam.ivy.request.metadata.MetaData;
import ch.ivyteam.ivy.security.ISession;

public class IvyStreamingOutput implements StreamingOutput {

	private final IProcessModelVersion pmv;
	private final ISession session;
	private final StreamingOutput otherStreamingOutput;

	public IvyStreamingOutput(StreamingOutput another) {
		this.pmv = MetaData.getProcessModelVersion();
		this.session = MetaData.getSession();
		this.otherStreamingOutput = another;
	}

	@Override
	public final void write(OutputStream stream) throws IOException,
			WebApplicationException {
		IProcessModelVersionRequest streamRequest = RequestFactory
				.createRestRequest(pmv, session);
		MetaData.pushRequest(streamRequest);
		try {
			otherStreamingOutput.write(stream);
		} finally {
			MetaData.popRequest();
		}
	}

}