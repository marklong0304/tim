package com.travelinsurancemaster.model.dto.report.sales;

import com.travelinsurancemaster.model.dto.PolicyMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 02.10.15.
 */
public class SalesTable implements Serializable {
    private static final long serialVersionUID = 2465015246555291344L;

    private List<String> header = new ArrayList<>();
    private Map<PolicyMeta, SalesRow> content = new HashMap<>();
    private SalesRow footer = new SalesRow();

    public SalesTable() {
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public Map<PolicyMeta, SalesRow> getContent() {
        return content;
    }

    public void setContent(Map<PolicyMeta, SalesRow> content) {
        this.content = content;
    }

    public SalesRow getFooter() {
        return footer;
    }

    public void setFooter(SalesRow footer) {
        this.footer = footer;
    }
}
