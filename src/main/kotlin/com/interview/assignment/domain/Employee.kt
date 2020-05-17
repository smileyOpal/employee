package com.interview.assignment.domain

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "employee")
data class Employee(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long?,
                    @Size(max = 50)
                    @Column(name = "first_name", length = 50, nullable = false)
                    var firstname: String?,
                    @Size(max = 50)
                    @Column(name = "last_name", length = 50, nullable = false)
                    var lastname: String?,
                    @Column(name = "join_date", nullable = false)
                    var joinDate: ZonedDateTime?,
                    @Column(name = "resign_date")
                    var resignDate: ZonedDateTime?) : AbstractAuditingEntity(), Serializable {
    constructor() : this(null, null, null, null, null)
}