package frameforge.client;

public class ServerCommands {
    public enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
        GET_TOP_USERS,
        GET_MAIN_POST,
        SET_MAIN_POST,
        GET_FULL_PHOTO,
        SET_LIKE,
        SET_COMMENT,
        SUBSCRIBE,
        //EXTEND_TOKEN
    };

    public enum RESPONSE_TYPE {
        REGISTER_BACK,
        AUTHORIZATION_BACK,
        GET_MAIN_POST_BACK,
        GET_TOP_USERS_BACK,
        GET_FULL_PHOTO_BACK,
        SET_MAIN_POST_BACK,
        SET_LIKE_BACK,
        SET_COMMENT_BACK,
        SUBSCRIBE_BACK,
        //EXTEND_TOKEN_BACK
    }

    public enum STATUS {
        OK,
        USERNAME_EXIST,
        USERNAME_NOT_FOUND,
        PASS_WRONG,
        ERROR
    }

    public enum ImgType {
        SCALED, FULL
    }
}
