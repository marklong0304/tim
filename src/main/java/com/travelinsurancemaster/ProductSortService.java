package com.travelinsurancemaster;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.FilteredProducts;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.SortOrder;
import com.travelinsurancemaster.services.PolicyMetaCategoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author Artur Chernov
 */

@Service
public class ProductSortService {
    public static final SortOrder DEFAULT_ORDER = SortOrder.LTH;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Value("${sort.weight.category.tripCancellation}")
    private Integer cancellationWeight;

    @Value("${sort.weight.category.primaryMedical}")
    private Integer medicalWeight;

    public void sort(FilteredProducts filteredProducts, QuoteRequest quoteRequest, SortOrder sortOrder) {

        List<Product> products = filteredProducts.getIncludedProducts();
        Product bestPlan;
        PriceComparator customComparator;

        switch (sortOrder) {
            case LTH:
                fillWeightedCategories(filteredProducts, quoteRequest);
                bestPlan = getBestPlan(products, quoteRequest);

                customComparator = new PriceComparator(quoteRequest, bestPlan);
                products.sort(customComparator);
                break;

            case HTL:
//                products.remove(bestPlan);
                fillWeightedCategories(filteredProducts, quoteRequest);
                bestPlan = getBestPlan(products, quoteRequest);

                customComparator = new PriceComparator(quoteRequest, bestPlan);
                products.sort(customComparator.reversed());
//                products.add(0, bestPlan);
                break;

            case PROVIDER:
                fillWeightedCategories(filteredProducts, quoteRequest);
                bestPlan = getBestPlan(products, quoteRequest);

                ProviderComparator providerComparator = new ProviderComparator(quoteRequest, bestPlan);
                products.sort(providerComparator);
                break;
        }
        int ln = products.size();
    }


    private void fillWeightedCategories(FilteredProducts products, QuoteRequest quoteRequest){
        for(Product product: products.getIncludedProducts()){
            product.setHasCancellation(hasCategory(product, quoteRequest, CategoryCodes.TRIP_CANCELLATION));
            product.setHasCancellation(hasPrimaryMedical(product, quoteRequest, CategoryCodes.PRIMARY_MEDICAL));
        }
    }

    public Product getBestPlan(List<Product> policies, QuoteRequest quoteRequest) {
        CustomComparator customComparator = new CustomComparator(quoteRequest);
        if (CollectionUtils.isEmpty(policies)) {
            return null;
        }
        Product best = null;
        for (Product policy : policies) {
            if (!policy.getPolicyMeta().isPurchasable()) {
                continue;
            }
            if (best == null) {
                best = policy;
            } else {
                best = customComparator.compare(best, policy) == -1 ? best : policy;
            }
        }
        return best;
    }

    private boolean hasCategory(Product product, QuoteRequest quoteRequest, String categoryCode) {
        return product.getPolicyMeta() != null &&  policyMetaCategoryService.isPolicyMetaContainsCategory(product.getPolicyMeta().getId(), categoryCode, quoteRequest);
    }

    private boolean hasPrimaryMedical(Product product, QuoteRequest quoteRequest, String categoryCode){
        if (product.getPolicyMeta() == null){
            return false;
        }
        PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(product.getPolicyMeta().getId(), categoryCode, quoteRequest);
        if (policyMetaCategory == null || CollectionUtils.isEmpty(policyMetaCategory.getValues())){
            return false;
        }
        return StringUtils.equals(policyMetaCategory.getValues().get(0).getCaption(), "Yes");
    }

    private class PriceComparator implements Comparator<Product> {

        private QuoteRequest quoteRequest;
        private Product bestPlan;

        public PriceComparator(QuoteRequest quoteRequest, Product bestPlan) {
            this.quoteRequest = quoteRequest;
            this.bestPlan = bestPlan;
        }

        @Override
        public int compare(Product firstProduct, Product secondProduct) {
            if(firstProduct.equals(bestPlan))
                return -1;
            if(secondProduct.equals(bestPlan))
                return 1;
            return firstProduct.getTotalPrice().compareTo(secondProduct.getTotalPrice());
        }
    }

    private class CustomComparator implements Comparator<Product> {

        private QuoteRequest quoteRequest;

        CustomComparator(QuoteRequest quoteRequest) {
            this.quoteRequest = quoteRequest;
        }

        @Override
        public int compare(Product firstProduct, Product secondProduct) {
            final int BEFORE = -1;
            final int EQUAL = 0;
            final int AFTER = 1;
            if (firstProduct.equals(secondProduct)) return EQUAL;

            boolean firstHasCancellation = firstProduct.isHasCancellation();
            boolean firstHasMedical = firstProduct.isHasPrimaryMedical();

            boolean secondHasCancellation = secondProduct.isHasCancellation();
            boolean secondHasMedical = secondProduct.isHasPrimaryMedical();

            float firstWeight = BooleanUtils.toInteger(firstHasCancellation) * cancellationWeight + BooleanUtils.toInteger(firstHasMedical) * medicalWeight;
            float secondWeight = BooleanUtils.toInteger(secondHasCancellation) * cancellationWeight + BooleanUtils.toInteger(secondHasMedical) * medicalWeight;
            if (firstWeight > secondWeight) {
                return BEFORE;
            } else if (firstWeight < secondWeight) {
                return AFTER;
            } else {
                return firstProduct.compareTo(secondProduct);
            }
        }
    }

    private class ProviderComparator implements Comparator<Product> {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        private QuoteRequest quoteRequest;
        private Product bestPlan;

        public ProviderComparator(QuoteRequest quoteRequest, Product bestPlan) {
            this.quoteRequest = quoteRequest;
            this.bestPlan = bestPlan;
        }

        @Override
        public int compare(Product firstProduct, Product secondProduct) {
            if(firstProduct.equals(bestPlan))
                return -1;
            if(secondProduct.equals(bestPlan))
                return 1;

//            if (firstProduct == null || secondProduct == null || firstProduct.getPolicyMeta() == null || secondProduct.getPolicyMeta() == null
//                    || firstProduct.getPolicyMeta().getVendor() == null || secondProduct.getPolicyMeta().getVendor() == null) {
//                return AFTER;
//            }

            String firstName = firstProduct.getPolicyMeta().getVendor().getName();
            String secondName = secondProduct.getPolicyMeta().getVendor().getName();
            String bestName = bestPlan.getPolicyMeta().getVendor().getName();

            if(firstName.compareToIgnoreCase(bestName) == 0 && secondName.compareToIgnoreCase(bestName) != 0)
                return -1;
            if(secondName.compareToIgnoreCase(bestName) == 0 && firstName.compareToIgnoreCase(bestName) != 0)
                return 1;

            int compareByNames = firstName.compareToIgnoreCase(secondName);

            if (compareByNames == 0) {
                PriceComparator customComparator = new PriceComparator(quoteRequest, bestPlan);
                return customComparator.compare(firstProduct, secondProduct);
            } else {
                return compareByNames;
            }
        }
    }
}
