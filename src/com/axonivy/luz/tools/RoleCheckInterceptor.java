package com.axonivy.luz.tools;

import java.util.Optional;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import ch.ivyteam.ivy.environment.Ivy;

public class RoleCheckInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		
		Optional<RequiresRole> requiresRole = Optional.ofNullable(methodInvocation.getMethod().getAnnotation(RequiresRole.class));
		
		if (requiresRole.isPresent()) {
			if (Ivy.session().getSessionUser().getRoles()
				.stream()
					.anyMatch(r -> r.getName().equals(requiresRole.get().value()))) {
				return methodInvocation.proceed();
			} else {
				throw new NotAuthorizedException("User must have role " + requiresRole.get().value(), Response.status(Status.UNAUTHORIZED));
			}
		}
		
		return methodInvocation.proceed();
		
	}

}
