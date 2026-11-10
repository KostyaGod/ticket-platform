package dev.kostya.ticketplatform.event.dto.base;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageModel<T> (
        List<T> model,
        PageMeta pageMeta
) {
    public PageModel(Page<T> page) {
        this(
                page.getContent(),
                new PageMeta(page)
        );
    }

    public <R> PageModel(Page<R> page, Function<List<R>, List<T>> mapping) {
        this(
                mapping.apply(page.getContent()),
                new PageMeta(page)
        );
    }
}
