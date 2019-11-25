package com.travelinsurancemaster.services.converters;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chernov Artur on 09.02.2016.
 */

public class ImageReplacedElementFactory implements ReplacedElementFactory {
    private static final Logger log = LoggerFactory.getLogger(ImageReplacedElementFactory.class);
    private final ReplacedElementFactory superFactory;

    private ApplicationContext appContext;

    private Set<String> images = new HashSet<>();

    public ImageReplacedElementFactory(ReplacedElementFactory superFactory, ApplicationContext appContext) {
        this.superFactory = superFactory;
        this.appContext = appContext;
        initImages();
    }

    private void initImages() {
        images.add("mail_logo_header");
        images.add("calendar");
        images.add("basket");
        images.add("planName");
        images.add("airline");
        images.add("cost");
        images.add("destination");
        images.add("mail");
        images.add("map");
        images.add("operator");
        images.add("pdf");
        images.add("phone");
        images.add("ship");
        images.add("underwriter");
        images.add("user");
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox,
                                                 UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {

        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }

        String nodeName = element.getNodeName();
        String className = element.getAttribute("class");
        if ("div".equals(nodeName) && images.contains(className)) {

            InputStream input = null;
            try {
                Resource resource = appContext.getResource("classpath:/static/images/mails/" + className + ".png");
                input = resource.getInputStream();
                byte[] bytes = IOUtils.toByteArray(input);
                Image image = Image.getInstance(bytes);
                FSImage fsImage = new ITextFSImage(image);

                if (fsImage != null) {
                    if ((cssWidth != -1) || (cssHeight != -1)) {
                        fsImage.scale(cssWidth, cssHeight);
                    }
                    return new ITextImageElement(fsImage);
                }
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            } catch (BadElementException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            } finally {
                IOUtils.closeQuietly(input);
            }
        }

        return superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    @Override
    public void reset() {
        superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        superFactory.setFormSubmissionListener(listener);
    }
}
