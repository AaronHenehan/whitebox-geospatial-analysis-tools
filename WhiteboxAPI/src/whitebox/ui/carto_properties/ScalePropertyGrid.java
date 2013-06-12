/*
 * Copyright (C) 2012 johnlindsay
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package whitebox.ui.carto_properties;

import whitebox.ui.StringProperty;
import whitebox.ui.NumericProperty;
import whitebox.ui.ColourProperty;
import whitebox.ui.FontProperty;
import whitebox.ui.BooleanProperty;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import whitebox.cartographic.MapScale;
import whitebox.interfaces.WhiteboxPluginHost;

/**
 *
 * @author johnlindsay
 */
public class ScalePropertyGrid extends JPanel implements PropertyChangeListener  {
    
    private MapScale mapScale;
    private int rightMargin = 10;
    private int leftMargin = 10;
    private Color backColour = new Color(225, 245, 255);
    private WhiteboxPluginHost host = null;
    
    private StringProperty scaleUnits;
    private ColourProperty outlineColourBox;
    private BooleanProperty scaleVisible;
    private BooleanProperty scaleRepFracVisible;
    private ColourProperty borderColourBox;
    private BooleanProperty graphicalScaleVisible;
    private BooleanProperty backgroundVisible;
    private ColourProperty backgroundColourBox;
    private BooleanProperty borderVisible;
    private NumericProperty marginSize;
    private NumericProperty scaleWidth;
    private NumericProperty scaleHeight;
    private FontProperty fontProperty;
    private NumericProperty scaleStyle;
    private ResourceBundle bundle;
    
    public ScalePropertyGrid() {
        createUI();
    }
    
    public ScalePropertyGrid(MapScale mapScale, WhiteboxPluginHost host) {
        this.mapScale = mapScale;
        this.host = host;
        bundle = host.getGuiLabelsBundle();
        createUI();
    }

    public MapScale getMapScale() {
        return mapScale;
    }

    public void setMapScale(MapScale mapScale) {
        this.mapScale = mapScale;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Color getBackColour() {
        return backColour;
    }

    public void setBackColour(Color backColour) {
        this.backColour = backColour;
    }

    public WhiteboxPluginHost getHost() {
        return host;
    }

    public void setHost(WhiteboxPluginHost host) {
        this.host = host;
    }
    
    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    public final void createUI() {
        try {
            
            if (bundle == null) { return; }
            this.setBackground(Color.WHITE);
            
            Box mainBox = Box.createVerticalBox();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            int preferredWidth = 470;
            this.add(mainBox);
            
            scaleVisible = new BooleanProperty(bundle.getString("IsElementVisible"), 
                    mapScale.isVisible());
            scaleVisible.setLeftMargin(leftMargin);
            scaleVisible.setRightMargin(rightMargin);
            scaleVisible.setBackColour(backColour);
            scaleVisible.setPreferredWidth(preferredWidth);
            scaleVisible.addPropertyChangeListener("value", this);
            scaleVisible.revalidate();
            mainBox.add(scaleVisible);
            
            backgroundVisible = new BooleanProperty(bundle.getString("IsBackgroundVisible"), 
                    mapScale.isBackgroundVisible());
            backgroundVisible.setLeftMargin(leftMargin);
            backgroundVisible.setRightMargin(rightMargin);
            backgroundVisible.setBackColour(Color.WHITE);
            backgroundVisible.setPreferredWidth(preferredWidth);
            backgroundVisible.revalidate();
            backgroundVisible.addPropertyChangeListener("value", this);
            mainBox.add(backgroundVisible);
            
            backgroundColourBox = new ColourProperty(bundle.getString("BackgroundColor"), 
                    mapScale.getBackColour());
            backgroundColourBox.setLeftMargin(leftMargin);
            backgroundColourBox.setRightMargin(rightMargin);
            backgroundColourBox.setBackColour(backColour);
            backgroundColourBox.setPreferredWidth(preferredWidth);
            backgroundColourBox.revalidate();
            backgroundColourBox.addPropertyChangeListener("value", this);
            mainBox.add(backgroundColourBox);
            
            borderVisible = new BooleanProperty(bundle.getString("IsBorderVisible"), 
                    mapScale.isBorderVisible());
            borderVisible.setLeftMargin(leftMargin);
            borderVisible.setRightMargin(rightMargin);
            borderVisible.setBackColour(Color.WHITE);
            borderVisible.setPreferredWidth(preferredWidth);
            borderVisible.revalidate();
            borderVisible.addPropertyChangeListener("value", this);
            mainBox.add(borderVisible);

            borderColourBox = new ColourProperty(bundle.getString("BorderColor"), 
                    mapScale.getFontColour());
            borderColourBox.setLeftMargin(leftMargin);
            borderColourBox.setRightMargin(rightMargin);
            borderColourBox.setBackColour(backColour);
            borderColourBox.setPreferredWidth(preferredWidth);
            borderColourBox.revalidate();
            borderColourBox.addPropertyChangeListener("value", this);
            mainBox.add(borderColourBox);
            
            
            // scale units
            scaleUnits = new StringProperty(bundle.getString("ScaleUnits"), 
                    mapScale.getUnits());
            scaleUnits.setLeftMargin(leftMargin);
            scaleUnits.setRightMargin(rightMargin);
            scaleUnits.setBackColour(Color.WHITE);
            scaleUnits.setPreferredWidth(preferredWidth);
            scaleUnits.revalidate();
            scaleUnits.addPropertyChangeListener("value", this);
            mainBox.add(scaleUnits);
            
            // scale width
            scaleWidth = new NumericProperty(bundle.getString("Width"), 
                    String.valueOf(mapScale.getWidth()));
            scaleWidth.setLeftMargin(leftMargin);
            scaleWidth.setRightMargin(rightMargin);
            scaleWidth.setBackColour(backColour);
            scaleWidth.setPreferredWidth(preferredWidth);
            scaleWidth.setParseIntegersOnly(true);
            scaleWidth.setTextboxWidth(5);
            scaleWidth.revalidate();
            scaleWidth.addPropertyChangeListener("value", this);
            mainBox.add(scaleWidth);
            
            // scale height
            scaleHeight = new NumericProperty(bundle.getString("Height"), 
                    String.valueOf(mapScale.getHeight()));
            scaleHeight.setLeftMargin(leftMargin);
            scaleHeight.setRightMargin(rightMargin);
            scaleHeight.setBackColour(Color.WHITE);
            scaleHeight.setPreferredWidth(preferredWidth);
            scaleHeight.setParseIntegersOnly(true);
            scaleHeight.setTextboxWidth(5);
            scaleHeight.revalidate();
            scaleHeight.addPropertyChangeListener("value", this);
            mainBox.add(scaleHeight);
            
            // scale margin
            marginSize = new NumericProperty(bundle.getString("MarginSize2"), 
                    String.valueOf(mapScale.getMargin()));
            marginSize.setLeftMargin(leftMargin);
            marginSize.setRightMargin(rightMargin);
            marginSize.setBackColour(backColour);
            marginSize.setTextboxWidth(5);
            marginSize.setParseIntegersOnly(true);
            marginSize.addPropertyChangeListener("value", this);
            marginSize.setPreferredWidth(preferredWidth);
            marginSize.revalidate();
            mainBox.add(marginSize);
            
            // scale representative fraction
            scaleRepFracVisible = new BooleanProperty(bundle.getString("ShowRepresentativeFraction"), 
                    mapScale.isBorderVisible());
            scaleRepFracVisible.setLeftMargin(leftMargin);
            scaleRepFracVisible.setRightMargin(rightMargin);
            scaleRepFracVisible.setBackColour(Color.WHITE);
            scaleRepFracVisible.setPreferredWidth(preferredWidth);
            scaleRepFracVisible.revalidate();
            scaleRepFracVisible.addPropertyChangeListener("value", this);
            mainBox.add(scaleRepFracVisible);
            
            outlineColourBox = new ColourProperty(bundle.getString("OutlineColor"), 
                    mapScale.getOutlineColour());
            outlineColourBox.setLeftMargin(leftMargin);
            outlineColourBox.setRightMargin(rightMargin);
            outlineColourBox.setBackColour(backColour);
            outlineColourBox.setPreferredWidth(preferredWidth);
            outlineColourBox.revalidate();
            outlineColourBox.addPropertyChangeListener("value", this);
            mainBox.add(outlineColourBox);
            
            fontProperty = new FontProperty(bundle.getString("Font"), mapScale.getLabelFont());
            fontProperty.setLeftMargin(leftMargin);
            fontProperty.setRightMargin(rightMargin);
            fontProperty.setBackColour(Color.WHITE);
            fontProperty.setTextboxWidth(15);
            fontProperty.setPreferredWidth(preferredWidth);
            fontProperty.addPropertyChangeListener("value", this);
            fontProperty.revalidate();
            mainBox.add(fontProperty);
            
            graphicalScaleVisible = new BooleanProperty(bundle.getString("IsGraphicalScaleVisible"), 
                    mapScale.isGraphicalScaleVisible());
            graphicalScaleVisible.setLeftMargin(leftMargin);
            graphicalScaleVisible.setRightMargin(rightMargin);
            graphicalScaleVisible.setBackColour(backColour);
            graphicalScaleVisible.setPreferredWidth(preferredWidth);
            graphicalScaleVisible.addPropertyChangeListener("value", this);
            graphicalScaleVisible.revalidate();
            mainBox.add(graphicalScaleVisible);
            
            // scale style
            
//            String[] styles = { "Standard", "Simple", "Complex" };
//            scaleStyle = new ComboBoxProperty("Scale style:", styles, mapScale.getScaleStyle().ordinal());
//            scaleStyle.setLeftMargin(leftMargin);
//            scaleStyle.setRightMargin(rightMargin);
//            scaleStyle.setBackColour(Color.WHITE);
//            scaleStyle.setPreferredWidth(preferredWidth);
//            scaleStyle.revalidate();
//            ItemListener il = new ItemListener() {
//
//                @Override
//                public void itemStateChanged(ItemEvent e) {
//                    if (e.getStateChange() == ItemEvent.SELECTED) {
//                    Object item = e.getItem();
//                    //setValue(item.toString());
//                }
//                }
//            };
//            scaleStyle.parentListener = il;
//            
//            scaleStyle.addPropertyChangeListener("value", this);
//            mainBox.add(scaleStyle);
            
            scaleStyle = new NumericProperty(bundle.getString("ScaleStyle"), 
                    String.valueOf(mapScale.getScaleStyle().ordinal() + 1));
            scaleStyle.setLeftMargin(leftMargin);
            scaleStyle.setRightMargin(rightMargin);
            scaleStyle.setBackColour(Color.WHITE);
            scaleStyle.setPreferredWidth(preferredWidth);
            scaleStyle.setParseIntegersOnly(true);
            scaleStyle.setMinValue(1);
            scaleStyle.setMaxValue(3);
            scaleStyle.revalidate();
            scaleStyle.addPropertyChangeListener("value", this);
            mainBox.add(scaleStyle);
            
            
            super.revalidate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //host.showFeedback(e.getMessage());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        Boolean didSomething = false;
        if (!evt.getPropertyName().equals("value")) {
            return;
        }
        if (source == outlineColourBox) {
            mapScale.setOutlineColour(outlineColourBox.getValue());
            didSomething = true;
        } else if (source == scaleWidth) {
            mapScale.setWidth(Integer.parseInt((String) evt.getNewValue()));
            didSomething = true;
        } else if (source == scaleHeight) {
            mapScale.setHeight(Integer.parseInt((String) evt.getNewValue()));
            didSomething = true;
        } else if (source == scaleVisible) {
            mapScale.setVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == scaleRepFracVisible) {
            mapScale.setRepresentativeFractionVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == borderColourBox) {
            mapScale.setBorderColour(borderColourBox.getValue());
            didSomething = true;
        } else if (source == scaleUnits) {
            mapScale.setUnits(evt.getNewValue().toString());
            didSomething = true;
        } else if (source == backgroundColourBox) {
            mapScale.setBackColour(backgroundColourBox.getValue());
            didSomething = true;
        } else if (source == backgroundVisible) {
            mapScale.setBackgroundVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == borderVisible) {
            mapScale.setBorderVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == marginSize) {
            mapScale.setMargin(Integer.parseInt((String) evt.getNewValue()));
            didSomething = true;
        } else if (source == fontProperty) {
            mapScale.setLabelFont((Font)evt.getNewValue());
            didSomething = true;
        } else if (source == graphicalScaleVisible) {
            mapScale.setGraphicalScaleVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == scaleStyle) {
            int style = Integer.parseInt((String) evt.getNewValue());
            switch (style) {
                case 1:
                    mapScale.setScaleStyle(MapScale.ScaleStyle.STANDARD);
                    break;
                case 2:
                    mapScale.setScaleStyle(MapScale.ScaleStyle.SIMPLE);
                    break;
                case 3:
                    mapScale.setScaleStyle(MapScale.ScaleStyle.COMPLEX);
                    break;
            }
            didSomething = true;
        }

        if (didSomething && host != null) {
            host.refreshMap(false);
        }
    }
}
