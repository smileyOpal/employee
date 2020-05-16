package com.interview.assignment.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.envers.Audited
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractAuditingEntity {

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    @JsonIgnore
    lateinit var createdBy: String

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    @JsonIgnore
    var createdDate = ZonedDateTime.now()

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false, length = 50)
    @JsonIgnore
    lateinit var lastModifiedBy: String

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @JsonIgnore
    var lastModifiedDate = ZonedDateTime.now()

}