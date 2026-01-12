package uz.vv.templatex.exception

import uz.vv.templatex.enum.ErrorCodes

sealed class BaseException(
    val errorCode: ErrorCodes,
    val fieldErrors: Map<String, String>? = null
) : RuntimeException()

class ValidationException(
    fieldErrors: Map<String, String>
) : BaseException(ErrorCodes.VALIDATION_ERROR, fieldErrors)

//  ORGANIZATION
class OrganizationNotFoundException(msg: String) : BaseException(ErrorCodes.ORGANIZATION_NOT_FOUND)
class OrganizationAlreadyExistsException(msg: String) : BaseException(ErrorCodes.ORGANIZATION_ALREADY_EXISTS)

//  USER
class UserNotFoundException(msg: String) : BaseException(ErrorCodes.USER_NOT_FOUND)
class UserAlreadyExistsException(msg: String) : BaseException(ErrorCodes.USER_ALREADY_EXISTS)
class UsernameAlreadyExistsException(msg: String) : BaseException(ErrorCodes.USERNAME_ALREADY_EXISTS)

//  ROLE
class RoleNotFoundException(msg: String) : BaseException(ErrorCodes.ROLE_NOT_FOUND)
class RoleAlreadyExistsException(msg: String) : BaseException(ErrorCodes.ROLE_ALREADY_EXISTS)

//  PERMISSION
class PermissionNotFoundException(msg: String) : BaseException(ErrorCodes.PERMISSION_NOT_FOUND)
class PermissionAlreadyExistsException(msg: String) : BaseException(ErrorCodes.PERMISSION_ALREADY_EXISTS)

//  DOCUMENT
class DocumentNotFoundException(msg: String) : BaseException(ErrorCodes.DOCUMENT_NOT_FOUND)
class DocumentAlreadyExistsException(msg: String) : BaseException(ErrorCodes.DOCUMENT_ALREADY_EXISTS)

//  DOCUMENT FIELD
class DocumentFieldNotFoundException(msg: String) : BaseException(ErrorCodes.DOCUMENT_FIELD_NOT_FOUND)

//  GENERATED DOCUMENT
class GeneratedDocumentNotFoundException(msg: String) : BaseException(ErrorCodes.GENERATED_DOCUMENT_NOT_FOUND)

//  AUTHENTICATION
class InvalidCredentialsException(msg: String) : BaseException(ErrorCodes.INVALID_CREDENTIALS)
class TokenExpiredException(msg: String) : BaseException(ErrorCodes.TOKEN_EXPIRED)
class UnauthorizedException(msg: String) : BaseException(ErrorCodes.UNAUTHORIZED)
class ForbiddenException(msg: String) : BaseException(ErrorCodes.FORBIDDEN)

//  BUSINESS LOGIC
class InvalidFieldValueException(msg: String) : BaseException(ErrorCodes.INVALID_FIELD_VALUE)
class RequiredFieldMissingException(msg: String) : BaseException(ErrorCodes.REQUIRED_FIELD_MISSING)
class ConstraintViolationException(msg: String) : BaseException(ErrorCodes.CONSTRAINT_VIOLATION)
class FileUploadException(msg: String) : BaseException(ErrorCodes.FILE_UPLOAD_ERROR)
class FileGenerationException(msg: String) : BaseException(ErrorCodes.FILE_GENERATION_ERROR)