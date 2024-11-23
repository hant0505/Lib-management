package Users;

public class UserSession {
    private static User user;

    public static void setUser(User user) {
        UserSession.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static void clearSession() {
        user = null;
    }
}

