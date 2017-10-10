package com.tstv.infofrom.di.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by tstv on 10.10.2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface TemperatureScope {
}
