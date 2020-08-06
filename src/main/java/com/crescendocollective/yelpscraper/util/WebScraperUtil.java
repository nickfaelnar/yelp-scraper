package com.crescendocollective.yelpscraper.util;

import com.crescendocollective.yelpscraper.constants.GlobalConstants;
import lombok.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class WebScraperUtil {

    private Document doc;

    public WebScraperUtil(@NonNull String pageUrl) throws IOException {
        this.doc = Jsoup.parse(new URL(pageUrl), GlobalConstants.MAX_PAGE_TIMEOUT_MS);
    }

    public String getTextBySelector(String selector) {
        return doc.selectFirst(selector)
                .text();
    }

    public Element getFirstElementBySelector(String selector) {
        return doc.selectFirst(selector);
    }

    public Elements getElementsBySelector(String selector) {
        return doc.select(selector);
    }

}
