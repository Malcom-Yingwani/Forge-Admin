package za.co.heritagebaptist.forge.media;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.imageio.*;
import org.springframework.stereotype.Component;
@Component
public class ImageVariants {
    public void create(Path file,MediaAsset asset,StorageService storage,java.util.List<String> stored) throws IOException {
        try(var input=ImageIO.createImageInputStream(file.toFile())) {
            var readers=ImageIO.getImageReaders(input);if(!readers.hasNext()) throw new MediaExceptionHandler.Rejected(415,"The image could not be read");
            var reader=readers.next();
            try {
                reader.setInput(input,true,true);int width=reader.getWidth(0),height=reader.getHeight(0);
                if(width<=0||height<=0||width>20000||height>20000||(long)width*height>20000000) throw new MediaExceptionHandler.Rejected(413,"Image dimensions exceed the supported limit");
                asset.setWidth(width);asset.setHeight(height);BufferedImage image=reader.read(0);Map<String,Object> variants=new LinkedHashMap<>();
                for(int target:new int[]{320,960}) {
                    int w=Math.min(width,target),h=Math.max(1,(int)Math.round((double)height*w/width));
                    BufferedImage scaled=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);Graphics2D graphics=scaled.createGraphics();
                    try { graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);graphics.drawImage(image,0,0,w,h,null); } finally { graphics.dispose(); }
                    Path output=Files.createTempFile("forge-variant-",".webp");String key=asset.getStorageKey()+"-"+target+".webp";
                    try {
                        if(!ImageIO.write(scaled,"webp",output.toFile())) throw new IllegalStateException("WebP encoder unavailable");
                        MessageDigest digest=sha256();long size=Files.size(output);stored.add(key);
                        try(var in=new DigestInputStream(Files.newInputStream(output),digest)) { storage.put(key,in,size,"image/webp"); }
                        variants.put(Integer.toString(target),Map.of("key",key,"width",w,"height",h,"contentType","image/webp","size",size,"sha256",HexFormat.of().formatHex(digest.digest())));
                    } finally { Files.deleteIfExists(output);scaled.flush(); }
                }
                image.flush();asset.setVariants(variants);
            } finally { reader.dispose(); }
        } catch(javax.imageio.IIOException exception) { throw new MediaExceptionHandler.Rejected(415,"The image could not be read"); }
    }
    static MessageDigest sha256() { try { return MessageDigest.getInstance("SHA-256"); } catch(NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); } }
}
