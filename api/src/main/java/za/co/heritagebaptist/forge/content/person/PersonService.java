package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.person.Person;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class PersonService extends ContentService<Person,PersonRequest,PersonAdminDto,PersonPublicDto> {
    public PersonService(ContentSupport support,PersonRepository repository,PersonMapper mapper) { super(support,repository,mapper,Person.class,"person",Set.of("id","createdAt","updatedAt","name","category","office","roleTitle","bio","publicEmail","consentAt","sortOrder","published"),List.of("name")); }
    protected Person fresh() { return new Person(); }
    protected Long version(PersonRequest request) { return request.version(); }
    @Override protected void setPublished(Person e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(Person e,int order) { e.setSortOrder(order); }
}
