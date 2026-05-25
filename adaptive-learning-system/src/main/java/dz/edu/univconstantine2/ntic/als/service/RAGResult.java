/**
 * Data structure wrapping the result of a Retrieval-Augmented Generation query including retrieved document context.
 */
package dz.edu.univconstantine2.ntic.als.service;

import java.util.List;

public record RAGResult(
        List<String> contextChunks,
        boolean isOutOfContext
) {
}
