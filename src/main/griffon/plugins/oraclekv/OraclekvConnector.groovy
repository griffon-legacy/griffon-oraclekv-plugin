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

import oracle.kv.KVStore
import oracle.kv.KVStoreConfig
import oracle.kv.KVStoreFactory

import griffon.core.GriffonApplication
import griffon.util.Environment
import griffon.util.Metadata
import griffon.util.CallableWithArgs
import griffon.util.ConfigUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
@Singleton
final class OraclekvConnector implements OraclekvProvider {
    private bootstrap

    private static final Logger LOG = LoggerFactory.getLogger(OraclekvConnector)

    Object withOraclekv(String storeName = 'default', Closure closure) {
        OraclekvStoreHolder.instance.withOraclekv(storeName, closure)
    }

    public <T> T withOraclekv(String storeName = 'default', CallableWithArgs<T> callable) {
        return OraclekvStoreHolder.instance.withOraclekv(storeName, callable)
    }

    // ======================================================

    ConfigObject createConfig(GriffonApplication app) {
        ConfigUtils.loadConfigWithI18n('OraclekvConfig')
    }

    private ConfigObject narrowConfig(ConfigObject config, String storeName) {
        return storeName == 'default' ? config.store : config.stores[storeName]
    }

    KVStore connect(GriffonApplication app, ConfigObject config, String storeName = 'default') {
        if (OraclekvStoreHolder.instance.isStoreConnected(storeName)) {
            return OraclekvStoreHolder.instance.getStore(storeName)
        }

        config = narrowConfig(config, storeName)
        app.event('OraclekvConnectStart', [config, storeName])
        KVStore store = startOraclekv(storeName, config)
        OraclekvStoreHolder.instance.setStore(storeName, store)
        bootstrap = app.class.classLoader.loadClass('BootstrapOraclekv').newInstance()
        bootstrap.metaClass.app = app
        bootstrap.init(storeName, store)
        app.event('OraclekvConnectEnd', [storeName, store])
        store
    }

    void disconnect(GriffonApplication app, ConfigObject config, String storeName = 'default') {
        if (OraclekvStoreHolder.instance.isStoreConnected(storeName)) {
            config = narrowConfig(config, storeName)
            KVStore store = OraclekvStoreHolder.instance.getStore(storeName)
            app.event('OraclekvDisconnectStart', [config, storeName, store])
            bootstrap.destroy(storeName, store)
            stopOraclekv(config, store)
            app.event('OraclekvDisconnectEnd', [config, storeName])
            OraclekvStoreHolder.instance.disconnectStore(storeName)
        }
    }

    private KVStore startOraclekv(String storeName, ConfigObject config) {
        String store = storeName == 'default'? config.name : storeName
        String host  = config.host ?: 'localhost'
        def port     = config.port ?: 5000

        KVStoreFactory.getStore(new KVStoreConfig(store, host + ":" + port))
    }

    private void stopOraclekv(ConfigObject config, KVStore store) {
        store.close()
    }
}
