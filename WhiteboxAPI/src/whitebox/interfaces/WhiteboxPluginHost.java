/*
 * Copyright (C) 2012 Dr. John Lindsay <jlindsay@uoguelph.ca>
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

package whitebox.interfaces;

import java.util.List;
import java.util.ArrayList;
import java.awt.Font;
/**
 *
 * @author Dr. John Lindsay <jlindsay@uoguelph.ca>
 */
public interface WhiteboxPluginHost extends Communicator {
    /**
     * Used to tell the Whitebox GUI to toggle the edit vector tool if the
     * active layer is a vector.
     */
    public void editVector();
    
    public String getLanguageCountryCode();

    public void setLanguageCountryCode(String code);
    
    public List returnPluginList();
    /**
     * Used to cancel any currently running plugin.
     */
    public void cancelOperation();
    
    /**
     * Used to launch a plugin dialog box for retrieval of plugin parameters.
     * @param pluginName String containing the descriptive name of the plugin.
     */
    public void launchDialog(String pluginName);

    /**
     * Used to communicate a return object from a plugin tool to the main Whitebox user-interface.
     * @param ret
     * @return Object, such as an output WhiteboxRaster.
     */
    public void returnData(Object ret);

//    /**
//     * Used to run a plugin through the Host app.
//     * @param pluginName String containing the descriptive name of the plugin.
//     * @param args String array containing the parameters to feed to the plugin.
//     */
//    public void runPlugin(String pluginName, String[] args);
    
    public void pluginComplete();

    /**
     * Used to communicate a progress update between a plugin tool and the main Whitebox user interface.
     * @param progressLabel A String to use for the progress label.
     * @param progress Float containing the progress value (between 0 and 100).
     */
    public void updateProgress(String progressLabel, int progress);

    /**
     * Used to communicate a progress update between a plugin tool and the main Whitebox user interface.
     * @param progress Float containing the progress value (between 0 and 100).
     */
    public void updateProgress(int progress);
    
    /**
     * Used to refresh a displayed map.
     */
    public void refreshMap(boolean updateLayersTab);
    
    /**
     * Used to delete a selected vector feature that is actively being edited.
     */
    public void deleteFeature();
    
    /**
     * Used to delete the last digitized node in a feature.
     */
    public void deleteLastNodeInFeature();
    
    /**
     * The default font.
     * @return  default font
     */
    public Font getDefaultFont();
    
    /**
     * Used to communicate a request to cancel an operation
     */
    public boolean isRequestForOperationCancelSet();
    
    /**
     * Used to ensure that there is no active cancel operation request
     */
    public void resetRequestForOperationCancel();
    
    public void showHelp();
    
    public void showHelp(String helpFile);
    
    /**
     * Used to set the select feature mode of a Whitebox user interface.
     */
    public void setSelectFeature();
    
    /**
     * Used to clear all the selected features from the active vector layer.
     */
    public void deselectAllFeaturesInActiveLayer();
    
    /**
     * Called to save selected features into a separate vector file.
     */
    public void saveSelection();
    
    /**
     * Used to retrieve the active map layer.
     * @return MapLayer that is currently active.
     */
    public MapLayer getActiveMapLayer();
    
    /**
     * Used to retrieve all the displayed map layers.
     * @return ArrayList of MapLayers
     */
    public ArrayList<MapLayer> getAllMapLayers();
}
