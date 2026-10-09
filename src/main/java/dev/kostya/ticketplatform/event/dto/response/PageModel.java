package dev.kostya.ticketplatform.event.dto.response;

import java.util.List;

public record PageModel<T> (
        List<T> model,
        PageMeta pageMeta
) { }
