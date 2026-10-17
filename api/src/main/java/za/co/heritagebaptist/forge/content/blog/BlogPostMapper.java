package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.auth.AppUser;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.blog.BlogPost;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class BlogPostMapper implements ContentMapper<BlogPost,BlogPostRequest,BlogPostAdminDto,BlogPostPublicDto> {
    private final ContentSupport support;
    public BlogPostMapper(ContentSupport support) { this.support=support; }
    public void apply(BlogPost e,BlogPostRequest r) {
        e.setTitle(r.title());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(BlogPost.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setAuthor(support.reference(AppUser.class,r.authorId()));
        e.setExcerpt(r.excerpt());
        e.setBody(r.body());
        e.setCoverImage(support.reference(MediaAsset.class,r.coverImageId()));
        e.setTags(r.tags());
        e.setPublishedAt(r.publishedAt());
        e.setPublished(r.published());
    }
    public BlogPostAdminDto admin(BlogPost e) { return new BlogPostAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getTitle(), e.getSlug(), e.getAuthor() == null ? null : e.getAuthor().getId(), e.getExcerpt(), e.getBody(), e.getCoverImage() == null ? null : e.getCoverImage().getId(), e.getTags(), e.getPublishedAt(), e.isPublished()); }
    public BlogPostPublicDto publicDto(BlogPost e) { return new BlogPostPublicDto(e.getId(), e.getVersion(), e.getTitle(), e.getSlug(), e.getAuthor() == null ? null : e.getAuthor().getDisplayName(), e.getExcerpt(), e.getBody(), e.getCoverImage() == null ? null : e.getCoverImage().getStorageKey(), e.getTags(), e.getPublishedAt()); }
}
