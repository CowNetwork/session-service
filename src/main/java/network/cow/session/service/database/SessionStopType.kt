package network.cow.session.service.database

enum class SessionStopType {
    UNKNOWN,
    DISCONNECTED,
    MAINTENANCE,
    ERROR,
    KICKED,
    BANNED,
    BLACKLISTED,
    CUSTOM
}