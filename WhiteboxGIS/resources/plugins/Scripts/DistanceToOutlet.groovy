/*
 * Copyright (C) 2014 Dr. John Lindsay <jlindsay@uoguelph.ca>
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
 
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util.Date
import java.util.ArrayList
import whitebox.interfaces.WhiteboxPluginHost
import whitebox.geospatialfiles.WhiteboxRaster
import whitebox.geospatialfiles.WhiteboxRasterInfo
import whitebox.geospatialfiles.WhiteboxRasterBase.DataType
import whitebox.geospatialfiles.WhiteboxRasterBase.DataScale
import whitebox.geospatialfiles.ShapeFile
import whitebox.geospatialfiles.shapefile.*
import whitebox.ui.plugin_dialog.ScriptDialog
import whitebox.utilities.StringUtilities
import groovy.transform.CompileStatic

// The following four variables are required for this 
// script to be integrated into the tool tree panel. 
// Comment them out if you want to remove the script.
def name = "DistanceToOutlet"
def descriptiveName = "Distance to Outlet"
def description = "Calculates the distance of stream grid cells to the channel network outlet cell."
def toolboxes = ["StreamAnalysis"]

public class DistanceToOutlet implements ActionListener {
	private WhiteboxPluginHost pluginHost
	private ScriptDialog sd;
	private String descriptiveName
	
	public DistanceToOutlet(WhiteboxPluginHost pluginHost, 
		String[] args, String name, String descriptiveName) {
		this.pluginHost = pluginHost
		this.descriptiveName = descriptiveName
			
		if (args.length > 0) {
			execute(args)
		} else {
			// Create a dialog for this tool to collect user-specified
			// tool parameters.
		 	sd = new ScriptDialog(pluginHost, descriptiveName, this)	
		
			// Specifying the help file will display the html help
			// file in the help pane. This file should be be located 
			// in the help directory and have the same name as the 
			// class, with an html extension.
			sd.setHelpFile(name)
		
			// Specifying the source file allows the 'view code' 
			// button on the tool dialog to be displayed.
			def pathSep = File.separator
			def scriptFile = pluginHost.getResourcesDirectory() + "plugins" + pathSep + "Scripts" + pathSep + name + ".groovy"
			sd.setSourceFile(scriptFile)
			
			// add some components to the dialog
			sd.addDialogFile("Input D8 flow pointer raster", "Input D8 Pointer Raster:", "open", "Raster Files (*.dep), DEP", true, false)
            sd.addDialogFile("Input streams raster", "Input Streams Raster:", "open", "Raster Files (*.dep), DEP", true, false)
            sd.addDialogFile("Output file", "Output Raster File:", "save", "Raster Files (*.dep), DEP", true, false)
			sd.addDialogCheckBox("Use NoData for background value?", "Use NoData for background value?", false)
	
			// resize the dialog to the standard size and display it
			sd.setSize(800, 400)
			sd.visible = true
		}
	}

	// The CompileStatic annotation can be used to significantly
	// improve the performance of a Groovy script to nearly 
	// that of native Java code.
	@CompileStatic
	private void execute(String[] args) {
		try {
	  		int progress, oldProgress, cN, rN, c
	  		int[] dX = [ 1, 1, 1, 0, -1, -1, -1, 0 ]
			int[] dY = [ -1, 0, 1, 1, 1, 0, -1, -1 ]
			double[] inflowingVals = [ 16, 32, 64, 128, 1, 2, 4, 8 ]
        	double flowDir
        	double val
        	boolean flag
        	double outputValue = 1.0
        	final double LnOf2 = 0.693147180559945;
        	
			if (args.length != 4) {
				pluginHost.showFeedback("Incorrect number of arguments given to tool.")
				return
			}
			// read the input parameters
			String pointerFile = args[0]
			String inputStreamsFile = args[1]
			String outputFile = args[2]
			
			WhiteboxRaster pointer = new WhiteboxRaster(pointerFile, "r")
			double pointerNoData = pointer.getNoDataValue()
			double backgroundVal = 0.0
			if (Boolean.parseBoolean(args[3])) {
				backgroundVal = pointerNoData
			}
			
			int rows = pointer.getNumberRows()
			int cols = pointer.getNumberColumns()
			double gridResX = pointer.getCellSizeX();
            double gridResY = pointer.getCellSizeY();
            double diagGridRes = Math.sqrt(gridResX * gridResX + gridResY * gridResY);
            double[] gridLengths = [diagGridRes, gridResX, diagGridRes, gridResY, diagGridRes, gridResX, diagGridRes, gridResY]
            
			WhiteboxRaster streams = new WhiteboxRaster(inputStreamsFile, "r")
			double streamsNoData = streams.getNoDataValue()
			if (rows != streams.getNumberRows() ||
			  cols != streams.getNumberColumns()) {
				pluginHost.showFeedback("Error: The input files must have the same dimensions.")
				return;
			}
			
			WhiteboxRaster output = new WhiteboxRaster(outputFile, "rw", 
  		  	     pointerFile, DataType.FLOAT, pointerNoData)
			output.setDataScale(DataScale.CONTINUOUS)
			output.setPreferredPalette("spectrum_black_background.plt")

			// Find the channel heads.
			ArrayList<Integer> channelHeadsRows = new ArrayList<>()
			ArrayList<Integer> channelHeadsCols = new ArrayList<>()
			
			oldProgress = -1
	  		for (int row in 0..(rows - 1)) {
	  			for (int col in 0..(cols - 1)) {
	  				val = streams.getValue(row, col)
	  				if (val != streamsNoData && val != 0 &&
	  				  pointer.getValue(row, col) != pointerNoData) {
	  					int n = 0;
	                    for (int i = 0; i < 8; i++) {
	                    	rN = row + dY[i]
	                    	cN = col + dX[i]
	                    	val = streams.getValue(rN, cN)
	                    	if (val != 0 && val != streamsNoData &&
	                    	  pointer.getValue(rN, cN) == inflowingVals[i]) { 
	                    	  	n++; 
	                    	}
	                    }
	
	                    if (n == 0) {
	                    	// it's a channel head
	                    	channelHeadsRows.add(row)
	                    	channelHeadsCols.add(col)
	                    }
	  				}
	  			}
	  			progress = (int)(100f * row / (rows - 1))
				if (progress > oldProgress) {
					pluginHost.updateProgress(progress)
					oldProgress = progress
					// check to see if the user has requested a cancellation
					if (pluginHost.isRequestForOperationCancelSet()) {
						pluginHost.showFeedback("Operation cancelled")
						return
					}
				}
	  		}

			double pntrVal, dist, flowpathDist, outputVal
			int numChannelHeads = channelHeadsRows.size()
			oldProgress = -1
  		  	for (int i = 0; i < numChannelHeads; i++) {
  		  		cN = channelHeadsCols.get(i)
                rN = channelHeadsRows.get(i)
                dist = 0
                flag = false;
                while (!flag) {
                	// find it's downslope neighbour
                    flowDir = pointer.getValue(rN, cN);
                    if (flowDir > 0 && flowDir != pointerNoData) {
                    	//move x and y accordingly
                        c = (int) (Math.log(flowDir) / LnOf2);
                        cN += dX[c];
                        rN += dY[c];
                        //if the new cell already has a value in the output, use that value
                        if (output.getValue(rN, cN) != pointerNoData) {
                        	dist += gridLengths[c] + output.getValue(rN, cN)
                        	flag = true
                        } else {
                        	dist += gridLengths[c]
                        }
                    } else {
                    	// edge of grid or cell with undefined flow...don't do anything
                        flag = true;
                    }
                }

                cN = channelHeadsCols.get(i)
                rN = channelHeadsRows.get(i)
                flag = false;
                while (!flag) {
                	output.setValue(rN, cN, dist)
                	// find it's downslope neighbour
                    flowDir = pointer.getValue(rN, cN);
                    if (flowDir > 0 && flowDir != pointerNoData) {
                    	//move x and y accordingly
                        c = (int) (Math.log(flowDir) / LnOf2);
                        cN += dX[c];
                        rN += dY[c];
                        //if the new cell already has a value in the output, use that value
                        if (output.getValue(rN, cN) != pointerNoData) {
                        	flag = true
                        } else {
                        	dist -= gridLengths[c]
                        }
                    } else {
                    	// edge of grid or cell with undefined flow...don't do anything
                        flag = true;
                    }
                }
                
  		  		progress = (int)(100f * i / (numChannelHeads - 1))
				if (progress > oldProgress) {
					pluginHost.updateProgress(progress)
					oldProgress = progress
					// check to see if the user has requested a cancellation
					if (pluginHost.isRequestForOperationCancelSet()) {
						pluginHost.showFeedback("Operation cancelled")
						return
					}
				}
  		  	}
  		  	
			pointer.close()
			streams.close()
			output.addMetadataEntry("Created by the "
	                    + descriptiveName + " tool.")
	        output.addMetadataEntry("Created on " + new Date())
			output.close()
	
			// display the output image
			pluginHost.returnData(outputFile)
	
		} catch (OutOfMemoryError oe) {
            pluginHost.showFeedback("An out-of-memory error has occurred during operation.")
	    } catch (Exception e) {
	        pluginHost.showFeedback("An error has occurred during operation. See log file for details.")
	        pluginHost.logException("Error in " + descriptiveName, e)
        } finally {
        	// reset the progress bar
        	pluginHost.updateProgress(0)
        }
	}
	
	@Override
    public void actionPerformed(ActionEvent event) {
    	if (event.getActionCommand().equals("ok")) {
    		final def args = sd.collectParameters()
			sd.dispose()
			final Runnable r = new Runnable() {
            	@Override
            	public void run() {
                	execute(args)
            	}
        	}
        	final Thread t = new Thread(r)
        	t.start()
    	}
    }
}

if (args == null) {
	pluginHost.showFeedback("Plugin arguments not set.")
} else {
	def tdf = new DistanceToOutlet(pluginHost, args, name, descriptiveName)
}
