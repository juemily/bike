package com.example.bike.inftrastructure.rest.controller.pagination;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpHeadersBuilder {

    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_LIMIT = "limit";

    public static HttpHeaders generatePaginationHttpHeaders(long offset, int limit, long total, final String baseUrl) throws URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + total);

        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(String.format("?%s=%d&%s=%d", PARAM_OFFSET, offset, PARAM_LIMIT, limit));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().uri(new URI(sb.toString()));
        final StringBuilder linkHeader = new StringBuilder();

        linkHeader.append(createLinkHeader(uriBuilder.build().encode().toUriString(), "self"));

        if (hasNextPage(offset, limit, total)) {
            long nextOffset = offset + limit;
            long nextLimit = (nextOffset + limit > total)? total - nextOffset : limit;
            final String uriForNextPage = constructUri(uriBuilder, nextOffset, nextLimit);
            linkHeader.append(createLinkHeader(uriForNextPage, "next"));
        }
        if (hasPreviousPage(offset, limit)) {
            final String uriForPrevPage = constructUri(uriBuilder, Math.max((offset - limit), 0), limit);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForPrevPage, "prev"));
        }
        if (hasFirstPage(offset)) {
            final String uriForFirstPage = constructUri(uriBuilder, 0, limit);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForFirstPage, "first"));
        }
        if (hasLastPage(offset, limit, total)) {
            long lastOffset, lastLimit;
            long pagesLeft = (total - offset) / limit;
            lastOffset = offset + (limit * pagesLeft);
            if (lastOffset + limit > total) {
                lastLimit = total - lastOffset;
            } else {
                lastLimit = limit;
            }

            if (lastOffset == total) {
                lastOffset -= limit;
                lastLimit = limit;
            }

            final String uriForLastPage = constructUri(uriBuilder, lastOffset,  lastLimit);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForLastPage, "last"));
        }

        headers.add(HttpHeaders.LINK, linkHeader.toString());
        return headers;
    }

    public static String createLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }

    private static String constructUri(final UriComponentsBuilder uriBuilder, final long offset, final long limit) {
        return uriBuilder.replaceQueryParam(PARAM_OFFSET, offset).replaceQueryParam(PARAM_LIMIT, limit).build().encode().toUriString();
    }

    private static boolean hasNextPage(final long offset, final int limit, final long total) {
        return ((offset + limit) < total) && ((total - (offset + limit)) > limit);
    }

    private static boolean hasPreviousPage(final long offset, final int limit) {
        return offset - limit > 0;
    }

    private static boolean hasFirstPage(final long offset) {
        return offset > 0;
    }

    private static boolean hasLastPage(final long offset, final int limit, final long total) {
        return offset < total && (offset + limit) < total && limit < total;
    }

    private static void appendCommaIfNecessary(final StringBuilder linkHeader) {
        if (linkHeader.length() > 0) {
            linkHeader.append(", ");
        }
    }

}
