/**
 * Spring Application Event representing the occurrence of a File Indexing Request activity.
 */
package dz.edu.univconstantine2.ntic.als.event;

public record FileIndexingRequestEvent(
        String moduleId,
        String courseId,
        String filePath
) {
}
