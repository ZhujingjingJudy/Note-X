package com.example.notex.plant;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtils {

    public static String getPathFromUri(final Context context, final Uri uri) {
        File file = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success = false;
        try {
            String extension = getImageExtension(uri);
            inputStream = context.getContentResolver().openInputStream(uri);
            file = File.createTempFile("image_picker", extension, context.getCacheDir());
            file.deleteOnExit();
            outputStream = new FileOutputStream(file);
            if (inputStream != null) {
                copy(inputStream, outputStream);
                success = true;
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignored) {
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false;
            }
        }
        return success ? file.getPath() : null;
    }

    /** @return extension of image with dot, or default .jpg if it none. */
    private static String getImageExtension(Uri uriImage) {
        String extension = null;

        try {
            String imagePath = uriImage.getPath();
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            extension = null;
        }

        if (extension == null || extension.isEmpty()) {
            //default extension for matches the previous behavior of the plugin
            extension = "jpg";
        }

        return "." + extension;
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }
}
