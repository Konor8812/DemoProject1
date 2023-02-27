package com.illia.client.model.request.registry;

import lombok.Getter;

@Getter
public enum AttributeRegistry {
  TITLE(false,false),
  DATE(false,false),
  COLOR(false,true),
  GENRE(false,true),
  LANGUAGE(false,true),
  COUNTRY(false,true),
  RATING(false,true),
  LEAD_ACTOR(false,true),
  DIRECTOR_NAME(false,true),
  LEAD_ACTOR_FB_LIKES(true,false),
  CAST_FB_LIKES(true,false),
  DIRECTOR_FB_LIKES(true,false),
  MOVIE_FB_LIKES(true,false),
  IMDB_SCORE(true,true),
  TOTAL_REVIEWS(true,false),
  DURATION(true,false),
  GROSS_REVENUE(true,false),
  BUDGET(true,false);

  private final boolean isANumber;
  private final boolean okForGroupBy;

  AttributeRegistry(boolean isANumber, boolean okForGroupBy) {
    this.isANumber = isANumber;
    this.okForGroupBy = okForGroupBy;
  }

}
