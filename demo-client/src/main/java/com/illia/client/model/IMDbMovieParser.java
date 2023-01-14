package com.illia.client.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
        } else{
            return null;
        }
    }

    private IMDbMovieEntity tryBuild(Matcher matcher){
        return IMDbMovieEntity.builder()
                .title(matcher.group(1))
                .date(matcher.group(2))
                .color(matcher.group(3))
                .genre(matcher.group(4))
                .language(matcher.group(5))
                .country(matcher.group(6))
                .rating(matcher.group(7))
                .leadActor(matcher.group(8))
                .directorName(matcher.group(9))
                .leadActorFBLikes(matcher.group(10))
                .castFBLikes(matcher.group(11))
                .directorFBLikes(matcher.group(12))
                .movieFBLikes(matcher.group(13))
                .IMBdScore(matcher.group(14).replace(",", "."))
                .totalReviews(matcher.group(15))
                .duration(matcher.group(16))
                .grossRevenue(matcher.group(17))
                .budget(matcher.group(18))
                .build();

    }

}
