package pk.training.basit.polarbookshop.catalogservice.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse(
        List<?> content,
        int currentPage,
        long totalElements,
        int totalPages
) {

    public static PagedResponse.Builder builder(Page<?> page) {
        return new PagedResponse.Builder(page);
    }

    //Builder
    public static final class Builder {

        List<?> content;
        int currentPage;
        long totalElements;
        int totalPages;

        public Builder(Page<?> page) {
            this.content = page.getContent();
            this.currentPage = page.getNumber() + 1;
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
        }

        public PagedResponse build() {
            List<?> content = this.content;
            int currentPage = this.currentPage;
            long totalElements = this.totalElements;
            int totalPages = this.totalPages;
            return new PagedResponse(content, currentPage, totalElements, totalPages);
        }
    }

}
