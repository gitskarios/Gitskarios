package com.alorma.github.sdk.services.issues.story;

import com.alorma.github.sdk.bean.dto.response.GithubCommentReactions;
import com.alorma.github.sdk.services.issues.reactions.GithubReaction;
import com.alorma.github.sdk.services.issues.reactions.GithubReactionType;

public class ReactionMapper {

    public static void parseGithubReaction(GithubCommentReactions reactions) {
        if (reactions != null) {
            try {
                reactions.setTotalCount(((Double) reactions.get("total_count")).intValue());

                parseGithubReaction(reactions, GithubReactionType.PlusOne);
                parseGithubReaction(reactions, GithubReactionType.MinusOne);
                parseGithubReaction(reactions, GithubReactionType.Laugh);
                parseGithubReaction(reactions, GithubReactionType.Confused);
                parseGithubReaction(reactions, GithubReactionType.Hooray);
                parseGithubReaction(reactions, GithubReactionType.Heart);

                reactions.setUrl((String) reactions.get("url"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseGithubReaction(GithubCommentReactions reactions
            , GithubReactionType githubReactionType) {
        if (reactions.get(githubReactionType.getGithubKey()) != null) {
            int value = ((Double) reactions.get(githubReactionType.getGithubKey())).intValue();
            if (value > 0) {
                GithubReaction reaction = new GithubReaction(githubReactionType, value);
                reactions.getReactions().add(reaction);
            }
        }
    }
}
