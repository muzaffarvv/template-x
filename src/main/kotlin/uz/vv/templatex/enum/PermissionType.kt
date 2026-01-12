package uz.vv.templatex.enum

enum class PermissionType(val code: String, val description: String) {
    USER_CREATE("USER_CREATE", "Permission to create users"),
    USER_READ("USER_READ", "Permission to read users"),
    USER_UPDATE("USER_UPDATE", "Permission to update users"),
    USER_DELETE("USER_DELETE", "Permission to delete users"),
    
    DOCUMENT_CREATE("DOCUMENT_CREATE", "Permission to create documents"),
    DOCUMENT_READ("DOCUMENT_READ", "Permission to read documents"),
    DOCUMENT_UPDATE("DOCUMENT_UPDATE", "Permission to update documents"),
    DOCUMENT_DELETE("DOCUMENT_DELETE", "Permission to delete documents"),
    
    ORGANIZATION_READ("ORGANIZATION_READ", "Permission to read organization")
}
