/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.maven

import org.junit.jupiter.api.Test

class ChangePackagingTest: MavenRecipeTest {

    @Test
    fun addPackaging() = assertChanged(
        recipe = ChangePackaging("*", "*", "pom"),
        before = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>
        """,
        after = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <packaging>pom</packaging>
            </project>
        """
    )

    @Test
    fun removePackaging() = assertChanged(
        recipe = ChangePackaging("*", "*", null),
        before = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <packaging>pom</packaging>
            </project>
        """,
        after = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>
        """
    )

    @Test
    fun changePackaging() = assertChanged(
        recipe = ChangePackaging("*", "*", "pom"),
        before = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <packaging>jar</packaging>
            </project>
        """,
        after = """
            <project>
                <groupId>org.example</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <packaging>pom</packaging>
            </project>
        """
    )
}
