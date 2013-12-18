/*
 * Copyright (C) 2011-2012 Dr. John Lindsay <jlindsay@uoguelph.ca>
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
package plugins;

import java.util.Date;
import whitebox.geospatialfiles.WhiteboxRaster;
import whitebox.geospatialfiles.WhiteboxRasterInfo;
import whitebox.interfaces.WhiteboxPlugin;
import whitebox.interfaces.WhiteboxPluginHost;

/**
 * WhiteboxPlugin is used to define a plugin tool for Whitebox GIS.
 * @author johnlindsay
 */
public class CreateColourComposite implements WhiteboxPlugin {
    
    private WhiteboxPluginHost myHost = null;
    private String[] args;
    /**
     * Used to retrieve the plugin tool's name. This is a short, unique name containing no spaces.
     * @return String containing plugin name.
     */
    @Override
    public String getName() {
        return "CreateColourComposite";
    }
    /**
     * Used to retrieve the plugin tool's descriptive name. This can be a longer name (containing spaces) and is used in the interface to list the tool.
     * @return String containing the plugin descriptive name.
     */
    @Override
    public String getDescriptiveName() {
    	return "Create Colour Composite";
    }
    /**
     * Used to retrieve a short description of what the plugin tool does.
     * @return String containing the plugin's description.
     */
    @Override
    public String getToolDescription() {
    	return "This tool creates an RGBa colour composite image from multispectral data.";
    }
     /**
     * Used to identify which toolboxes this plugin tool should be listed in.
     * @return Array of Strings.
     */
    @Override
    public String[] getToolbox() {
    	String[] ret = { "ImageProc" };
    	return ret;
    }
     /**
     * Sets the WhiteboxPluginHost to which the plugin tool is tied. This is the class
     * that the plugin will send all feedback messages, progress updates, and return objects.
     * @param host The WhiteboxPluginHost that called the plugin tool.
     */  
    @Override
    public void setPluginHost(WhiteboxPluginHost host) {
        myHost = host;
    }
    /**
     * Used to communicate feedback pop-up messages between a plugin tool and the main Whitebox user-interface.
     * @param feedback String containing the text to display.
     */
    private void showFeedback(String message) {
        if (myHost != null) {
            myHost.showFeedback(message);
        } else {
            System.out.println(message);
        }
    }
    /**
     * Used to communicate a return object from a plugin tool to the main Whitebox user-interface.
     * @return Object, such as an output WhiteboxRaster.
     */
    private void returnData(Object ret) {
        if (myHost != null) {
            myHost.returnData(ret);
        }
    }

    private int previousProgress = 0;
    private String previousProgressLabel = "";
    /**
     * Used to communicate a progress update between a plugin tool and the main Whitebox user interface.
     * @param progressLabel A String to use for the progress label.
     * @param progress Float containing the progress value (between 0 and 100).
     */
    private void updateProgress(String progressLabel, int progress) {
        if (myHost != null && ((progress != previousProgress) || 
                (!progressLabel.equals(previousProgressLabel)))) {
            myHost.updateProgress(progressLabel, progress);
        }
        previousProgress = progress;
        previousProgressLabel = progressLabel;
    }
    /**
     * Used to communicate a progress update between a plugin tool and the main Whitebox user interface.
     * @param progress Float containing the progress value (between 0 and 100).
     */
    private void updateProgress(int progress) {
        if (myHost != null && progress != previousProgress) {
            myHost.updateProgress(progress);
        }
        previousProgress = progress;
    }
    /**
     * Sets the arguments (parameters) used by the plugin.
     * @param args 
     */
    @Override
    public void setArgs(String[] args) {
        this.args = args.clone();
    }
    
    private boolean cancelOp = false;
    /**
     * Used to communicate a cancel operation from the Whitebox GUI.
     * @param cancel Set to true if the plugin should be canceled.
     */
    @Override
    public void setCancelOp(boolean cancel) {
        cancelOp = cancel;
    }
    
    private void cancelOperation() {
        showFeedback("Operation cancelled.");
        updateProgress("Progress: ", 0);
    }
    
    private boolean amIActive = false;
    /**
     * Used by the Whitebox GUI to tell if this plugin is still running.
     * @return a boolean describing whether or not the plugin is actively being used.
     */
    @Override
    public boolean isActive() {
        return amIActive;
    }

    @Override
    public void run() {
        amIActive = true;
        
        String inputHeaderRed = null;
        String inputHeaderGreen = null;
        String inputHeaderBlue = null;
        String inputHeaderAlpha = null;
        String outputHeader = null;
        boolean alphaChannelSpecified = true; 
        
        if (args.length <= 0) {
            showFeedback("Plugin parameters have not been set.");
            return;
        }
        
        for (int i = 0; i < args.length; i++) {
    		if (i == 0) {
                    inputHeaderRed = args[i];
                } else if (i == 1) {
                    inputHeaderGreen = args[i];
                } else if (i == 2) {
                    inputHeaderBlue = args[i];
                } else if (i == 3) {
                    inputHeaderAlpha = args[i];
                    if (inputHeaderAlpha.toLowerCase().contains("not specified")) {
                        alphaChannelSpecified = false;
                    }
                } else if (i == 4) {
                    outputHeader = args[i];
                }
    	}

        // check to see that the inputHeader and outputHeader are not null.
       if ((inputHeaderRed == null) || (inputHeaderGreen == null) || 
                (inputHeaderBlue == null) || (outputHeader == null)) {
           showFeedback("One or more of the input parameters have not been set properly.");
           return;
       }

        try {
            int row, col;
            double redVal, greenVal, blueVal, alphaVal;
            double redRange, greenRange, blueRange;
            double redMin, greenMin, blueMin;
            int r, g, b, a;
            double z;
            float progress = 0;
            
            WhiteboxRasterInfo red = new WhiteboxRasterInfo(inputHeaderRed);
            
            int rows = red.getNumberRows();
            int cols = red.getNumberColumns();

            WhiteboxRasterInfo green = new WhiteboxRasterInfo(inputHeaderGreen);
            if ((green.getNumberRows() != rows) || (green.getNumberColumns() != cols)) {
                showFeedback("All input images must have the same dimensions.");
                return;
            }
            WhiteboxRasterInfo blue = new WhiteboxRasterInfo(inputHeaderBlue);
            if ((blue.getNumberRows() != rows) || (blue.getNumberColumns() != cols)) {
                showFeedback("All input images must have the same dimensions.");
                return;
            }

            double noData = red.getNoDataValue();

            WhiteboxRaster outputFile = new WhiteboxRaster(outputHeader, "rw", 
                    inputHeaderRed, WhiteboxRaster.DataType.FLOAT, noData);
            outputFile.setPreferredPalette("rgb.pal");
            outputFile.setDataScale(WhiteboxRaster.DataScale.RGB);

            redMin = red.getDisplayMinimum();
            greenMin = green.getDisplayMinimum();
            blueMin = blue.getDisplayMinimum();

            redRange = red.getDisplayMaximum() - redMin;
            greenRange = green.getDisplayMaximum() - greenMin;
            blueRange = blue.getDisplayMaximum() - blueMin;

            if (!alphaChannelSpecified) {
                double[] dataRed, dataGreen, dataBlue;
                for (row = 0; row < rows; row++) {
                    dataRed = red.getRowValues(row);
                    dataGreen = green.getRowValues(row);
                    dataBlue = blue.getRowValues(row);
                    for (col = 0; col < cols; col++) {
                        redVal = dataRed[col];
                        greenVal = dataGreen[col];
                        blueVal = dataBlue[col];
                        if ((redVal != noData) && (greenVal != noData) && (blueVal != noData)) {
                            r = (int) ((redVal - redMin) / redRange * 255);
                            if (r < 0) {
                                r = 0;
                            }
                            if (r > 255) {
                                r = 255;
                            }
                            g = (int) ((greenVal - greenMin) / greenRange * 255);
                            if (g < 0) {
                                g = 0;
                            }
                            if (g > 255) {
                                g = 255;
                            }
                            b = (int) ((blueVal - blueMin) / blueRange * 255);
                            if (b < 0) {
                                b = 0;
                            }
                            if (b > 255) {
                                b = 255;
                            }
                            z = (double) ((255 << 24) | (b << 16) | (g << 8) | r);
                            outputFile.setValue(row, col, z);
                        } else {
                            outputFile.setValue(row, col, noData);
                        }

                    }
                    if (cancelOp) {
                        cancelOperation();
                        return;
                    }
                    progress = (float) (100f * row / (rows - 1));
                    updateProgress((int) progress);
                }
                
                outputFile.addMetadataEntry("Created by the "
                    + getDescriptiveName() + " tool.");
                outputFile.addMetadataEntry("Created on " + new Date());

                outputFile.close();
                red.close();
                green.close();
                blue.close();
                
            } else {
                WhiteboxRaster alpha = new WhiteboxRaster(inputHeaderAlpha, "r");
                if ((alpha.getNumberRows() != rows) || (alpha.getNumberColumns() != cols)) {
                    showFeedback("All input images must have the same dimensions.");
                    return;
                }
                double[] dataRed, dataGreen, dataBlue, dataAlpha;
                double alphaMin, alphaRange;
                alphaMin = alpha.getDisplayMinimum();
                alphaRange = alpha.getDisplayMaximum() - alphaMin;
                for (row = 0; row < rows; row++) {
                    dataRed = red.getRowValues(row);
                    dataGreen = green.getRowValues(row);
                    dataBlue = blue.getRowValues(row);
                    dataAlpha = alpha.getRowValues(row);
                    for (col = 0; col < cols; col++) {
                        redVal = dataRed[col];
                        greenVal = dataGreen[col];
                        blueVal = dataBlue[col];
                        alphaVal = dataAlpha[col];
                        if ((redVal != noData) && (greenVal != noData) && (blueVal != noData)) {
                            r = (int) ((redVal - redMin) / redRange * 255);
                            if (r < 0) {
                                r = 0;
                            }
                            if (r > 255) {
                                r = 255;
                            }
                            g = (int) ((greenVal - greenMin) / greenRange * 255);
                            if (g < 0) {
                                g = 0;
                            }
                            if (g > 255) {
                                g = 255;
                            }
                            b = (int) ((blueVal - blueMin) / blueRange * 255);
                            if (b < 0) {
                                b = 0;
                            }
                            if (b > 255) {
                                b = 255;
                            }
                            a = (int) ((alphaVal - alphaMin) / alphaRange * 255);
                            if (a < 0) {
                                a = 0;
                            }
                            if (a > 255) {
                                a = 255;
                            }
                            z = (a << 24) | (b << 16) | (g << 8) | r;
                            outputFile.setValue(row, col, z);
                        } else {
                            outputFile.setValue(row, col, noData);
                        }

                    }
                    if (cancelOp) {
                        cancelOperation();
                        return;
                    }
                    progress = (float) (100f * row / (rows - 1));
                    updateProgress((int) progress);
                }
                
                outputFile.addMetadataEntry("Created by the "
                    + getDescriptiveName() + " tool.");
                outputFile.addMetadataEntry("Created on " + new Date());

                outputFile.close();
                red.close();
                green.close();
                blue.close();
                alpha.close();
            }

            // returning a header file string displays the image.
            returnData(outputHeader);
            
        } catch (OutOfMemoryError oe) {
            myHost.showFeedback("An out-of-memory error has occurred during operation.");
        } catch (Exception e) {
            myHost.showFeedback("An error has occurred during operation. See log file for details.");
            myHost.logException("Error in " + getDescriptiveName(), e);
        } finally {
            updateProgress("Progress: ", 0);
            // tells the main application that this process is completed.
            amIActive = false;
            myHost.pluginComplete();
        }
    }
}
