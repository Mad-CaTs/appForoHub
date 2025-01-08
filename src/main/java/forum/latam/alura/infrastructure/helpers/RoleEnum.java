package forum.latam.alura.infrastructure.helpers;

public enum RoleEnum {
    ADMIN,
    USER,
    INVITED,
    DEVELOPER;

    @Override
    public String toString() {
        return name();
    }
}