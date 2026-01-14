package uz.vv.templatex.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.servlet.http.HttpServletRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ResponseVO<T>(
    val status: Int,
    val errors: Map<String, String>? = null,
    val data: T? = null,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
    val source: String? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val source = ex.stackTrace.firstOrNull()?.let {
            "${it.className}.${it.methodName}(${it.lineNumber})"
        }
        val response = ResponseVO<Void>(
            status = 400,
            errors = ex.fieldErrors ?: mapOf("error" to ex.errorCode.name),
            data = null,
            source = source
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val response = ResponseVO<Void>(
            status = 400,
            errors = errors,
            data = null,
            source = request?.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val response = ResponseVO<Void>(
            status = 405,
            errors = mapOf("error" to (ex.message ?: "Method not supported")),
            data = null,
            source = request?.requestURI
        )
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response)
    }

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val response = ResponseVO<Void>(
            status = 400,
            errors = mapOf("error" to (ex.message ?: "Required request part is missing")),
            data = null,
            source = request?.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val response = ResponseVO<Void>(
            status = 400,
            errors = mapOf("error" to (ex.message ?: "Required request parameter is missing")),
            data = null,
            source = request?.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest?
    ): ResponseEntity<ResponseVO<Void>> {
        val source = ex.stackTrace.firstOrNull()?.let {
            "${it.className}.${it.methodName}(${it.lineNumber})"
        }
        val response = ResponseVO<Void>(
            status = 500,
            errors = mapOf("unexpectedError" to (ex.message ?: "Unknown error")),
            data = null,
            source = source
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}