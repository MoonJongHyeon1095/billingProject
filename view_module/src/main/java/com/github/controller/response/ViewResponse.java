package com.github.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ViewResponse {
    private final int lastWatched;
    public ViewResponse(final int lastWatched) {
        this.lastWatched = lastWatched;
    }
}
