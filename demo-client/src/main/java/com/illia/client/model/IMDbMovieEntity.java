package com.illia.client.model;


import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    private static List<String> attributesList;
    public static boolean isAttributeValid(String attribute) {
        if (attributesList == null) {
            attributesList = new ArrayList<>();
            attributesList.add(0, "title");
            attributesList.add(1, "date");
            attributesList.add(2, "color");
            attributesList.add(3, "genre");
            attributesList.add(4, "language");
            attributesList.add(5, "country");
            attributesList.add(6, "rating");
            attributesList.add(7, "leadActor");
            attributesList.add(8, "directorName");
            attributesList.add(9, "leadActorFBLikes");
            attributesList.add(10, "castFBLikes");
            attributesList.add(11, "movieFBLikes");
            attributesList.add(12, "IMBdScore");
            attributesList.add(13, "totalReviews");
            attributesList.add(14, "duration");
            attributesList.add(15, "grossRevenue");
            attributesList.add(15, "budget");
        }
        return attributesList.contains(attribute);
    }

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