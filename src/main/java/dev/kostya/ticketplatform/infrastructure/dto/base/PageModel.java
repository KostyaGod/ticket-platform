package dev.kostya.ticketplatform.infrastructure.dto.base;

import java.util.List;

public record PageModel<T> (
        List<T> model,
        PageMeta pageMeta
) { }
