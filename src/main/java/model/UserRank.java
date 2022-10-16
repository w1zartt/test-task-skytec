package model;

import java.util.Arrays;

public enum UserRank {
    MEMBER("MEMBER"),
    LEAD("LEAD");

    private String rankName;

    UserRank(String rankName) {
        this.rankName = rankName;
    }

    public String getAsText() {
        return this.rankName;
    }

    public static UserRank fromString(String rankName) {
         return Arrays.stream(UserRank.values())
                .filter(r -> r.getAsText().equals(rankName))
                .findFirst().orElseThrow(() ->
                         new IllegalArgumentException(
                                 String.format("Rank %s does not exist", rankName)));
    }
}
