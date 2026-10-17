package za.co.heritagebaptist.forge.media;
import java.io.*;
public interface StorageService {
    void put(String key,InputStream input,long size,String contentType) throws IOException;
    InputStream get(String key) throws IOException;
    void delete(String key) throws IOException;
}
