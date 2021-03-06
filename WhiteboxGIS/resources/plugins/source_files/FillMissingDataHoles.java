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
import java.util.List;
import whitebox.geospatialfiles.WhiteboxRaster;
import whitebox.interfaces.WhiteboxPlugin;
import whitebox.interfaces.WhiteboxPluginHost;
import whitebox.structures.KdTree;

/**
 * WhiteboxPlugin is used to define a plugin tool for Whitebox GIS.
 * @author Dr. John Lindsay <jlindsay@uoguelph.ca>
 */
public class FillMissingDataHoles implements WhiteboxPlugin {
    
    private WhiteboxPluginHost myHost = null;
    private String[] args;
    /**
     * Used to retrieve the plugin tool's name. This is a short, unique name containing no spaces.
     * @return String containing plugin name.
     */
    @Override
    public String getName() {
        return "FillMissingDataHoles";
    }
    /**
     * Used to retrieve the plugin tool's descriptive name. This can be a longer name (containing spaces) and is used in the interface to list the tool.
     * @return String containing the plugin descriptive name.
     */
    @Override
    public String getDescriptiveName() {
    	return "Fill Missing Data Holes";
    }
    /**
     * Used to retrieve a short description of what the plugin tool does.
     * @return String containing the plugin's description.
     */
    @Override
    public String getToolDescription() {
    	return "Fills NoData holes in an image or DEM by linear interpolation.";
    }
     /**
     * Used to identify which toolboxes this plugin tool should be listed in.
     * @return Array of Strings.
     */
    @Override
    public String[] getToolbox() {
    	String[] ret = { "ImageEnhancement", "TerrainAnalysis", "LidarTools" };
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
        
        String inputHeader = null;
        String outputHeader = null;
        int row, col, x, y;
        int progress = 0;
        double z, val;
        int i;
        int[] dX = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] dY = {-1, 0, 1, 1, 1, 0, -1, -1};
        
        if (args.length <= 0) {
            showFeedback("Plugin parameters have not been set.");
            return;
        }
        
        inputHeader = args[0];
        outputHeader = args[1];
        

        // check to see that the inputHeader and outputHeader are not null.
        if ((inputHeader == null) || (outputHeader == null)) {
            showFeedback("One or more of the input parameters have not been set properly.");
            return;
        }

        try {
            WhiteboxRaster image = new WhiteboxRaster(inputHeader, "r");
            image.isReflectedAtEdges = true;
            int rows = image.getNumberRows();
            int cols = image.getNumberColumns();
            double noData = image.getNoDataValue();
            
            WhiteboxRaster output = new WhiteboxRaster(outputHeader, "rw", inputHeader, WhiteboxRaster.DataType.FLOAT, noData);
            
            // flag the noData cells in the output image
            for (row = 0; row < rows; row++) {
                for (col = 0; col < cols; col++) {
                    z = image.getValue(row, col);
                    if (z == noData) {
                        output.setValue(row, col, 1);
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * row / (rows - 1));
                updateProgress("Loop 1 of 6: ", progress);
            }
            
            int[] scanFilter = new int[]{6, 7, 0, 5};
            
            for (row = 0; row < rows; row++) { // west to east
                for (col = 0; col < cols; col++) {
                    z = output.getValue(row, col);
                    if (z == 1) {
                        for (int a = 0; a < 4; a++) {
                            x = col + dX[scanFilter[a]];
                            y = row + dY[scanFilter[a]];
                            z = output.getValue(y, x);
                            if (z == -1 || x < 0 || x >= cols || y < 0 || y >= rows) {
                                output.setValue(row, col, -1);
                            }
                        }
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * row / (rows - 1));
                updateProgress("Loop 2 of 6: ", progress);
            }
            
            scanFilter = new int[]{4, 3, 2, 1};
            
            for (row = (rows - 1); row >= 0; row--) { // west to east
                for (col = 0; col < cols; col++) {
                    z = output.getValue(row, col);
                    if (z == 1) {
                        for (int a = 0; a < 4; a++) {
                            x = col + dX[scanFilter[a]];
                            y = row + dY[scanFilter[a]];
                            z = output.getValue(y, x);
                            if (z == -1 || x < 0 || x >= cols || y < 0 || y >= rows) {
                                output.setValue(row, col, -1);
                            }
                        }
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * 1 - row / (rows - 1));
                updateProgress("Loop 3 of 6: ", progress);
            }

 
            // Find all cells that border with no-data cells. 
            int k = 0;
            boolean neighboursNoData = false;
            for (row = 0; row < rows; row++) {
                for (col = 0; col < cols; col++) {
                    z = image.getValue(row, col);
                    if (z != noData) {
                        neighboursNoData = false;
                        for (i = 0; i < 8; i++) {
                            if (output.getValue(row + dY[i], col + dX[i]) == 1) {
                                neighboursNoData = true;
                                break;
                            }
                        }
                        if (neighboursNoData) {
                            k++;
                            //double[] entry = { row, col };
                            //tree.addPoint(entry, z);
                        }
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * row / (rows - 1));
                updateProgress("Loop 4 of 6: ", progress);
            }
            
            KdTree<Double> tree = new KdTree.SqrEuclid<Double>(2, k);
            
            for (row = 0; row < rows; row++) {
                for (col = 0; col < cols; col++) {
                    z = image.getValue(row, col);
                    if (z != noData) {
                        neighboursNoData = false;
                        for (i = 0; i < 8; i++) {
                            if (output.getValue(row + dY[i], col + dX[i]) == 1) {
                                neighboursNoData = true;
                                break;
                            }
                        }
                        if (neighboursNoData) {
                            double[] entry = { row, col };
                            tree.addPoint(entry, z);
                        }
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * row / (rows - 1));
                updateProgress("Loop 5 of 6: ", progress);
            }

            List<KdTree.Entry<Double>> results;
            //double[] entry;
            double sumWeights;
            for (row = 0; row < rows; row++) {
                for (col = 0; col < cols; col++) {
                    z = output.getValue(row, col);
                    if (z == 1) {
                        double[] entry = { row, col };
                        results = tree.nearestNeighbor(entry, 6, true);
                        sumWeights = 0;
                        for (i = 0; i < results.size(); i++) {
                            sumWeights += 1 / (results.get(i).distance);
                        }
                        val = 0;
                        for (i = 0; i < results.size(); i++) {
                            val += (1 / (results.get(i).distance)) / sumWeights * results.get(i).value;
                        }
                        output.setValue(row, col, val);
                    } else {
                        output.setValue(row, col, image.getValue(row, col));
                    }
                }
                if (cancelOp) {
                    cancelOperation();
                    return;
                }
                progress = (int) (100f * row / (rows - 1));
                updateProgress("Loop 6 of 6: ", progress);
            }
            
            output.addMetadataEntry("Created by the "
                    + getDescriptiveName() + " tool.");
            output.addMetadataEntry("Created on " + new Date());
            
            image.close();
            output.close();
            
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