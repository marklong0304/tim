package com.travelinsurancemaster.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Alexander.Isaenco
 */
@Component
public class ApplicationVersion implements ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(ApplicationVersion.class);

    private static final String TITLE = "Implementation-Title";
    private static final String VERSION = "Implementation-Version";

    private String title;
    private String version;

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public void setServletContext(ServletContext ctx) {
        Properties props = new Properties();
        try {
            InputStream manifest = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
            if(manifest != null) {
                props.load(manifest);
                title = (String) props.get(TITLE);
                version = (String) props.get(VERSION);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
