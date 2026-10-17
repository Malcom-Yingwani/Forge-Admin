package za.co.heritagebaptist.forge.media;
import java.io.*;
import java.nio.file.*;
public class LocalDiskStorage implements StorageService {
    private final Path root;
    public LocalDiskStorage(Path root) throws IOException { this.root=root.toAbsolutePath().normalize();Files.createDirectories(this.root); }
    private Path resolve(String key) {
        if(key==null||key.isBlank()||key.indexOf('\\')>=0||Path.of(key).isAbsolute()) throw new IllegalArgumentException("Invalid storage key");
        Path result=root.resolve(key).normalize();if(!result.startsWith(root)||result.equals(root)) throw new IllegalArgumentException("Invalid storage key");return result;
    }
    public void put(String key,InputStream input,long size,String contentType) throws IOException {
        Path target=resolve(key);Files.createDirectories(target.getParent());Path staging=Files.createTempFile(target.getParent(),".upload-",".tmp");
        try {
            long copied=Files.copy(input,staging,StandardCopyOption.REPLACE_EXISTING);if(copied!=size) throw new IOException("Incomplete storage upload");
            try { Files.move(staging,target,StandardCopyOption.ATOMIC_MOVE); } catch(AtomicMoveNotSupportedException exception) { Files.move(staging,target); }
        } finally { Files.deleteIfExists(staging); }
    }
    public InputStream get(String key) throws IOException { return Files.newInputStream(resolve(key)); }
    public void delete(String key) throws IOException { Files.deleteIfExists(resolve(key)); }
}
