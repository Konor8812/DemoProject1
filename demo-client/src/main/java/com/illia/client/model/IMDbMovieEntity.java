package com.illia.client.model;


import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class IMDbMovieEntity {
    private String title;
    private String date;
    private String color;
    private String genre;
    private String language;
    private String country;
    private String rating;
    private String leadActor;
    private String directorName;
    private String leadActorFBLikes;
    private String castFBLikes;
    private String directorFBLikes;
    private String movieFBLikes;
    private String IMBdScore;
    private String totalReviews;
    private String duration;
    private String grossRevenue;
    private String budget;

    public String getFieldAccessor(String field){
        switch (field){
            case "title":
                return getTitle();
            case "date":
                return getDate();
            case "color":
                return getColor();
            case "genre":
                return getGenre();
            case "language":
                return getLanguage();
            case "country":
                return getCountry();
            case "rating":
                return getRating();
            case "leadActor":
                return getLeadActor();
            case "directorName":
                return getDirectorName();
            case "leadActorFBLikes":
                return getLeadActorFBLikes();
            case "castFBLikes":
                return getCastFBLikes();
            case "directorFBLikes":
                return getDirectorFBLikes();
            case "movieFBLikes":
                return getMovieFBLikes();
            case "IMBdScore":
                return getIMBdScore();
            case "duration":
                return getDuration();
            case "grossRevenue":
                return getGrossRevenue();
            case "budget":
                return getBudget();

            default : return null;
        }
    }

}