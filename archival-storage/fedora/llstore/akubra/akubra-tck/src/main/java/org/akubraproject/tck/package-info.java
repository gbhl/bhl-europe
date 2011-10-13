/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2009-2010 DuraSpace
 * http://duraspace.org
 *
 * In collaboration with Topaz Inc.
 * http://www.topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This package provides a basic TCK. This is a test suite aiming at providing a fairly complete
 * set of tests for the core Akubra functionality each store implementation must provide in order
 * to ensure that the Akubra API's are properly, consistently, and fully implemented.
 *
 * <p>This test suite, however, is not meant to be a complete set of tests for any given store, as
 * stores on the one hand usually have additional semantics which need verifying, and on the other
 * hand are likely to have code paths that are not fully exercised by this test suite.
 *
 * <p>To use these tests {@link org.akubraproject.tck.TCKTestSuite} must be subclassed and
 * the result run with testng.
 */
package org.akubraproject.tck;
