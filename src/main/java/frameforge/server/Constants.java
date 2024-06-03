package frameforge.server;

import java.util.*;

public class Constants {

    public static Map<String, Integer> tagToIndex = new HashMap<>();
    public static Map<Integer, String> indexToTag = new HashMap<>();
    public static final List<String> TAGS = new ArrayList<>(Arrays.asList("Cars",
                                                                        "Nature",
                                                                        "Animals",
                                                                        "Abstract",
                                                                        "Music",
                                                                        "Art",
                                                                        "Technic",
                                                                        "Fantasy",
                                                                        "Aesthetics",
                                                                        "Clothes",
                                                                        "Anime",
                                                                        "People",
                                                                        "Realism",
                                                                        "Space",
                                                                        "Games",
                                                                        "Martial art",
                                                                        "Design",
                                                                        "Utopia",
                                                                        "Journey",
                                                                        "Animation",
                                                                        "Movie",
                                                                        "Relaxation",
                                                                        "Mood",
                                                                        "Geometry",
                                                                        "Sadness",
                                                                        "Joy",
                                                                        "Madness",
                                                                        "Other"
                                                                        ));

    static {
        for (int i = 0; i < TAGS.size(); ++i) {
            tagToIndex.put(TAGS.get(i), i);
            indexToTag.put(i, TAGS.get(i));
        }
    } 
    public enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
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
        GET_FULL_PHOTO_BACK,
        SET_MAIN_POST_BACK,
        SET_LIKE_BACK,
        SET_COMMENT_BACK,
        SUBSCRIBE_BACK,
        //EXTEND_TOKEN_BACK
    };

    public enum STATUS {
        OK,
        USERNAME_EXIST,
        USERNAME_NOT_FOUND,
        PASS_WRONG,
        ERROR
    };

    public enum ImgType {
        SCALED, FULL
    };       
}
