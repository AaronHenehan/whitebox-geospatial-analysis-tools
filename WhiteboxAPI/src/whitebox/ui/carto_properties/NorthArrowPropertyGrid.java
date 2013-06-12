/*
 * Copyright (C) 2013 johnlindsay
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

import whitebox.ui.NumericProperty;
import whitebox.ui.ColourProperty;
import whitebox.ui.BooleanProperty;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.swing.*;
import whitebox.cartographic.NorthArrow;
import whitebox.interfaces.WhiteboxPluginHost;

/**
 *
 * @author johnlindsay
 */
public class NorthArrowPropertyGrid extends JPanel implements PropertyChangeListener {
    
    private NorthArrow northArrow;
    private int rightMargin = 20;
    private int leftMargin = 10;
    private Color backColour = new Color(225, 245, 255);
    private WhiteboxPluginHost host = null;
    
    private ColourProperty outlineColourBox;
    private BooleanProperty northArrowVisible;
    private BooleanProperty backgroundVisible;
    private ColourProperty backgroundColourBox;
    private BooleanProperty borderVisible;
    private ColourProperty borderColour;
    private NumericProperty marginSize;
    private NumericProperty markerSize;
    private ResourceBundle bundle;
    
    public NorthArrowPropertyGrid() {
        createUI();
    }
    
    public NorthArrowPropertyGrid(NorthArrow northArrow, WhiteboxPluginHost host) {
        this.northArrow = northArrow;
        this.host = host;
        bundle = host.getGuiLabelsBundle();
        createUI();
    }

    public NorthArrow getNorthArrow() {
        return northArrow;
    }

    public void setNorthArrow(NorthArrow northArrow) {
        this.northArrow = northArrow;
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
            
            
            northArrowVisible = new BooleanProperty(bundle.getString("IsElementVisible"), 
                    northArrow.isVisible());
            northArrowVisible.setLeftMargin(leftMargin);
            northArrowVisible.setRightMargin(rightMargin);
            northArrowVisible.setBackColour(backColour);
            northArrowVisible.setPreferredWidth(preferredWidth);
            northArrowVisible.addPropertyChangeListener("value", this);
            northArrowVisible.revalidate();
            mainBox.add(northArrowVisible);

            markerSize = new NumericProperty(bundle.getString("MarginSize2"), 
                    String.valueOf(northArrow.getMarkerSize()));
            markerSize.setLeftMargin(leftMargin);
            markerSize.setRightMargin(rightMargin);
            markerSize.setBackColour(Color.WHITE);
            markerSize.setTextboxWidth(5);
            markerSize.setParseIntegersOnly(true);
            markerSize.setMinValue(1);
            markerSize.setMaxValue(250);
            markerSize.addPropertyChangeListener("value", this);
            markerSize.setPreferredWidth(preferredWidth);
            markerSize.revalidate();
            mainBox.add(markerSize);
            
            outlineColourBox = new ColourProperty(bundle.getString("OutlineColor"), 
                    northArrow.getOutlineColour());
            outlineColourBox.setLeftMargin(leftMargin);
            outlineColourBox.setRightMargin(rightMargin);
            outlineColourBox.setBackColour(backColour);
            outlineColourBox.setPreferredWidth(preferredWidth);
            outlineColourBox.revalidate();
            outlineColourBox.addPropertyChangeListener("value", this);
            mainBox.add(outlineColourBox);
            
            backgroundVisible = new BooleanProperty(bundle.getString("IsBackgroundVisible"), 
                    northArrow.isBackgroundVisible());
            backgroundVisible.setLeftMargin(leftMargin);
            backgroundVisible.setRightMargin(rightMargin);
            backgroundVisible.setBackColour(Color.WHITE);
            backgroundVisible.setPreferredWidth(preferredWidth);
            backgroundVisible.revalidate();
            backgroundVisible.addPropertyChangeListener("value", this);
            mainBox.add(backgroundVisible);
            
            backgroundColourBox = new ColourProperty(bundle.getString("BackgroundColor"), 
                    northArrow.getBackColour());
            backgroundColourBox.setLeftMargin(leftMargin);
            backgroundColourBox.setRightMargin(rightMargin);
            backgroundColourBox.setBackColour(backColour);
            backgroundColourBox.setPreferredWidth(preferredWidth);
            backgroundColourBox.revalidate();
            backgroundColourBox.addPropertyChangeListener("value", this);
            mainBox.add(backgroundColourBox);
            
            borderVisible = new BooleanProperty(bundle.getString("IsBorderVisible"), 
                    northArrow.isBorderVisible());
            borderVisible.setLeftMargin(leftMargin);
            borderVisible.setRightMargin(rightMargin);
            borderVisible.setBackColour(Color.WHITE);
            borderVisible.setPreferredWidth(preferredWidth);
            borderVisible.revalidate();
            borderVisible.addPropertyChangeListener("value", this);
            mainBox.add(borderVisible);
            
            borderColour = new ColourProperty(bundle.getString("BorderColor"), 
                    northArrow.getBorderColour());
            borderColour.setLeftMargin(leftMargin);
            borderColour.setRightMargin(rightMargin);
            borderColour.setBackColour(backColour);
            borderColour.setPreferredWidth(preferredWidth);
            borderColour.revalidate();
            borderColour.addPropertyChangeListener("value", this);
            mainBox.add(borderColour);
            
            marginSize = new NumericProperty(bundle.getString("MarginSize2"), 
                    String.valueOf(northArrow.getMargin()));
            marginSize.setLeftMargin(leftMargin);
            marginSize.setRightMargin(rightMargin);
            marginSize.setBackColour(Color.WHITE);
            marginSize.setTextboxWidth(5);
            marginSize.setParseIntegersOnly(true);
            marginSize.addPropertyChangeListener("value", this);
            marginSize.setPreferredWidth(preferredWidth);
            marginSize.revalidate();
            mainBox.add(marginSize);
            
            super.revalidate();
        } catch (Exception e) {
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
            northArrow.setOutlineColour(outlineColourBox.getValue());
            didSomething = true;
        } else if (source == markerSize) {
            northArrow.setMarkerSize(Integer.parseInt((String) evt.getNewValue()));
            didSomething = true;
        } else if (source == northArrowVisible) {
            northArrow.setVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == backgroundColourBox) {
            northArrow.setBackColour(backgroundColourBox.getValue());
            didSomething = true;
        } else if (source == backgroundVisible) {
            northArrow.setBackgroundVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == borderVisible) {
            northArrow.setBorderVisible((Boolean) evt.getNewValue());
            didSomething = true;
        } else if (source == borderColour) {
            northArrow.setBorderColour((Color) evt.getNewValue());
            didSomething = true;
        } else if (source == marginSize) {
            northArrow.setMargin(Integer.parseInt((String) evt.getNewValue()));
            didSomething = true;
        }

        if (didSomething && host != null) {
            host.refreshMap(false);
        }
    }
}
