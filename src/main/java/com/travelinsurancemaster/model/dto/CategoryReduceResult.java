package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chernov Artur on 13.07.15.
 */
public class CategoryReduceResult implements Serializable {
    static final long serialVersionUID = 1L;
    public static final List<String> CATEGORIES_FOR_REDUCE = Arrays.asList(CategoryCodes.PRE_EX_WAIVER, CategoryCodes.PRIMARY_MEDICAL, CategoryCodes.TRIP_CANCELLATION);
    private static final String DEFAULT_ORIGINAL_MESSAGE = "";

    private QuoteRequest quoteRequest;
    private List<Category> reducedCategories = new ArrayList<>();
    private boolean reduced = true;

    public CategoryReduceResult(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public List<Category> getReducedCategories() {
        return reducedCategories;
    }

    public void setReducedCategories(List<Category> reducedCategories) {
        this.reducedCategories = reducedCategories;
    }

    public boolean isReduced() {
        return reduced;
    }

    public void setReduced(boolean reduced) {
        this.reduced = reduced;
    }

    public String getMessage() {
        StringBuilder categoryReducedMessage = new StringBuilder(DEFAULT_ORIGINAL_MESSAGE);
        if (CollectionUtils.isEmpty(this.reducedCategories)) {
            return categoryReducedMessage.toString();
        }
        for (Category category : this.reducedCategories) {
            categoryReducedMessage.append(", ");
            categoryReducedMessage.append(category.getName());
        }
        categoryReducedMessage.append(" not eligible)");
        String temp = categoryReducedMessage.toString();
        if (this.reducedCategories.size() > 1) {
            categoryReducedMessage.replace(temp.lastIndexOf(','), temp.lastIndexOf(',') + 1, " and");
        }
        return categoryReducedMessage.toString().replaceFirst(", ", " (");
    }
}
