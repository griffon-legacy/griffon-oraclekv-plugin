/*
    griffon-oraclekv plugin
    Copyright (C) 2012 Andres Almiray

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import griffon.core.GriffonApplication
import griffon.plugins.oraclekv.OraclekvConnector
import griffon.plugins.oraclekv.OraclekvEnhancer

/**
 * @author Andres Almiray
 */
class OraclekvGriffonAddon {
    void addonInit(GriffonApplication app) {
        ConfigObject config = OraclekvConnector.instance.createConfig(app)
        OraclekvConnector.instance.connect(app, config)
    }

    def events = [
        ShutdownStart: { app ->
            ConfigObject config = OraclekvConnector.instance.createConfig(app)
            OraclekvConnector.instance.disconnect(app, config)
        },
        NewInstance: { klass, type, instance ->
            def types = app.config.griffon?.oraclekv?.injectInto ?: ['controller']
            if(!types.contains(type)) return
            def mc = app.artifactManager.findGriffonClass(klass).metaClass
            OraclekvEnhancer.enhance(mc)
        }
    ]
}
