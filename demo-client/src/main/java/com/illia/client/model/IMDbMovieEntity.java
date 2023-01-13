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
}