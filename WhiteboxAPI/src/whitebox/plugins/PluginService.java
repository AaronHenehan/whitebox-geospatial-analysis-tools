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
package whitebox.plugins;

import whitebox.interfaces.WhiteboxPlugin;
import java.util.Iterator;
import java.util.ArrayList;
import whitebox.structures.InteroperableGeospatialDataFormat;

/**
 *
 * @author Dr. John Lindsay <jlindsay@uoguelph.ca>
 */
public interface PluginService {
    Iterator<WhiteboxPlugin> getPlugins();
    void initPlugins();
    WhiteboxPlugin getPlugin(String pluginName, int nameType);
    int getNumberOfPlugins();
    ArrayList getPluginList();
    public ArrayList<InteroperableGeospatialDataFormat> getInteroperableDataFormats();
}
