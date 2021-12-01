package com.NetCracker.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SERVICE_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "SERVICE_NAME", nullable = false)
    @NotNull
    private String serviceName;

    public Service() {
    }

    public Service(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return getId().equals(service.getId()) && getServiceName().equals(service.getServiceName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getServiceName());
    }
}
