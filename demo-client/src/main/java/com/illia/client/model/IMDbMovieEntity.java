package com.illia.client.model;


import com.illia.client.model.request.registry.AttributeRegistry;
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

  public String getFieldAccessor(AttributeRegistry attribute) {
    switch (attribute) {
      case TITLE:
        return getTitle();
      case DATE:
        return getDate();
      case COLOR:
        return getColor();
      case GENRE:
        return getGenre();
      case LANGUAGE:
        return getLanguage();
      case COUNTRY:
        return getCountry();
      case RATING:
        return getRating();
      case LEAD_ACTOR:
        return getLeadActor();
      case DIRECTOR_NAME:
        return getDirectorName();
      case LEAD_ACTOR_FB_LIKES:
        return getLeadActorFBLikes();
      case CAST_FB_LIKES:
        return getCastFBLikes();
      case DIRECTOR_FB_LIKES:
        return getDirectorFBLikes();
      case MOVIE_FB_LIKES:
        return getMovieFBLikes();
      case IMDB_SCORE:
        return getIMBdScore();
      case TOTAL_REVIEWS:
        return getTotalReviews();
      case DURATION:
        return getDuration();
      case GROSS_REVENUE:
        return getGrossRevenue();
      case BUDGET:
        return getBudget();

      default:
        return null;
    }
  }

}