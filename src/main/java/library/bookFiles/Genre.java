package library.bookFiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Genre {
    FANTASY("Фэнтези"),
    SCIENCE_FICTION("Научная фантастика"),
    ROMANCE("Роман"),
    MYSTERY("Детектив"),
    THRILLER("Триллер"),
    HORROR("Ужасы"),
    HISTORICAL("История"),
    ADVENTURE("Приключения"),
    CRIME("Криминал"),
    NON_FICTION("Нон-фикшн"),
    BIOGRAPHY("Биография"),
    AUTOBIOGRAPHY("Автобиография"),
    SELF_DEVELOPMENT("Личностный рост"),
    YOUNG_ADULT("Книга для подростков"),
    CHILDREN_LITERATURE("Детская литература"),
    DYSTOPIAN("Антиутопия"),
    UTOPIAN("Утопия"),
    CLASSIC("Классика"),
    HUMOR("Юмор"),
    SATIRE("Сатира"),
    POETRY("Поэзия"),
    DRAMA("Драма"),
    GRAPHIC_NOVEL("Графический роман"),
    PARANORMAL("Паранормальное"),
    OTHER("Другой");

    private final String displayName;
    Genre(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName()
    {
        return displayName;
    }

    public static Genre fromDisplayName(String displayName)
    {
        for (Genre genre : values()) {
            if (genre.displayName.equals(displayName)) {
                return genre;
            }
        } throw new IllegalArgumentException("Неизвестный жанр: " + displayName);
    }
    public static List<String> getDisplayNames() {
        return Arrays.stream(values()) .map(Genre::getDisplayName) .collect(Collectors.toList());
    }

    public static Genre fromString(String s) {
        if (s == null || s.isBlank()) {
            return OTHER;
        }

        String normalized = s.trim();

        for (Genre genre : values()) {
            if (genre.name().equalsIgnoreCase(normalized)
                    || genre.getDisplayName().equalsIgnoreCase(normalized)) {
                return genre;
            }
        }
        return OTHER;
    }
}
