package za.co.heritagebaptist.forge.content.growthgroup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import za.co.heritagebaptist.forge.common.BaseEntity;

@Entity
@Table(name = "growth_group")
public class GrowthGroup extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "area")
    private String area;

    @Column(name = "host")
    private String host;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "contact", columnDefinition = "text")
    private String contact;

    @Column(name = "contact_public", nullable = false)
    private boolean contactPublic;

    @Column(name = "active", nullable = false)
    private boolean active;

    public GrowthGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isContactPublic() {
        return contactPublic;
    }

    public void setContactPublic(boolean contactPublic) {
        this.contactPublic = contactPublic;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
