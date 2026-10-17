package za.co.heritagebaptist.forge.media;
import java.io.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;
class LocalDiskStorageTests {
    @TempDir Path directory;
    @Test void streamsFilesAndRejectsPathsOutsideStorage() throws Exception {
        var storage=new LocalDiskStorage(directory);byte[] bytes="example".getBytes();storage.put("2026/10/example.pdf",new ByteArrayInputStream(bytes),bytes.length,"application/pdf");
        try(var in=storage.get("2026/10/example.pdf")) { assertThat(in.readAllBytes()).isEqualTo(bytes); }
        assertThatThrownBy(() -> storage.get("../outside")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> storage.put("/outside",InputStream.nullInputStream(),0,"application/pdf")).isInstanceOf(IllegalArgumentException.class);
        storage.delete("2026/10/example.pdf");assertThat(Files.exists(directory.resolve("2026/10/example.pdf"))).isFalse();
    }
}
