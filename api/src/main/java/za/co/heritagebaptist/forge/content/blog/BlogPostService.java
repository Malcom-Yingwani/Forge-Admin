package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.blog.BlogPost;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class BlogPostService extends ContentService<BlogPost,BlogPostRequest,BlogPostAdminDto,BlogPostPublicDto> {
    public BlogPostService(ContentSupport support,BlogPostRepository repository,BlogPostMapper mapper) { super(support,repository,mapper,BlogPost.class,"blog-post",Set.of("id","createdAt","updatedAt","title","slug","excerpt","body","publishedAt","published"),List.of("title","slug")); }
    protected BlogPost fresh() { return new BlogPost(); }
    protected Long version(BlogPostRequest request) { return request.version(); }
    @Override protected void setPublished(BlogPost e,boolean published) { e.setPublished(published); }
}
