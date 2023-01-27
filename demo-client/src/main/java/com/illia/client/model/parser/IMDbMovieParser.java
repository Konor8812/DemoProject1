package com.illia.client.model.parser;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolderImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Data
public class IMDbMovieParser {

    @Autowired
    private IMDbMovieHolderImpl reportsHolder;

    /**
     * Title and date are mandatory, other attributes can be absent(null)
     */
    private static Pattern rowPattern = Pattern.compile("^([^;]+);(\\d+/\\d+/\\d+);(Color|Black and White)?;([a-zA-Z-]+)?;([a-zA-Z]+)?;([a-zA-Z ]+)?;([a-zA-Z0-9- ]+)?;([^;]+)?;([^;]+)?;(\\d+)?;(\\d+)?;(\\d+)?;(\\d+)?;(\\d+,?\\d*)?;(\\d+)?;(\\d+)?;(\\d+)?;(\\d+)?");


    public List<IMDbMovieEntity> parseFile(File file) {

        if (file.canRead()) {
            try (var linesStream = new BufferedReader(new FileReader(file)).lines()) {
                var reports = linesStream.skip(1)
                        .map(this::parseRow)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                reportsHolder.saveEntities(file.getName(), reports);
                return reports;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return List.of();
    }

    private IMDbMovieEntity parseRow(String row) {
        Matcher matcher;
        if ((matcher = rowPattern.matcher(row)).find()) {
            return tryBuild(matcher);
        } else {
            return null;
        }
    }

    private IMDbMovieEntity tryBuild(Matcher matcher) {
        return IMDbMovieEntity.builder()
                .title(fixNullStrings(matcher.group(1)))
                .date(fixNullStrings(matcher.group(2)))
                .color(fixNullStrings(matcher.group(3)))
                .genre(fixNullStrings(matcher.group(4)))
                .language(fixNullStrings(matcher.group(5)))
                .country(fixNullStrings(matcher.group(6)))
                .rating(fixNullStrings(matcher.group(7)))
                .leadActor(fixNullStrings(matcher.group(8)))
                .directorName(fixNullStrings(matcher.group(9)))
                .leadActorFBLikes(fixNullIntegers(matcher.group(10)))
                .castFBLikes(fixNullIntegers(matcher.group(11)))
                .directorFBLikes(fixNullIntegers(matcher.group(12)))
                .movieFBLikes(fixNullIntegers(matcher.group(13)))
                .IMBdScore(fixNullDecimals(matcher.group(14).replace(",", ".")))
                .totalReviews(fixNullIntegers(matcher.group(15)))
                .duration(fixNullIntegers(matcher.group(16)))
                .grossRevenue(fixNullIntegers(matcher.group(17)))
                .budget(fixNullIntegers(matcher.group(18)))
                .build();

    }

    private String fixNullStrings(String s) {
        return s == null ? "" : s;
    }

    private String fixNullIntegers(String s){
        return s == null ? "0" : s;
    }

    private String fixNullDecimals(String s){
        return s == null ? ".0" : s;
    }

}
