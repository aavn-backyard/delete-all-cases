package com.axonivy.lab.misc.rootwf;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;



@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
	String value();
}
