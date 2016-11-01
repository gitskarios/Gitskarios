package com.alorma.github.injector.named;

import javax.inject.Named;
import javax.inject.Qualifier;

@Qualifier @Named(value = "ComputationScheduler") public @interface ComputationScheduler {
}
