package com.interview.assignment.domain

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "employee")
class Employee : AbstractAuditingEntity(), Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employee

        if (id != other.id) return false
        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (joinDate != other.joinDate) return false
        if (resignDate != other.resignDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (firstname?.hashCode() ?: 0)
        result = 31 * result + (lastname?.hashCode() ?: 0)
        result = 31 * result + joinDate.hashCode()
        result = 31 * result + (resignDate?.hashCode() ?: 0)
        return result
    }


}