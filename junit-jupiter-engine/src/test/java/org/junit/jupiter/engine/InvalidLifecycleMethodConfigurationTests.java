/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.jupiter.engine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.test.event.ExecutionEventRecorder;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

/**
 * Integration tests that very proper handling of invalid configuration for
 * lifecycle methods in conjunction with the {@link JupiterTestEngine}.
 *
 * <p>In general, configuration errors should not be thrown until the
 * execution phase, thereby giving all containers a chance to execute.
 *
 * @since 5.0
 */
class InvalidLifecycleMethodConfigurationTests extends AbstractJupiterTestEngineTests {

	@Test
	void executeValidTestCaseAlongsideTestCaseWithInvalidBeforeAllDeclaration() {
		assertExecutionResults(TestCaseWithInvalidBeforeAllMethod.class);
	}

	@Test
	void executeValidTestCaseAlongsideTestCaseWithInvalidAfterAllDeclaration() {
		assertExecutionResults(TestCaseWithInvalidAfterAllMethod.class);
	}

	@Test
	void executeValidTestCaseAlongsideTestCaseWithInvalidBeforeEachDeclaration() {
		assertExecutionResults(TestCaseWithInvalidBeforeEachMethod.class);
	}

	@Test
	void executeValidTestCaseAlongsideTestCaseWithInvalidAfterEachDeclaration() {
		assertExecutionResults(TestCaseWithInvalidAfterEachMethod.class);
	}

	private void assertExecutionResults(Class<?> invalidTestClass) {
		LauncherDiscoveryRequest request = request().selectors(selectClass(TestCase.class),
			selectClass(invalidTestClass)).build();

		ExecutionEventRecorder eventRecorder = executeTests(request);

		// @formatter:off
		assertAll(
			() -> assertEquals(3, eventRecorder.getContainerStartedCount(), "# containers started"),
			() -> assertEquals(1, eventRecorder.getTestStartedCount(), "# tests started"),
			() -> assertEquals(1, eventRecorder.getTestSuccessfulCount(), "# tests succeeded"),
			() -> assertEquals(0, eventRecorder.getTestFailedCount(), "# tests failed"),
			() -> assertEquals(3, eventRecorder.getContainerFinishedCount(), "# containers finished"),
			() -> assertEquals(1, eventRecorder.getContainerFailedCount(), "# containers failed")
		);
		// @formatter:on
	}

	// -------------------------------------------------------------------------

	private static class TestCase {

		@Test
		void test() {
		}
	}

	private static class TestCaseWithInvalidBeforeAllMethod {

		// must be static
		@BeforeAll
		void beforeAll() {
		}

		@Test
		void test() {
		}
	}

	private static class TestCaseWithInvalidAfterAllMethod {

		// must be static
		@AfterAll
		void afterAll() {
		}

		@Test
		void test() {
		}
	}

	private static class TestCaseWithInvalidBeforeEachMethod {

		// must NOT be static
		@BeforeEach
		static void beforeEach() {
		}

		@Test
		void test() {
		}
	}

	private static class TestCaseWithInvalidAfterEachMethod {

		// must NOT be static
		@AfterEach
		static void afterEach() {
		}

		@Test
		void test() {
		}
	}

}
