/**
 * Data structure wrapping the result of a Retrieval-Augmented Generation query including retrieved document context.
 */
package dz.edu.univconstantine2.ntic.als.service.ai.legacy;

import java.util.List;










@Deprecated(since = "2.0", forRemoval = false)
public record RAGResult(
        List<String> contextChunks,
        boolean isOutOfContext
) {
}
