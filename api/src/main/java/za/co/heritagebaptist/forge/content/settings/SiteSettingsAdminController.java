package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
@RestController
@RequestMapping("/api/admin/settings")
public class SiteSettingsAdminController {
    private final SiteSettingsService service;
    public SiteSettingsAdminController(SiteSettingsService service) { this.service=service; }
    @GetMapping public SiteSettingsAdminDto get() { return service.singleton(); }
    @PutMapping public SiteSettingsAdminDto update(@Valid @RequestBody SiteSettingsRequest request) { return service.update(service.singleton().id(),request); }
}
