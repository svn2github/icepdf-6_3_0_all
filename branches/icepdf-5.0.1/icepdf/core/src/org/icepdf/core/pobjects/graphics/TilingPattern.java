/*
 * Copyright 2006-2014 ICEsoft Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.icepdf.core.pobjects.graphics;

import org.icepdf.core.io.SeekableInputConstrainedWrapper;
import org.icepdf.core.pobjects.Name;
import org.icepdf.core.pobjects.Resources;
import org.icepdf.core.pobjects.Stream;
import org.icepdf.core.pobjects.graphics.commands.ColorDrawCmd;
import org.icepdf.core.util.Defs;
import org.icepdf.core.util.Library;
import org.icepdf.core.util.content.ContentParser;
import org.icepdf.core.util.content.ContentParserFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p><i>Tiling patterns</i> consist of a small graphical figure (called a
 * pattern cell) that is replicated at fixed horizontal and vertical
 * intervals to fill the area to be painted. The graphics objects to
 * use for tiling are described by a content stream. (PDF 1.2)
 *
 * @author ICEsoft Technologies Inc.
 * @since 3.0
 */
public class TilingPattern extends Stream implements Pattern {

    private static final Logger logger =
            Logger.getLogger(TilingPattern.class.toString());

    public static final Name PATTERNTYPE_KEY = new Name("PatternType");
    public static final Name PAINTTYPE_KEY = new Name("PaintType");
    public static final Name TILINGTYPE_KEY = new Name("TilingType");
    public static final Name BBOX_KEY = new Name("BBox");
    public static final Name XSTEP_KEY = new Name("XStep");
    public static final Name YSTEP_KEY = new Name("YStep");
    public static final Name MATRIX_KEY = new Name("Matrix");
    public static final Name RESOURCES_KEY = new Name("Resources");

    // change the the interpolation and anti-aliasing settings.
    private static RenderingHints renderingHints;

    static {
        Object antiAliasing = RenderingHints.VALUE_ANTIALIAS_OFF;
        Object interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        String property = Defs.sysProperty("org.icepdf.core.tiling.antiAliasing");
        if (property != null) {
            if (property.equalsIgnoreCase("VALUE_ANTIALIAS_DEFAULT")) {
                antiAliasing = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
            } else if (property.equalsIgnoreCase("VALUE_ANTIALIAS_ON")) {
                antiAliasing = RenderingHints.VALUE_ANTIALIAS_ON;
            } else if (property.equalsIgnoreCase("VALUE_ANTIALIAS_OFF")) {
                antiAliasing = RenderingHints.VALUE_ANTIALIAS_OFF;
            }
        }
        property = Defs.sysProperty("org.icepdf.core.tiling.interpolation");
        if (property != null) {
            if (property.equalsIgnoreCase("VALUE_INTERPOLATION_BICUBIC")) {
                interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            } else if (property.equalsIgnoreCase("VALUE_INTERPOLATION_BILINEAR")) {
                interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            } else if (property.equalsIgnoreCase("VALUE_INTERPOLATION_NEAREST_NEIGHBOR")) {
                interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            }
        }
        renderingHints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                interpolation);
        renderingHints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, antiAliasing));
    }


    // A code identifying the type of pattern that this dictionary describes
    private int patternType;

    // A code that determines how the color of the pattern cell is to be specified
    private int paintType;

    // uncolored tiling pattern colour, if specified.
    private Color unColored;

    /**
     * Colored tiling pattern. The pattern's content stream itself specifies the
     * colors used to paint the pattern cell. When the content stream begins
     * execution, the current color is the one that was initially in effect in
     * the pattern's parent content stream.
     */
    public static final int PAINTING_TYPE_COLORED_TILING_PATTERN = 1;

    /**
     * Uncolored tiling pattern. The pattern's content stream does not specify
     * any color information. Instead, the entire pattern cell is painted with a
     * separately specified color each time the pattern is used. Essentially,
     * the content stream describes a stencil through which the current color is
     * to be poured. The content stream must not invoke operators that specify
     * colors or other color-related parameters in the graphics state;
     * otherwise, an error will occur
     */
    public static final int PAINTING_TYPE_UNCOLORED_TILING_PATTERN = 2;

    // A code that controls adjustments to the spacing of tiles relative to the
    // device pixel grid
    private int tilingType;

    // type of PObject, should always be "Pattern"
    private Name type;

    /**
     * Spacing of tiles relative to the device grid: Pattern cells are spaced
     * consistently-that is, by a multiple of a device pixel. To achieve this,
     * the viewer application may need to distort the pattern cell slightly by
     * making small adjustments to XStep, YStep, and the transformation matrix.
     * The amount of distortion does not exceed 1 device pixel.
     */
    public static final int TILING_TYPE_CONSTANT_SPACING = 1;

    /**
     * The pattern cell is not
     * distorted, but the spacing between pattern cells may vary by as much as
     * 1 device pixel, both horizontally and vertically, when the pattern is
     * painted. This achieves the spacing requested by XStep and YStep on
     * average, but not necessarily for each individual pattern cell.
     */
    public static final int TILING_TYPE_NO_DISTORTION = 2;

    /**
     * Pattern cells are spaced consistently as in tiling type 1, but with
     * additional distortion permitted to enable a more efficient implementation.
     */
    public static final int TILING_TYPE_CONSTANT_SPACING_FASTER = 3;

    // An array of four numbers in the pattern coordinate system giving the
    // coordinates of the left, bottom, right, and top edges, respectively, of
    // the pattern cell's bounding box. These boundaries are used to clip the
    // pattern cell.
    private Rectangle2D bBox;
    private Rectangle2D bBoxMod;

    // The desired horizontal spacing between pattern cells, measured in the
    // pattern coordinate system.
    private float xStep;

    // The desired vertical spacing between pattern cells, measured in the
    // pattern coordinate system. Note that XStep and YStep may differ from the
    // dimensions of the pattern cell implied by the BBox entry. This allows
    // tiling with irregularly shaped figures. XStep and YStep may be either
    // positive or negative, but not zero.
    private float yStep;

    // A resource dictionary containing all of the named resources required by
    // the pattern's content stream
    private Resources resources;

    // An array of six numbers specifying the pattern matrix. The default value
    // is the identity matrix [1 0 0 1 0 0].
    private AffineTransform matrix;

    // Parsed resource data is stored here.
    private Shapes shapes;

    // Fill colour
    public Color fillColour = null;

    //  initiated flag
    private boolean inited;

    private GraphicsState parentGraphicState;

    // cached pattern paint
    private TexturePaint patternPaint;

    public TilingPattern(Stream stream) {
        super(stream.getLibrary(), stream.getEntries(), stream.getRawBytes());
        initiParams();
    }

    /**
     * @param l
     * @param h
     * @param streamInputWrapper
     */
    public TilingPattern(Library l, HashMap h, SeekableInputConstrainedWrapper streamInputWrapper) {
        super(l, h, streamInputWrapper);
        initiParams();
    }

    private void initiParams() {
        type = library.getName(entries, TYPE_KEY);

        patternType = library.getInt(entries, PATTERNTYPE_KEY);

        paintType = library.getInt(entries, PAINTTYPE_KEY);

        tilingType = library.getInt(entries, TILINGTYPE_KEY);

        bBox = library.getRectangle(entries, BBOX_KEY);

        xStep = library.getFloat(entries, XSTEP_KEY);

        yStep = library.getFloat(entries, YSTEP_KEY);

        List v = (List) library.getObject(entries, MATRIX_KEY);
        if (v != null) {
            matrix = getAffineTransform(v);
        } else {
            // default is identity matrix
            matrix = new AffineTransform();
        }
    }

    public Name getType() {
        return type;
    }

    /**
     * Utility method for parsing a vector of affine transform values to an
     * affine transform.
     *
     * @param v vector containing affine transform values.
     * @return affine tansform based on v
     */
    private static AffineTransform getAffineTransform(List v) {
        float f[] = new float[6];
        for (int i = 0; i < 6; i++) {
            f[i] = ((Number) v.get(i)).floatValue();
        }
        return new AffineTransform(f);
    }

    /*
     * Since tiling patterns are still not fully supported we need  to make
     * a best guess at which colour to use for stroking or non stroking
     * operations
     */

    public Color getFirstColor() {
        // find and cache first colour found in stack
        if (shapes != null && unColored == null) {
            for (int i = 0, max = shapes.shapes.size(); i < max; i++) {
                if (shapes.shapes.get(i) instanceof ColorDrawCmd) {
                    ColorDrawCmd tmp = (ColorDrawCmd) shapes.shapes.get(i);
                    unColored = tmp.getColor();
                    return unColored;
                }
            }
        }
        // if now shapes then we go with black.
        if (unColored == null) {
            unColored = Color.black;
            return unColored;
        } else {
            return unColored;
        }
    }

    /**
     *
     */
    public synchronized void init() {

        if (inited) {
            return;
        }
        inited = true;

        // try and find the form's resources dictionary.
        Resources leafResources = library.getResources(entries, RESOURCES_KEY);
        // apply resource for tiling if any, otherwise we use the default dictionary.
        if (leafResources != null) {
            resources = leafResources;
        }

        if (paintType == PAINTING_TYPE_UNCOLORED_TILING_PATTERN) {
            parentGraphicState.setFillColor(unColored);
            parentGraphicState.setStrokeColor(unColored);
        }

        // Build a new content parser for the content streams and apply the
        // content stream of the calling content stream.
        ContentParser cp = ContentParserFactory.getInstance()
                .getContentParser(library, resources);
        cp.setGraphicsState(parentGraphicState);
        try {
            shapes = cp.parse(new byte[][]{getDecodedStreamBytes()}).getShapes();
        } catch (Throwable e) {
            logger.log(Level.FINE, "Error processing tiling pattern.", e);
        }

        // some encoders set the step to 2^15
        if (xStep == Short.MAX_VALUE) {
            xStep = (float) bBox.getWidth();
        }
        if (yStep == Short.MAX_VALUE) {
            yStep = (float) bBox.getHeight();
        }
        // adjust the bBox so that xStep and yStep can be applied
        // for tile spacing.
        bBoxMod = new Rectangle2D.Double(
                bBox.getX(), bBox.getY(),
                bBox.getWidth() == xStep ? bBox.getWidth() : xStep,
                bBox.getHeight() == yStep ? bBox.getHeight() : yStep);
    }

    /**
     * Applies the pattern paint specified by this TilingPattern instance.
     * Handles both uncoloured and coloured pattern types.
     *
     * @param g    graphics context to apply textured paint too.
     * @param base base transform before painting started, generally the page
     *             space transform of the page.
     */
    public void paintPattern(Graphics2D g, final AffineTransform base) {
        if (patternPaint == null) {

            // base represents the current page transform, zoom and rotation,
            // the g.getTransform() will be the state of the current graphics
            // state.  The matrix however maps from the original page space to
            // pattern space.  As a result we need draw the tile pattern
            // appropriately as to take into account the g.getTransform(), as
            // we can't scale the pattern space easily.

            AffineTransform originalPageSpace;
            try {
                originalPageSpace = new AffineTransform(base);
                originalPageSpace.concatenate(g.getTransform().createInverse());
            } catch (NoninvertibleTransformException e) {
                logger.warning("Error creating Tiling pattern transform. ");
                originalPageSpace = new AffineTransform();
            }

            // convert to original page space
            Rectangle2D bBoxMod = matrix.createTransformedShape(this.bBoxMod).getBounds2D();
            // scale to the current state of g2d.
            bBoxMod = originalPageSpace.createTransformedShape(bBoxMod).getBounds2D();

            int width = (int) bBoxMod.getWidth();
            int height = (int) bBoxMod.getHeight();

            // corner cases where some bBoxes don't have a dimension.
            if (width == 0) {
                width = 1;
            }
            if (height == 0) {
                height = 1;
            }

            // create the new image to write too.
            final BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D canvas = bi.createGraphics();

            // create the pattern paint before  we paint encase there
            // is some recursive calls during the paint PDF-436
//            patternPaint = new TexturePaint(bi, bBoxMod);
            patternPaint = new TexturePaint(bi, new Rectangle2D.Double(
                    0, 0, bBoxMod.getWidth(), bBoxMod.getHeight()));
            g.setPaint(patternPaint);

            // apply current hints
            canvas.setRenderingHints(renderingHints);
            // copy over the rendering hints
            // get shapes and paint them.
            final Shapes tilingShapes = getShapes();

            // add clip for bBoxMod, needed for some shapes painting.
            canvas.setClip(0, 0, (int) bBoxMod.getWidth(), (int) bBoxMod.getHeight());

            // paint the pattern
            paintPattern(canvas, tilingShapes, originalPageSpace);

            // show it in a frame
//            final JFrame f = new JFrame(this.toString());
//            final Rectangle2D bbox2 = bBoxMod;
//            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//            f.getContentPane().add(new JComponent() {
//                @Override
//                public void paint(Graphics g_) {
//                    super.paint(g_);
//                    Graphics2D g2d = (Graphics2D) g_;
//                    // draw the tile image buffer.
//                    g2d.drawImage(bi,0,0,null);
//                    g2d.setColor(Color.GREEN);
//                    g2d.drawRect(0,0, (int)bbox2.getWidth(), (int)bbox2.getHeight());
//                }
//            });
//            f.setSize(new Dimension(800, 800));
//            f.setVisible(true);
            // post paint cleanup
            canvas.dispose();
            bi.flush();
        } else {
            g.setPaint(patternPaint);
        }
    }

    private void paintPattern(Graphics2D g2d, Shapes tilingShapes, AffineTransform base) {

        // store previous state so we can draw bounds
        AffineTransform preAf = g2d.getTransform();

        // apply scale an shear but not translation
        AffineTransform af = new AffineTransform(matrix);
        AffineTransform af2 = new AffineTransform(base);
        af2.concatenate(af);
        af2 = new AffineTransform(
                af2.getScaleX(),
                af2.getShearY(),
                af2.getShearX(),
                af2.getScaleY(),
                0,
                0);
        g2d.setTransform(af2);

        // pain the key pattern
        AffineTransform prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        // build the the tile
        g2d.translate(xStep, 0);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(0, -yStep);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(-xStep, 0);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(-xStep, 0);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(0, yStep);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(0, yStep);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(xStep, 0);
        prePaint = g2d.getTransform();
        tilingShapes.paint(g2d);
        g2d.setTransform(prePaint);

        g2d.translate(xStep, 0);
        tilingShapes.paint(g2d);

        // highlight key square.
//        g2d.setTransform(prePaint);
//        g2d.setColor(Color.red);
//        // direction line and bounding box
//        g2d.fillRect((int)bBox.getX(), (int)bBox.getY(), 10,10);
//        g2d.drawRect((int)bBox.getX(), (int)bBox.getY(), (int)bBox.getWidth()-1,(int)bBox.getHeight()-1);
//        // Axis lines
//        g2d.drawLine(0, 0, 400,400);
//        g2d.setColor(Color.BLACK);
//        g2d.drawLine(-400, 0, 400, 0);
//        g2d.setColor(Color.BLUE);
//        g2d.drawLine(0,400, 0,-400);

        g2d.setTransform(preAf);
    }


    public Paint getPaint() {
        return patternPaint;
    }

    public int getPatternType() {
        return patternType;
    }

    public void setPatternType(int patternType) {
        this.patternType = patternType;
    }

    public int getPaintType() {
        return paintType;
    }

    public void setPaintType(int paintType) {
        this.paintType = paintType;
    }

    public int getTilingType() {
        return tilingType;
    }

    public void setTilingType(int tilingType) {
        this.tilingType = tilingType;
    }

    public Rectangle2D getBBox() {
        return bBox;
    }

    /**
     * Bbox converted to user space.
     *
     * @return bBox converted to user space.
     */
    public Rectangle2D getbBoxMod() {
        return bBoxMod;
    }

    public float getXStep() {
        return xStep;
    }

    public float getYStep() {
        return yStep;
    }

    public AffineTransform getMatrix() {
        return matrix;
    }

    public AffineTransform getInvMatrix() {
        try {
            return matrix.createInverse();
        } catch (NoninvertibleTransformException e) {

        }
        return null;
    }

    public void setMatrix(AffineTransform matrix) {
        this.matrix = matrix;
    }

    public Shapes getShapes() {
        return shapes;
    }

    public void setShapes(Shapes shapes) {
        this.shapes = shapes;
    }


    public void setParentGraphicState(GraphicsState graphicsState) {
        this.parentGraphicState = graphicsState;
    }

    public GraphicsState getParentGraphicState() {
        return parentGraphicState;
    }

    public Color getUnColored() {
        return unColored;
    }

    public void setUnColored(Color unColored) {
        this.unColored = unColored;
    }

    /**
     * @return
     */
    public String toString() {
        return "Tiling Pattern: \n" +
                "           type: pattern " +
                "\n    patternType: tilling" +
                "\n      paintType: " + (paintType == PAINTING_TYPE_COLORED_TILING_PATTERN ? "colored" : "uncoloured") +
                "\n    tilingType: " + tilingType +
                "\n          bbox: " + bBox +
                "\n         xStep: " + xStep +
                "\n         yStep: " + yStep +
                "\n      resource: " + resources +
                "\n        matrix: " + matrix;
    }
}
