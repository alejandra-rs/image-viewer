package software.ulpgc.imageviewer.architecture.io;

import java.util.stream.Stream;

public interface ImageStore {
    Stream<String> images();
}
