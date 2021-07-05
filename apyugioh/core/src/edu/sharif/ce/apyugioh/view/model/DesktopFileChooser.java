package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserCallback;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserConfiguration;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserUtils;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.regex.Pattern;

public class DesktopFileChooser implements NativeFileChooser {
    @Override
    public void chooseFile(final NativeFileChooserConfiguration configuration, NativeFileChooserCallback callback) {

        NativeFileChooserUtils.checkNotNull(configuration, "configuration");
        NativeFileChooserUtils.checkNotNull(callback, "callback");

        // Create awt Dialog
        FileDialog fileDialog = configuration.title == null ? new FileDialog((Frame) null)
                : new FileDialog((Frame) null, configuration.title);

//                                FilenameFilter filter = null;
//
//                                // Add MIME type filter if any
//                                if (configuration.mimeFilter != null)
//                                    filter = createMimeTypeFilter(configuration.mimeFilter);
//
//                                // Add name filter if any
//                                if (configuration.nameFilter != null) {
//                                    if (filter == null) {
//                                        filter = configuration.nameFilter;
//                                    } else {
//                                        // Combine filters!
//                                        final FilenameFilter mime = filter;
//                                        filter = new FilenameFilter() {
//                                            @Override
//                                            public boolean accept(File dir, String name) {
//                                                return mime.accept(dir, name) && configuration.nameFilter.accept(dir, name);
//                                            }
//                                        };
//                                    }
//                                }

//                                if (filter != null)
        fileDialog.setFilenameFilter(configuration.nameFilter);

        // Set starting path if any
        if (configuration.directory != null)
            fileDialog.setDirectory(configuration.directory.file().getAbsolutePath());

        // Present it to the world
        fileDialog.setVisible(true);

        File[] files = fileDialog.getFiles();

        if (files == null || files.length == 0) {
            callback.onCancellation();
        } else {
            FileHandle result = null;
            File f = files[0];
            result = new FileHandle(f);
            callback.onFileChosen(result);
        }

    }

    public FilenameFilter createMimeTypeFilter(final String mimeType) {
        return new FilenameFilter() {

            Pattern mimePattern = Pattern.compile(mimeType.replaceAll("/", "\\\\/").replace("*", ".*"));

            @Override
            public boolean accept(File dir, String name) {

                // Getting a Mime type is not warranted (and may be slow!)
                try {

                    // Java6
                    FileNameMap map = URLConnection.getFileNameMap();
                    String path = new File(dir, name).getAbsolutePath();
                    String mime = map.getContentTypeFor(path);

                    // Java7
//                                            String mime = Files.probeContentType(new File(dir, name).toPath());

                    if (mime != null) {
                        // Try to get a match on Mime type
                        // That's quite faulty I know!
                        return mimePattern.matcher(mime).matches();
                    }

                } catch (Exception e) {
                }

                // Accept by default, in case mime probing doesn't work
                return true;
            }
        };
    }
}
