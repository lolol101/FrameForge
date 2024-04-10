package client;

public class ServerCommands {
    public enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
        GET_MAIN_PAGE_POSTS_SET,
        SET_LIKE,
        SET_COMMENT,
        SUBSCRIBE
    };

    public enum RESPONSE_TYPE {
        REGISTER_BACK,
        AUTHORIZATION_BACK,
        MAKE_POST_BACK,
        GET_MAIN_PAGE_POSTS_SET_BACK,
        SET_LIKE_BACK,
        SET_COMMENT_BACK,
        SUBSCRIBE_BACK
    };

    public enum STATUS {
        OK,
        USERNAME_EXIST,
        USERNAME_NOT_FOUND,
        PASS_WRONG,
        ERROR,
    };
}
