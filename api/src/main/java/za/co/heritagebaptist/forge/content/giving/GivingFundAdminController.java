package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/giving-funds")
public class GivingFundAdminController {
    private final GivingFundService service;
    public GivingFundAdminController(GivingFundService service) { this.service=service; }
    @GetMapping public PageResponse<GivingFundAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="name,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public GivingFundAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<GivingFundAdminDto> create(@Valid @RequestBody GivingFundRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/giving-funds/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public GivingFundAdminDto update(@PathVariable UUID id,@Valid @RequestBody GivingFundRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public GivingFundAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public GivingFundAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
}
