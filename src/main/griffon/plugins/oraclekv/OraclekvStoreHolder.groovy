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

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import griffon.util.CallableWithArgs
import static griffon.util.GriffonNameUtils.isBlank

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
@Singleton
class OraclekvStoreHolder {
    private static final Logger LOG = LoggerFactory.getLogger(OraclekvStoreHolder)
    private static final Object[] LOCK = new Object[0]
    private final Map<String, KVStore> stores = [:]
  
    String[] getStoreNames() {
        List<String> storeNames = new ArrayList().addAll(stores.keySet())
        storeNames.toArray(new String[storeNames.size()])
    }

    KVStore getStore(String storeName = 'default') {
        if(isBlank(storeName)) storeName = 'default'
        retrieveStore(storeName)
    }

    void setStore(String storeName = 'default', KVStore store) {
        if(isBlank(storeName)) storeName = 'default'
        storeStore(storeName, store)       
    }

    Object withOraclekv(String storeName = 'default', Closure closure) {
        KVStore store = fetchStore(storeName)
        if(LOG.debugEnabled) LOG.debug("Executing statement on store '$storeName'")
        return closure(storeName, store)
    }

    public <T> T withOraclekv(String storeName = 'default', CallableWithArgs<T> callable) {
        KVStore store = fetchStore(storeName)
        if(LOG.debugEnabled) LOG.debug("Executing statement on store '$storeName'")
        callable.args = [storeName, store] as Object[]
        return callable.call()
    }
    
    boolean isStoreConnected(String storeName) {
        if(isBlank(storeName)) storeName = 'default'
        retrieveStore(storeName) != null
    }
    
    void disconnectStore(String storeName) {
        if(isBlank(storeName)) storeName = 'default'
        storeStore(storeName, null)        
    }

    private KVStore fetchStore(String storeName) {
        if(isBlank(storeName)) storeName = 'default'
        KVStore store = retrieveStore(storeName)
        if(store == null) {
            GriffonApplication app = ApplicationHolder.application
            ConfigObject config = OraclekvConnector.instance.createConfig(app)
            store = OraclekvConnector.instance.connect(app, config, storeName)
        }
        
        if(store == null) {
            throw new IllegalArgumentException("No such KVStore configuration for name $storeName")
        }
        store
    }

    private KVStore retrieveStore(String storeName) {
        synchronized(LOCK) {
            stores[storeName]
        }
    }

    private void storeStore(String storeName, KVStore store) {
        synchronized(LOCK) {
            stores[storeName] = store
        }
    }
}
