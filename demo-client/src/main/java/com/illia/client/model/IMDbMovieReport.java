package com.illia.client.model;

import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class IMDbMovieReport {

    private IMDbMovieReport() {
    }

    private static Pattern rowPattern = Pattern.compile("^([^;]+);(\\d+/\\d+/\\d+);(Color|Black and White);([a-zA-Z-]+);([a-zA-Z]+);([^;]+);([a-zA-Z0-9- ]+);([^;]+);([^;]+);(\\d+);(\\d+);(\\d+);(\\d+);(\\d+,?\\d*);(\\d+);(\\d+);(\\d+);(\\d+)");
    private static Pattern grossRowPattern = Pattern.compile("^([^;]+);(\\d+/\\d+/\\d+);(Color|Black and White);([a-zA-Z-]+);([a-zA-Z]+);([^;]+);([a-zA-Z0-9- ]+)?;([^;]+)?;([^;]+)?;(\\d+)?;(\\d+);(\\d+);(\\d+);(\\d+,?\\d*);(\\d+);(\\d+);(\\d+);(\\d+)");
    private int entitiesAmount = 0;
    private List<IMDbMovieEntity> reports;

    public static IMDbMovieReport parse(File file) {
        var result = new IMDbMovieReport();
        if (file.canRead()) {
            try (var linesStream = new BufferedReader(new FileReader(file)).lines()) {
                result.reports = linesStream.skip(1)
                        .map(result::parseRow)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result.entitiesAmount = result.reports.size();
        return result;
    }

    private IMDbMovieEntity parseRow(String row) {
        Matcher matcher;
        if ((matcher = rowPattern.matcher(row)).find()) {
            return tryBuild(matcher);
        } else if ((matcher = grossRowPattern.matcher(row)).find()){
            return tryBuild(matcher);
        }else{
            return null;
        }
    }

    private IMDbMovieEntity tryBuild(Matcher matcher){
        return IMDbMovieEntity.builder()
                .title(matcher.group(0))
                .date(matcher.group(1))
                .color(matcher.group(2))
                .genre(matcher.group(3))
                .language(matcher.group(4))
                .country(matcher.group(5))
                .rating(matcher.group(6))
                .leadActor(matcher.group(7))
                .directorName(matcher.group(7))
                .IMBdScore(matcher.group(8))
                .totalReviews(matcher.group(9))
                .duration(matcher.group(10))
                .grossRevenue(matcher.group(11))
                .budget(matcher.group(12))
                .build();

    }

}
