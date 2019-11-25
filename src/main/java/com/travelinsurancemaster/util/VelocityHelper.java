package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.Subcategory;
import com.travelinsurancemaster.model.dto.SubcategoryValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by N.Kurennoy on 09.06.2016.
 */
public class VelocityHelper {

    private static final Logger log = LoggerFactory.getLogger(VelocityHelper.class);
    private static final VelocityHelper instance = new VelocityHelper();
    private DecimalFormat formatter = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);


    private VelocityHelper() {
    }

    public static VelocityHelper getInstance() {
        return instance;
    }

    /**
     * Category template processing
     */
    public String replace(Category category, PolicyMetaCategoryValue policyMetaCategoryValue, BigDecimal tripCost,
                          List<Subcategory> subcategories) {
        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
        SimpleNode node = null;

        String templateString = getTemplate(category, policyMetaCategoryValue, subcategories);
        try {
            node = runtimeServices.parse(new StringReader(templateString), "template");
        } catch (ParseException e) {
            log.debug("Error while parsing template '{}'", templateString);
        }
        Template template = new Template();
        template.setRuntimeServices(runtimeServices);
        template.setData(node);
        template.initDocument();

        VelocityContext context = new VelocityContext();
        if (templateString.contains(category.getCode())) {
            Integer categoryValue = policyMetaCategoryValue.getValueByType(category.getValueType(), tripCost);
            String categoryValueStr;
            if (categoryValue != null) {
                categoryValueStr = formatter.format(categoryValue);
            } else {
                categoryValueStr = policyMetaCategoryValue.getCaption();
            }
            context.put(category.getCode(), categoryValueStr);
        }
        for (Subcategory subcategory : subcategories) {
            for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
                if (subcategoryValue.getId() == null) {
                    continue;
                }
                if (Objects.equals(subcategory.getId(), subcategoryValue.getSubcategory().getId())) {
                    context.put(subcategory.getSubcategoryCode(), subcategoryValue.getSubcategoryValue());
                    break;
                }
            }
        }
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private String getTemplate(Category category, PolicyMetaCategoryValue policyMetaCategoryValue, List<Subcategory> subcategories) {
        Map<Long, String> subcategoryTemplates = new HashMap<>();
        subcategories.forEach(subcategory -> subcategoryTemplates.put(subcategory.getId(), subcategory.getTemplate()));

        List<String> templates = new ArrayList<>();
        if (StringUtils.isNotBlank(category.getTemplate())) {
            templates.add(category.getTemplate());
        }
        if (CollectionUtils.isNotEmpty(subcategories)) {
            policyMetaCategoryValue.getSubcategoryValuesList().forEach(value -> {
                if (StringUtils.isNotBlank(value.getSubcategoryValue())) {
                    templates.add(subcategoryTemplates.get(value.getSubcategory().getId()));
                }
            });
        }
        return String.join(" ", templates);
    }
}
