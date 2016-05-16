package com.rohit.imageresizer;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rohit on 28/4/16.
 */
public class ImageResizer {
    /**
     * A static method to create the thumbnail of the mentioned sizes
     * @param image represents the original image from where the thumbnail is to be created
     * @param newWidth represents the width
     * @param newHeight represents the height
     */
    public static BufferedImage getScaledInstance(BufferedImage image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        boolean isTranslucent = image.getType() != Transparency.OPAQUE;
        Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
        hintsMap.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hintsMap.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (newWidth >= width || newHeight >= height)
        {
            int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
//            throw new IllegalArgumentException("newWidth and newHeight cannot be greater than the image dimensions");
            BufferedImage ret = image;
            int w, h;
            w = newWidth;
            h = newHeight;

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHints(hintsMap);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
            return ret;

        }
        else if (newWidth <= 0 || newHeight <= 0)
        {
            throw new IllegalArgumentException("newWidth and newHeight must be greater than 0");
        }

        BufferedImage thumb = image;
        BufferedImage temp = null;
        Graphics2D g2 = null;

        try
        {
            int previousWidth = width;
            int previousHeight = height;
            do
            {
                if (width > newWidth)
                {
                    width /= 2;
                    if (width < newWidth)
                    {
                        width = newWidth;
                    }
                }
                if (height > newHeight)
                {
                    height /= 2;
                    if (height < newHeight)
                    {
                        height = newHeight;
                    }
                }
                if (temp == null || isTranslucent)
                {
                    if (g2 != null)
                    {
                        //do not need to wrap with finally
                        //outer finally block will ensure
                        //that resources are properly reclaimed
                        g2.dispose();
                    }
                    temp = createCompatibleImage(image, width, height);
                    g2 = temp.createGraphics();
                    g2.setRenderingHints(hintsMap);
                }
                g2.drawImage(thumb, 0, 0, width, height,0, 0, previousWidth, previousHeight, null);
                previousWidth = width;
                previousHeight = height;
                thumb = temp;
            } while (width != newWidth || height != newHeight);
        } finally
        {
            g2.dispose();
        }
        if (width != thumb.getWidth() || height != thumb.getHeight())
        {
            temp = createCompatibleImage(image, width, height);
            g2 = temp.createGraphics();
            try
            {
                g2.setRenderingHints(hintsMap);
                g2.drawImage(thumb, 0, 0, width, height, 0, 0, width, height, null);
            }
            finally
            {
                g2.dispose();
            }
            thumb = temp;
        }
        //ImageIO.write(thumb, destFile.substring(destFile.lastIndexOf('.')+1), new FileOutputStream(destFile));
        return thumb;

    }

    private static BufferedImage createCompatibleImage(BufferedImage image,int width, int height) {

        /*BufferedImage temp = null;
        if(isHeadless()){
            temp = new BufferedImage(width, height, image.getType());
        } else {
            temp = getGraphicsConfiguration().createCompatibleImage(width, height);
        }*/
        //return isHeadless()?new BufferedImage(width, height, image.getType()):getGraphicsConfiguration().createCompatibleImage(width, height);
        return new BufferedImage(width, height, image.getType());
    }

    // Returns the graphics configuration for the primary screen
    @SuppressWarnings("unused")
	private static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    @SuppressWarnings("unused")
    private static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    /**
     * Resizing the image based on the aspect ratio.
     * Suggestible to keep the aspect ratio of the image for better image quality and no distortion
     * @param image, the image to be resized
     * @param destHeight, desired height of the image
     * @param fixHeight, the desired height will be fixed, will not take any aspect ratio if false
     * @param destWidth, desired width of the image
     * @param fixWidth, the desired width will be fixed, will not take any aspect ratio if false
     * @return
     */
    public static BufferedImage resizeImageWithAspectRatio (BufferedImage image, int destWidth, Boolean fixWidth, int destHeight, Boolean fixHeight){
        int height = image.getHeight();
        int width = image.getWidth();
        if(fixHeight){
            destWidth = (destHeight * width) / height;
        }
        if (fixWidth){
            destHeight = (height * destWidth) / width;
        }

        return getScaledInstance(image, destWidth, destHeight);
    }
}