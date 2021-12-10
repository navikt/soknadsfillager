package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.api.FilesApi
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerService
import no.nav.soknad.arkivering.soknadsfillager.service.LagreFilerService
import no.nav.soknad.arkivering.soknadsfillager.service.SjekkFilerService
import no.nav.soknad.arkivering.soknadsfillager.service.SlettFilerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RestController
class RestApi(
	private val hentFilerService: HentFilerService,
	private val sjekkFilerService: SjekkFilerService,
	private val lagreFilerService: LagreFilerService,
	private val slettFilerService: SlettFilerService
) : FilesApi {

	/**
	 * @see FilesApi#addFile
	 */
	override fun addFile(fileData: List<FileData>): ResponseEntity<Unit> {
		lagreFilerService.lagreFiler(fileData.map { FilElementDto(it.id, it.content, it.createdAt.toLocalDateTime()) })
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#checkFilesByIds
	 */
	override fun checkFilesByIds(ids: List<String>): ResponseEntity<Unit> {
		sjekkFilerService.sjekkFiler(ids)
		return ResponseEntity(HttpStatus.NOT_IMPLEMENTED) // TODO
	}


	/**
	 * @see FilesApi#deleteFiles
	 */
	override fun deleteFiles(ids: List<String>): ResponseEntity<Unit> {
		slettFilerService.slettFiler(ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#findFilesByIds
	 */
	override fun findFilesByIds(ids: List<String>): ResponseEntity<List<FileData>> {
		val files = hentFilerService.hentFiler(ids).map { FileData(it.uuid, it.fil!!,
			it.opprettet?.atOffset(ZoneOffset.UTC) ?: OffsetDateTime.now()) }

		return ResponseEntity(HttpStatus.NOT_IMPLEMENTED) // TODO
	}
}
