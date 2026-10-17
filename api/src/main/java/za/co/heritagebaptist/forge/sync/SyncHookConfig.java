package za.co.heritagebaptist.forge.sync;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
@Configuration
public class SyncHookConfig {
    @Bean @ConditionalOnMissingBean(SyncHook.class) SyncHook noOpSyncHook() { return new SyncHook() {}; }
}
