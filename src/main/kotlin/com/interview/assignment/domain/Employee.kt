package com.interview.assignment.domain

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "employee")
class Employee : AbstractAuditingEntity(), Serializable {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Size(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    var firstname: String? = null

    @Size(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    var lastname: String? = null

    @Column(name = "join_date", nullable = false)
    var joinDate: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "resign_date")
    var resignDate: ZonedDateTime? = null
}