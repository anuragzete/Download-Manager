package com.multithreaded.downloader;

/**
 * Utility class to handle common file-related operations.
 * <p>
 * This class provides methods to infer file extensions based on HTTP content types.
 * It uses a switch statement to map common MIME types to their corresponding file extensions.
 * </p>
 */
public class FileUtils {

    /**
     * Infers the file extension from the given HTTP content type.
     * <p>
     * If the content type is not recognized, it defaults to "bin" (binary).
     * </p>
     *
     * @param contentType The HTTP content type (e.g., "image/jpeg", "application/pdf")
     * @return The inferred file extension (e.g., "jpg", "pdf", "mp4") or "bin" if unknown
     */
    public static String getExtensionFromContentType(String contentType) {
        if (contentType == null) return null;

        switch (contentType) {
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "image/gif": return "gif";
            case "image/webp": return "webp";
            case "image/bmp": return "bmp";
            case "application/pdf": return "pdf";
            case "text/html": return "html";
            case "text/plain": return "txt";
            case "application/zip": return "zip";
            case "audio/mpeg": return "mp3";
            case "video/mp4": return "mp4";
            case "application/json": return "json";
            default: return "bin";
        }
    }
}
