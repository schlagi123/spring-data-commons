/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.spel;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Unit tests for {@link ExpressionDependencies}.
 *
 * @author Mark Paluch
 */
class ExpressionDependenciesUnitTests {

	SpelExpressionParser PARSER = new SpelExpressionParser();

	@Test
	void shouldExtractDependencies() {

		String expression = "hasRole('ROLE_ADMIN') ? '%' : principal.emailAddress";

		SpelExpression spelExpression = (SpelExpression) PARSER.parseExpression(expression);
		SpelNode ast = spelExpression.getAST();

		ExpressionDependencies dependencies = ExpressionDependencies.discover(ast, true);

		assertThat(dependencies).extracting(ExpressionDependencies.ExpressionDependency::getSymbol).containsOnly("hasRole",
				"principal");
	}

	@Test
	void shouldExtractDependenciesFromMethodCallArgs() {

		String expression = "hasRole(principal.emailAddress)";

		SpelExpression spelExpression = (SpelExpression) PARSER.parseExpression(expression);
		SpelNode ast = spelExpression.getAST();

		ExpressionDependencies dependencies = ExpressionDependencies.discover(ast, true);

		assertThat(dependencies).extracting(ExpressionDependencies.ExpressionDependency::getSymbol).containsOnly("hasRole",
				"principal");
	}

	@Test
	void shouldExtractFirstMethodAsDependency() {

		String expression = "hello().hasRole(principal.emailAddress, principal.somethingElse).somethingElse()";

		SpelExpression spelExpression = (SpelExpression) PARSER.parseExpression(expression);
		SpelNode ast = spelExpression.getAST();

		ExpressionDependencies dependencies = ExpressionDependencies.discover(ast, true);

		assertThat(dependencies).extracting(ExpressionDependencies.ExpressionDependency::getSymbol).containsOnly("hello",
				"principal");
	}

}