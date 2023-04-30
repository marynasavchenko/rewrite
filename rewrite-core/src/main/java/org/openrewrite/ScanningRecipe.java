/*
 * Copyright 2023 the original author or authors.
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
package org.openrewrite;

import org.openrewrite.internal.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * A recipe that may first scan a repository and study it in one pass over the
 * repository's source files before, in another pass, it decides how to transform
 * the code.
 * <br/>
 * New source file generation is part of this type as well, since in almost every case
 * we check that a file doesn't yet exist (and perhaps other conditions) before deciding
 * to generate a file.
 *
 * @param <T> The type of the accumulator where scanning data is held until the transformation phase.
 */
public abstract class ScanningRecipe<T> extends Recipe {
    private final String recipeAccMessage = "org.openrewrite.recipe.acc." + UUID.randomUUID();

    /**
     * @return The initial value of the accumulator before any source files have been iterated over.
     */
    public abstract T getInitialValue();

    /**
     * A visitor that is called for each source file in the repository in an initial pass.
     * Scanning data should be accumulated to <code>acc</code>. The first source file to visit
     * will receive an <code>acc</code> value that is supplied by {@link #getInitialValue()}.
     * <br/>
     * Any changes that the scanning visitor makes to the source file will be discarded.
     *
     * @param acc The accumulated scanning data.
     * @return A visitor that is called to collect scanning data on each source file.
     */
    public abstract TreeVisitor<?, ExecutionContext> getScanner(T acc);

    /**
     * Generate new source files to add to the repository using information collected from scanning.
     *
     * @param acc The accumulated scanning data.
     * @return A list of new source files.
     */
    public Collection<SourceFile> generate(T acc, ExecutionContext ctx) {
        return Collections.emptyList();
    }

    /**
     * A visitor that is called in a second pass to perform transformation on each source file.
     * To delete a source file, return <code>null</code> from the {@link TreeVisitor#visit(Tree, Object)}
     * method.
     *
     * @param acc The accumulated scanning data.
     * @return A visitor that is called to perform transformation on each source file.
     */
    public TreeVisitor<?, ExecutionContext> getVisitor(T acc) {
        return TreeVisitor.noop();
    }

    T getAccumulator(Cursor cursor) {
        return cursor.getRoot().computeMessageIfAbsent(recipeAccMessage, m -> getInitialValue());
    }

    @Override
    public final TreeVisitor<?, ExecutionContext> getVisitor() {
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Override
            public @Nullable Tree visit(@Nullable Tree tree, ExecutionContext ctx) {
                return getVisitor(getAccumulator(getCursor())).visit(tree, ctx);
            }
        };
    }
}
