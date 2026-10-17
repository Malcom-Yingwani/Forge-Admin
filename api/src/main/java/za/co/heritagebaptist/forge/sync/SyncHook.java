package za.co.heritagebaptist.forge.sync;
import java.util.UUID;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.media.MediaAsset;
public interface SyncHook {
    default void onChanged(BaseEntity entity) {}
    default void onDeleted(String type,UUID id) {}
    default void onMediaStored(MediaAsset asset) {}
}
