package com.project.bokduck.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(name="series", length = 64)
    private String series;

    @Column(name="username", length = 64, nullable = false)
    private String username;

    @Column(name="token", length = 64, nullable = false)
    private String token;

    @Column(name="last_used", nullable = false)
    private LocalDateTime lastUsed;

}