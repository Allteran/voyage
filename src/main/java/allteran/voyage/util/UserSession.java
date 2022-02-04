package allteran.voyage.util;

public final class UserSession {
    private static UserSession instance;

    private String username;
    private Long posId;

    private UserSession(String username, Long posId) {
        this.username = username;
        this.posId = posId;
    }

    public static UserSession getInstance(String username, Long posId) {
        if(instance == null) {
            instance = new UserSession(username, posId);
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public Long getPosId() {
        return posId;
    }

    public void cleanUserSession() {
        username = null;
        posId = null;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "username='" + username + '\'' +
                ", posId=" + posId +
                '}';
    }
}
