package com.axonivy.lab.misc.rootwf;

import com.axonivy.shaded.google.inject.Binder;
import com.axonivy.shaded.google.inject.Guice;
import com.axonivy.shaded.google.inject.Injector;
import com.axonivy.shaded.google.inject.Module;
import com.axonivy.shaded.google.inject.matcher.Matchers;

public class Instances {

	private static final ProgressMonitor EMPTY_PROGRESS_MONITOR = new ProgressMonitor() {
		@Override
		public void update(String progress, Object... formatingValues) {
		}
	};
	
	private static final Injector injector;
	
	static {
		injector = Guice.createInjector(new Module() {
			
			@Override
			public void configure(Binder binder) {
				binder.bind(ProgressMonitor.class)
					.toInstance(EMPTY_PROGRESS_MONITOR);
				binder.bindInterceptor(Matchers.any(),
						Matchers.annotatedWith(RequiresRole.class),
						new RoleCheckInterceptor());
			}
		});
	}
	
	
	public static <T> T of(Class<T> instanceClass) {
		return injector.getInstance(instanceClass);
	}
	
	public static void injectInto(Object object) {
		injector.injectMembers(object);
	}
	
}
