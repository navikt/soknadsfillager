package no.nav.soknad.arkivering.soknadsfillager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FilRepository : JpaRepository<FilDbData, String>
