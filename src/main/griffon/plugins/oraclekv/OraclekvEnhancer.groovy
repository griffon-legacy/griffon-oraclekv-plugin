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

package griffon.plugins.oraclekv

import griffon.util.CallableWithArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
final class OraclekvEnhancer {
    private static final Logger LOG = LoggerFactory.getLogger(OraclekvEnhancer)

    private OraclekvEnhancer() {}

    static void enhance(MetaClass mc, OraclekvProvider provider = OraclekvStoreHolder.instance) {
        if(LOG.debugEnabled) LOG.debug("Enhancing $mc with $provider")
        mc.withOraclekv = {Closure closure ->
            provider.withOraclekv('default', closure)
        }
        mc.withOraclekv << {String storeName, Closure closure ->
            provider.withOraclekv(storeName, closure)
        }
        mc.withOraclekv << {CallableWithArgs callable ->
            provider.withOraclekv('default', callable)
        }
        mc.withOraclekv << {String storeName, CallableWithArgs callable ->
            provider.withOraclekv(storeName, callable)
        }
    }
}
