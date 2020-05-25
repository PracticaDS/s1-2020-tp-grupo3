package ar.edu.unq.pdes.myprivateblog.data

data class ErrorState constructor(
    private val type: ErrorType? = ErrorType.SYSTEM,
    private val errorMessage: String? = "error",
    private val throwable: Throwable? = null
) {
    companion object {
        fun error(error: Throwable) = ErrorState(throwable = error,errorMessage = error.message, type = ErrorType.SYSTEM)
        fun validationError() = ErrorState(type = ErrorType.VALIDATION)
    }

    enum class ErrorType {
        SYSTEM, VALIDATION
    }

    fun getErrorMessage() : String {
        if (errorMessage != null) {
            return errorMessage
        }
        throw IllegalStateException("errorMessage shouldn't be null")
    }

    fun getType() : ErrorType {
        if (type != null) {
            return type
        }
        throw IllegalStateException("type shouldn't be null")
    }

}