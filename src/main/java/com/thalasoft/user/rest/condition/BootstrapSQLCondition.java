package com.thalasoft.user.rest.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BootstrapSQLCondition implements Condition {

    private static final String BOOTSTRAP_SQL = "bootstrapsql";

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return context.getEnvironment().getProperty(BOOTSTRAP_SQL) != null && context.getEnvironment().getProperty(BOOTSTRAP_SQL).equals("true");
	}

}