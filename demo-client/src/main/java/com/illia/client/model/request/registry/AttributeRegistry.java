package com.illia.client.model.request.registry;


public enum AttributeRegistry {
    TITLE("title"),
    DATE("date"),
    COLOR("color"),
    GENRE("genre"),
    LANGUAGE("language"),
    COUNTRY("country"),
    RATING("rating"),
    LEAD_ACTOR("leadActor"),
    DIRECTOR_NAME("directorName"),
    LEAD_ACTOR_FB_LIKES("leadActorFBLikes"),
    CAST_FB_LIKES("castFBLikes"),
    MOVIE_FB_LIKES("movieFBLikes"),
    IMDB_SCORE("IMBdScore"),
    TOTAL_REVIEWS("totalReviews"),
    DURATION("duration"),
    GROSS_REVENUE("grossRevenue"),
    BUDGET("budget");

    private final String attribute;

    AttributeRegistry(String attribute){
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attribute;
    }

    public static AttributeRegistry getAttributeValue(String attribute){
        if(attribute != null){
            try{
                return AttributeRegistry.valueOf(attribute);
            }catch (IllegalArgumentException ex){
                return null;
            }
        }
        return null;
    }
}
