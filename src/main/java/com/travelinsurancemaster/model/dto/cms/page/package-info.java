/**
 * Created by Chernov Artur on 16.10.15.
 */

@AnalyzerDefs({
        @AnalyzerDef(name = "customanalyzer",
                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                filters = {
                        @TokenFilterDef(factory = StandardFilterFactory.class),
                        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                                @Parameter(name = "language", value = "English")
                        })
                }),
        @AnalyzerDef(name = "htmlalyzer",
                charFilters = {
                        @CharFilterDef(factory = HTMLStripCharFilterFactory.class)
                },
                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                filters = {
                        @TokenFilterDef(factory = StandardFilterFactory.class),
                        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                                @Parameter(name = "language", value = "English")
                        })
                })
})
package com.travelinsurancemaster.model.dto.cms.page;

import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;